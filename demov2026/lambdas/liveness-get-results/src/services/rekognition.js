const { RekognitionClient, GetFaceLivenessSessionResultsCommand } = require('@aws-sdk/client-rekognition');

const rekognitionClient = new RekognitionClient({ region: 'us-east-1' });
const CONFIANZA_MINIMA = Number(process.env.LIVENESS_MIN_CONFIDENCE || 80);

const getLivenessResults = async (sessionId) => {
  const command = new GetFaceLivenessSessionResultsCommand({ SessionId: sessionId });
  const response = await rekognitionClient.send(command);
  const confianza = response.Confidence ?? 0;

  return {
    sessionId,
    estado: response.Status,
    confianza,
    esReal: response.Status === 'SUCCEEDED' && confianza >= CONFIANZA_MINIMA
  };
};

module.exports = { getLivenessResults };
