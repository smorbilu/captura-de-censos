const pool = require('../config/db');

// Capa de acceso a datos: solo habla con la base de datos, no tiene lógica de negocio.

async function findByUsuarioOrEmail(identificador) {
    const result = await pool.query(
        `SELECT id, usuario, nombre, apellido, email, telefono, rol, password_hash
         FROM users
         WHERE usuario = $1 OR LOWER(email) = LOWER($1)
         ORDER BY (usuario = $1) DESC
         LIMIT 1`,
        [identificador]
    );
    return result.rows[0] || null;
}

async function create({ usuario, nombre, apellido, email, telefono, passwordHash }) {
    const result = await pool.query(
        `INSERT INTO users (usuario, nombre, apellido, email, telefono, password_hash)
         VALUES ($1, $2, $3, $4, $5, $6)
         RETURNING id, usuario, nombre, apellido, email, telefono, rol`,
        [usuario, nombre, apellido, email, telefono, passwordHash]
    );
    return result.rows[0];
}

async function updateFoto(userId, foto) {
    const result = await pool.query('UPDATE users SET foto = $1 WHERE id = $2', [foto, userId]);
    return result.rowCount > 0;
}

async function findFotoById(userId) {
    const result = await pool.query('SELECT foto FROM users WHERE id = $1', [userId]);
    return result.rows[0] ? result.rows[0].foto : null;
}

module.exports = { findByUsuarioOrEmail, create, updateFoto, findFotoById };
