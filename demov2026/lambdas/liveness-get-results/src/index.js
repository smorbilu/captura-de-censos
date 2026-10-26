const { getLivenessResults } = require('./services/rekognition');
const { buildResponse } = require('./utils/response');

exports.handler = async (event) => {
  const sessionId =
    event?.pathParameters?.sessionId ||
    event?.queryStringParameters?.sessionId ||
    event?.sessionId;

  if (!sessionId) {
    return buildResponse(400, 'El sessionId es obligatorio');
  }

  try {
    const resultado = await getLivenessResults(sessionId);
    console.log('Resultado:', resultado);
    return buildResponse(200, resultado);
  } catch (error) {
    console.log('Error:', error);
    if (error.name === 'SessionNotFoundException') {
      return buildResponse(404, 'La sesion no existe o expiro');
    }
    return buildResponse(500, 'Error al obtener el resultado');
  }
};
