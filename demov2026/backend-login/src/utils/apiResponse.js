/**
 * Formato de respuesta estándar para TODAS las APIs del proyecto.
 * Así cualquier endpoint nuevo que agregues responde siempre igual:
 *
 * {
 *   "standardResponse": { "httpCode": 200, "message": "texto descriptivo" },
 *   "body": { ... } | [ ... ] | null
 * }
 */

function success(res, { message = 'OK', body = null, statusCode = 200 } = {}) {
    return res.status(statusCode).json({
        standardResponse: {
            httpCode: statusCode,
            message,
        },
        body,
    });
}

function error(res, { message = 'Ocurrió un error', statusCode = 400, body = null } = {}) {
    return res.status(statusCode).json({
        standardResponse: {
            httpCode: statusCode,
            message,
        },
        body,
    });
}

module.exports = { success, error };
