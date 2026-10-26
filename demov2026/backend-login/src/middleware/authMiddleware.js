const jwt = require('jsonwebtoken');
const apiResponse = require('../utils/apiResponse');

function autenticar(req, res, next) {
    const header = req.headers.authorization || '';
    const [esquema, token] = header.split(' ');

    if (esquema !== 'Bearer' || !token) {
        return apiResponse.error(res, { message: 'Token no proporcionado', statusCode: 401 });
    }

    try {
        req.user = jwt.verify(token, process.env.JWT_SECRET);
        return next();
    } catch (err) {
        return apiResponse.error(res, { message: 'Token inválido o expirado', statusCode: 401 });
    }
}

function requerirRol(rol) {
    return (req, res, next) => {
        if (!req.user || req.user.rol !== rol) {
            return apiResponse.error(res, { message: 'No tiene permisos para esta acción', statusCode: 403 });
        }
        return next();
    };
}

module.exports = { autenticar, requerirRol };
