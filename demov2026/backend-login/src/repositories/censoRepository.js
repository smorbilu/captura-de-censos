const pool = require('../config/db');

const COLUMNAS = `
    c.id,
    c.nombre,
    c.apellido,
    c.total_miembros AS "totalMiembros",
    c.cantidad_televisores AS "cantidadTelevisores",
    c.latitud,
    c.longitud,
    c.origen,
    c.created_at AS "creadoEn"`;

async function create({ nombre, apellido, totalMiembros, cantidadTelevisores, latitud, longitud, censistaId }) {
    const result = await pool.query(
        `INSERT INTO censos AS c
            (nombre, apellido, total_miembros, cantidad_televisores, latitud, longitud, origen, censista_id)
         VALUES ($1, $2, $3, $4, $5, $6, 'normal', $7)
         RETURNING ${COLUMNAS}`,
        [nombre, apellido, totalMiembros, cantidadTelevisores, latitud, longitud, censistaId]
    );
    return result.rows[0];
}

async function findAll() {
    const result = await pool.query(
        `SELECT ${COLUMNAS},
                TRIM(CONCAT(u.nombre, ' ', u.apellido)) AS censista
         FROM censos c
         LEFT JOIN users u ON u.id = c.censista_id
         ORDER BY c.id DESC`
    );
    return result.rows;
}

module.exports = { create, findAll };
