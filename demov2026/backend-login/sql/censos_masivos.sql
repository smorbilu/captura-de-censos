CREATE OR REPLACE FUNCTION generar_censos_masivos(cantidad INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    nombres TEXT[] := ARRAY[
        'Juan', 'María', 'José', 'Ana', 'Luis', 'Carmen', 'Pedro', 'Rosa',
        'Carlos', 'Sofía', 'Miguel', 'Lucía', 'Jorge', 'Elena', 'Manuel', 'Gabriela'
    ];
    apellidos TEXT[] := ARRAY[
        'García', 'López', 'Martínez', 'Hernández', 'González', 'Pérez', 'Rodríguez', 'Ramírez',
        'Morales', 'Castillo', 'Mendoza', 'Cruz', 'Flores', 'Ortiz', 'Reyes', 'Díaz'
    ];
    latitudes DOUBLE PRECISION[] := ARRAY[
        14.6349, 14.5586, 14.8347, 15.4708, 15.7278, 14.3050, 15.3197, 16.9314, 14.8000, 14.5333
    ];
    longitudes DOUBLE PRECISION[] := ARRAY[
        -90.5069, -90.7295, -91.5180, -90.3708, -88.5944, -90.7850, -91.4708, -89.8933, -89.5444, -91.6833
    ];
    insertados INTEGER;
BEGIN
    IF cantidad IS NULL OR cantidad < 1 THEN
        RAISE EXCEPTION 'La cantidad de censos debe ser un entero mayor a 0';
    END IF;

    INSERT INTO censos (
        nombre, apellido, total_miembros, cantidad_televisores,
        latitud, longitud, origen, censista_id
    )
    SELECT
        nombres[1 + floor(random() * array_length(nombres, 1))::int],
        apellidos[1 + floor(random() * array_length(apellidos, 1))::int],
        1 + floor(random() * 8)::int,
        floor(random() * 5)::int,
        latitudes[1 + (g.n % array_length(latitudes, 1))] + (random() - 0.5) * 0.2,
        longitudes[1 + (g.n % array_length(longitudes, 1))] + (random() - 0.5) * 0.2,
        'masivo',
        NULL
    FROM generate_series(1, cantidad) AS g(n);

    GET DIAGNOSTICS insertados = ROW_COUNT;
    RETURN insertados;
END;
$$;

CREATE OR REPLACE FUNCTION eliminar_censos_masivos()
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    eliminados INTEGER;
BEGIN
    DELETE FROM censos WHERE origen = 'masivo';
    GET DIAGNOSTICS eliminados = ROW_COUNT;
    RETURN eliminados;
END;
$$;
