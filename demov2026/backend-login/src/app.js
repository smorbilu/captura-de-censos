const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/authRoutes');
const censoRoutes = require('./routes/censoRoutes');
const perfilRoutes = require('./routes/perfilRoutes');
const apiResponse = require('./utils/apiResponse');

const app = express();

app.use(cors());
app.use(express.json());

// Ruta de salud, útil para probar que el servidor responde
// (también sigue el formato de respuesta estándar)
app.get('/api/health', (req, res) => {
    apiResponse.success(res, { message: 'API funcionando correctamente' });
});

app.use('/api', authRoutes);
app.use('/api', censoRoutes);
app.use('/api', perfilRoutes);

// Manejo de rutas no encontradas, también con el formato estándar
app.use((req, res) => {
    apiResponse.error(res, { message: 'Ruta no encontrada', statusCode: 404 });
});

app.use((err, req, res, next) => {
    if (err.type === 'entity.parse.failed') {
        return apiResponse.error(res, { message: 'El JSON enviado no es válido', statusCode: 400 });
    }
    if (err.type === 'entity.too.large') {
        return apiResponse.error(res, { message: 'La imagen es demasiado grande (máximo 2 MB)', statusCode: 413 });
    }
    console.error(err);
    return apiResponse.error(res, { message: 'Error interno del servidor', statusCode: 500 });
});

module.exports = app;
