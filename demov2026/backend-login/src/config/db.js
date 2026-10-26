const { Pool } = require('pg');
require('dotenv').config();

// Pool de conexiones a PostgreSQL. Se reutiliza en toda la app.
const pool = new Pool({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    database: process.env.DB_NAME,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    ssl: process.env.DB_SSL === 'true' ? { rejectUnauthorized: false } : false,
});

pool.on('error', (err) => {
    console.error('Error inesperado en el cliente de PostgreSQL', err);
});

module.exports = pool;
