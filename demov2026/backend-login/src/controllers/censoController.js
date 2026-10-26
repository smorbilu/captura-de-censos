const censoService = require('../services/censoService');
const apiResponse = require('../utils/apiResponse');

async function crear(req, res) {
    try {
        const censo = await censoService.crear(req.body, req.user.userId);
        return apiResponse.success(res, {
            message: 'Censo guardado correctamente',
            body: censo,
            statusCode: 201,
        });
    } catch (err) {
        if (err instanceof censoService.CensoValidationError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 400,
            });
        }
        console.error(err);
        return apiResponse.error(res, {
            message: 'Error interno del servidor',
            statusCode: 500,
        });
    }
}

async function listar(req, res) {
    try {
        const censos = await censoService.listar();
        return apiResponse.success(res, {
            message: 'Censos obtenidos correctamente',
            body: censos,
        });
    } catch (err) {
        console.error(err);
        return apiResponse.error(res, {
            message: 'Error interno del servidor',
            statusCode: 500,
        });
    }
}

module.exports = { crear, listar };
