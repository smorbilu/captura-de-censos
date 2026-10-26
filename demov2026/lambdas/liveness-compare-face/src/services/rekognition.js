const {
  RekognitionClient,
  GetFaceLivenessSessionResultsCommand,
  CompareFacesCommand
} = require('@aws-sdk/client-rekognition');

const rekognitionClient = new RekognitionClient({ region: 'us-east-1' });
const CONFIANZA_MINIMA = Number(process.env.LIVENESS_MIN_CONFIDENCE || 80);
const SIMILITUD_MINIMA = Number(process.env.FACE_MIN_SIMILARITY || 90);

const imagenDeReferencia = (referencia) => {
  if (referencia?.Bytes) {
    return { Bytes: referencia.Bytes };
  }
  if (referencia?.S3Object) {
    return { S3Object: referencia.S3Object };
  }
  return null;
};

const compareWithLiveness = async (sessionId, fotoBase64) => {
  const sesion = await rekognitionClient.send(
    new GetFaceLivenessSessionResultsCommand({ SessionId: sessionId })
  );
  const confianza = sesion.Confidence ?? 0;

  if (sesion.Status !== 'SUCCEEDED' || confianza < CONFIANZA_MINIMA) {
    return { livenessSuperado: false, estado: sesion.Status, confianza };
  }

  const origen = imagenDeReferencia(sesion.ReferenceImage);
  if (!origen) {
    throw new Error('La sesion no tiene imagen de referencia');
  }

  const comparacion = await rekognitionClient.send(
    new CompareFacesCommand({
      SourceImage: origen,
      TargetImage: { Bytes: Buffer.from(fotoBase64, 'base64') },
      SimilarityThreshold: SIMILITUD_MINIMA,
      QualityFilter: 'AUTO'
    })
  );

  const mejor = comparacion.FaceMatches?.[0];

  return {
    livenessSuperado: true,
    coincide: Boolean(mejor),
    similitud: mejor?.Similarity ?? 0,
    confianza
  };
};

module.exports = { compareWithLiveness };
