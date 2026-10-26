const authService = require('../services/authService');
const apiResponse = require('../utils/apiResponse');

// Capa de controlador: recibe el request, llama al servicio,
// y siempre responde con el formato estándar (apiResponse).

async function login(req, res) {
    const { usuario, password } = req.body || {};

    try {
        const result = await authService.login(usuario, password);
        return apiResponse.success(res, {
            message: 'Login exitoso',
            body: result,
        });
    } catch (err) {
        if (err instanceof authService.ValidationError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 400,
            });
        }
        if (err instanceof authService.AuthError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 401,
            });
        }
        console.error(err);
        return apiResponse.error(res, {
            message: 'Error interno del servidor',
            statusCode: 500,
        });
    }
}

async function register(req, res) {
    const { nombre, apellido, email, telefono, password } = req.body || {};

    try {
        const result = await authService.register(nombre, apellido, email, telefono, password);
        return apiResponse.success(res, {
            message: 'Usuario registrado correctamente',
            body: result,
            statusCode: 201,
        });
    } catch (err) {
        if (err instanceof authService.ValidationError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 400,
            });
        }
        if (err instanceof authService.ConflictError) {
            return apiResponse.error(res, {
                message: err.message,
                statusCode: 409,
            });
        }
        console.error(err);
        return apiResponse.error(res, {
            message: 'Error interno del servidor',
            statusCode: 500,
        });
    }
}

module.exports = { login, register };
