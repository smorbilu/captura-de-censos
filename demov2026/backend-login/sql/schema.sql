-- Ejecutar este script dentro de la base de datos "login_db" (o el nombre que definas en .env)
-- Ejemplo: psql -U postgres -d login_db -f sql/schema.sql

DROP TABLE IF EXISTS censos;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(30) NOT NULL DEFAULT '',
    rol VARCHAR(20) NOT NULL DEFAULT 'censista' CHECK (rol IN ('censista', 'administrador')),
    password_hash VARCHAR(255) NOT NULL,
    foto BYTEA,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX users_email_lower_key ON users (LOWER(email));

CREATE TABLE censos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    total_miembros INTEGER NOT NULL CHECK (total_miembros >= 1),
    cantidad_televisores INTEGER NOT NULL CHECK (cantidad_televisores >= 0),
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL,
    origen VARCHAR(10) NOT NULL DEFAULT 'normal' CHECK (origen IN ('normal', 'masivo')),
    censista_id INTEGER REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Usuario de prueba: usuario = "admin", password = "admin123", rol administrador
-- El hash de abajo corresponde a "admin123" generado con bcrypt (10 rounds)
INSERT INTO users (usuario, nombre, apellido, email, rol, password_hash)
VALUES ('admin', 'Admin', 'Sistema', 'admin@example.com', 'administrador', '$2a$10$OEH3FlUa7.V.k8UTMwNxWuidrIYOH2.MmijiOtJ.mv/8VIV6OhYP2')
ON CONFLICT (usuario) DO NOTHING;
