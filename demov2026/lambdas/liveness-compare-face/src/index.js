const { compareWithLiveness } = require('./services/rekognition');
const { buildResponse } = require('./utils/response');

const leerBody = (event) => {
  if (event?.sessionId) {
    return event;
  }
  try {
    return event?.body ? JSON.parse(event.body) : null;
  } catch {
    return null;
  }
};

exports.handler = async (event) => {
  const datos = leerBody(event);

  if (!datos || !datos.sessionId || !datos.fotoBase64) {
    return buildResponse(400, 'sessionId y fotoBase64 son obligatorios');
  }

  try {
    const resultado = await compareWithLiveness(datos.sessionId, datos.fotoBase64);

    if (!resultado.livenessSuperado) {
      return buildResponse(403, {
        mensaje: 'La prueba de vida no fue superada',
        estado: resultado.estado,
        confianza: resultado.confianza
      });
    }

    console.log('Comparacion:', resultado);
    return buildResponse(200, {
      coincide: resultado.coincide,
      similitud: resultado.similitud,
      confianzaLiveness: resultado.confianza
    });
  } catch (error) {
    console.log('Error:', error);
    if (error.name === 'SessionNotFoundException') {
      return buildResponse(404, 'La sesion no existe o expiro');
    }
    if (error.name === 'InvalidParameterException') {
      return buildResponse(400, 'No se detecto un rostro en la imagen');
    }
    return buildResponse(500, 'Error al comparar el rostro');
  }
};
