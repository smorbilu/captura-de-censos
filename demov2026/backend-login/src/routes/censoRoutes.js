const express = require('express');
const censoController = require('../controllers/censoController');
const { autenticar, requerirRol } = require('../middleware/authMiddleware');

const router = express.Router();

router.post('/censos', autenticar, requerirRol('censista'), censoController.crear);
router.get('/censos', autenticar, requerirRol('administrador'), censoController.listar);

module.exports = router;
