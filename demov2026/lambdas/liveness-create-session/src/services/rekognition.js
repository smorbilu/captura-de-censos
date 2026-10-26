const { RekognitionClient, CreateFaceLivenessSessionCommand } = require('@aws-sdk/client-rekognition');

const rekognitionClient = new RekognitionClient({ region: 'us-east-1' });

const createLivenessSession = async () => {
  const command = new CreateFaceLivenessSessionCommand({});
  const response = await rekognitionClient.send(command);
  return response.SessionId;
};

module.exports = { createLivenessSession };
