const userRepository = require('../repositories/userRepository');

class FotoValidationError extends Error {
    constructor(message) {
        super(message);
        this.name = 'FotoValidationError';
    }
}

class PerfilNoEncontradoError extends Error {
    constructor() {
        super('Usuario no encontrado');
        this.name = 'PerfilNoEncontradoError';
    }
}

const FIRMAS_IMAGEN = [
    [0xff, 0xd8, 0xff],
    [0x89, 0x50, 0x4e, 0x47],
];

function esImagen(foto) {
    return FIRMAS_IMAGEN.some((firma) => firma.every((byte, indice) => foto[indice] === byte));
}

async function guardarFoto(userId, foto) {
    if (!Buffer.isBuffer(foto) || foto.length === 0) {
        throw new FotoValidationError('Debe enviar la foto como una imagen JPEG o PNG');
    }

    if (!esImagen(foto)) {
        throw new FotoValidationError('El archivo enviado no es una imagen válida');
    }

    const guardada = await userRepository.updateFoto(userId, foto);

    if (!guardada) {
        throw new PerfilNoEncontradoError();
    }
}

async function obtenerFoto(userId) {
    const foto = await userRepository.findFotoById(userId);
    return foto ? foto.toString('base64') : null;
}

module.exports = { guardarFoto, obtenerFoto, FotoValidationError, PerfilNoEncontradoError };
