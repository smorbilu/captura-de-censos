const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const userRepository = require('../repositories/userRepository');

// Capa de servicio: acá vive la lógica de negocio del login,
// separada de las rutas (controllers) y del acceso a datos (repository).

class AuthError extends Error {
    constructor(message) {
        super(message);
        this.name = 'AuthError';
    }
}

class ValidationError extends Error {
    constructor(message) {
        super(message);
        this.name = 'ValidationError';
    }
}

class ConflictError extends Error {
    constructor(message) {
        super(message);
        this.name = 'ConflictError';
    }
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const UNIQUE_VIOLATION = '23505';

function buildSession(user) {
    const token = jwt.sign(
        { userId: user.id, usuario: user.usuario, rol: user.rol },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN || '1h' }
    );

    return {
        token,
        user: {
            id: user.id,
            nombre: user.nombre,
            apellido: user.apellido,
            usuario: user.usuario,
            email: user.email,
            telefono: user.telefono,
            rol: user.rol,
            token,
        },
    };
}

async function register(nombre, apellido, email, telefono, password) {
    if (
        typeof nombre !== 'string' || typeof apellido !== 'string' || typeof email !== 'string' ||
        typeof telefono !== 'string' || typeof password !== 'string' ||
        !nombre.trim() || !apellido.trim() || !email.trim() || !telefono.trim() || !password
    ) {
        throw new ValidationError('Todos los campos son obligatorios');
    }

    const correo = email.trim().toLowerCase();
    if (!EMAIL_REGEX.test(correo)) {
        throw new ValidationError('El correo no es válido');
    }

    const existente = await userRepository.findByUsuarioOrEmail(correo);
    if (existente) {
        throw new ConflictError('El correo ya está registrado');
    }

    const passwordHash = await bcrypt.hash(password, 10);

    try {
        const creado = await userRepository.create({
            usuario: correo,
            nombre: nombre.trim(),
            apellido: apellido.trim(),
            email: correo,
            telefono: telefono.trim(),
            passwordHash,
        });
        return buildSession(creado);
    } catch (err) {
        if (err.code === UNIQUE_VIOLATION) {
            throw new ConflictError('El correo ya está registrado');
        }
        throw err;
    }
}

async function login(usuario, password) {
    if (typeof usuario !== 'string' || typeof password !== 'string' || !usuario.trim() || !password) {
        throw new ValidationError('El usuario y la contraseña son obligatorios');
    }

    const user = await userRepository.findByUsuarioOrEmail(usuario.trim());
    if (!user) {
        throw new AuthError('Usuario o contraseña incorrectos');
    }

    const passwordMatches = await bcrypt.compare(password, user.password_hash);
    if (!passwordMatches) {
        throw new AuthError('Usuario o contraseña incorrectos');
    }

    return buildSession(user);
}

module.exports = { login, register, AuthError, ValidationError, ConflictError };
