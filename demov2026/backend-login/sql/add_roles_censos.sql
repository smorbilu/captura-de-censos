ALTER TABLE users ADD COLUMN IF NOT EXISTS rol VARCHAR(20) NOT NULL DEFAULT 'censista';

UPDATE users SET rol = 'administrador' WHERE usuario = 'admin';

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'users_rol_check') THEN
        ALTER TABLE users ADD CONSTRAINT users_rol_check CHECK (rol IN ('censista', 'administrador'));
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS censos (
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
