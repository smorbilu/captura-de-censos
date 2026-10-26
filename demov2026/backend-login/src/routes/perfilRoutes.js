const express = require('express');
const perfilController = require('../controllers/perfilController');
const { autenticar } = require('../middleware/authMiddleware');

const router = express.Router();

const recibirFoto = express.raw({ type: ['image/jpeg', 'image/png'], limit: '2mb' });

router.put('/perfil/foto', autenticar, recibirFoto, perfilController.guardarFoto);
router.get('/perfil/foto', autenticar, perfilController.obtenerFoto);

module.exports = router;
