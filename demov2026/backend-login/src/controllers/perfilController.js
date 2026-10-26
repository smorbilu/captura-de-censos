const perfilService = require('../services/perfilService');
const apiResponse = require('../utils/apiResponse');

async function guardarFoto(req, res) {
    try {
        await perfilService.guardarFoto(req.user.userId, req.body);
        return apiResponse.success(res, { message: 'Foto guardada correctamente' });
    } catch (err) {
        if (err instanceof perfilService.FotoValidationError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 400,
            });
        }
        if (err instanceof perfilService.PerfilNoEncontradoError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 404,
            });
        }
        console.error(err);
        return apiResponse.error(res, {
            message: 'Error interno del servidor',
            statusCode: 500,
        });
    }
}

async function obtenerFoto(req, res) {
    try {
        const foto = await perfilService.obtenerFoto(req.user.userId);
        return apiResponse.success(res, {
            message: foto ? 'Foto obtenida correctamente' : 'El usuario todavía no tiene foto',
            body: { foto },
        });
    } catch (err) {
        console.error(err);
        return apiResponse.error(res, {
            message: 'Error interno del servidor',
            statusCode: 500,
        });
    }
}

module.exports = { guardarFoto, obtenerFoto };
