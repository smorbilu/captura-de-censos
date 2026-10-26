const censoRepository = require('../repositories/censoRepository');

class CensoValidationError extends Error {
    constructor(message) {
        super(message);
        this.name = 'CensoValidationError';
    }
}

function textoValido(valor) {
    return typeof valor === 'string' && valor.trim().length > 0;
}

function enteroValido(valor, minimo) {
    return Number.isInteger(valor) && valor >= minimo;
}

function coordenadaValida(valor, limite) {
    return typeof valor === 'number' && Number.isFinite(valor) && Math.abs(valor) <= limite;
}

async function crear(datos, censistaId) {
    const { nombre, apellido, totalMiembros, cantidadTelevisores, latitud, longitud } = datos || {};

    if (!textoValido(nombre) || !textoValido(apellido)) {
        throw new CensoValidationError('El nombre y el apellido son obligatorios');
    }

    if (!enteroValido(totalMiembros, 1)) {
        throw new CensoValidationError('El total de miembros debe ser un número entero mayor a 0');
    }

    if (!enteroValido(cantidadTelevisores, 0)) {
        throw new CensoValidationError('La cantidad de televisores debe ser un número entero válido');
    }

    if (!coordenadaValida(latitud, 90) || !coordenadaValida(longitud, 180)) {
        throw new CensoValidationError('La ubicación del censo no es válida');
    }

    return censoRepository.create({
        nombre: nombre.trim(),
        apellido: apellido.trim(),
        totalMiembros,
        cantidadTelevisores,
        latitud,
        longitud,
        censistaId,
    });
}

async function listar() {
    return censoRepository.findAll();
}

module.exports = { crear, listar, CensoValidationError };
