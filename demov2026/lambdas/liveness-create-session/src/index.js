const { createLivenessSession } = require('./services/rekognition');
const { buildResponse } = require('./utils/response');

exports.handler = async (event) => {
  try {
    const sessionId = await createLivenessSession();
    console.log('SessionId:', sessionId);
    return buildResponse(200, sessionId);
  } catch (error) {
    console.log('Error:', error);
    return buildResponse(500, 'Error al obtener el sessionId');
  }
};
