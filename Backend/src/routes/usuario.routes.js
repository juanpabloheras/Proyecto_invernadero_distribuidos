const express = require('express');
const usuarioController = require('../controllers/usuario.controller');

const router = express.Router();

router.get('/', usuarioController.obtenerUsuarios);

router.get('/:id', usuarioController.obtenerUsuarioPorId);

router.post('/registro', usuarioController.registrarUsuario);

router.post('/login', usuarioController.iniciarSesion);

router.put('/:id', usuarioController.actualizarUsuario);

router.delete('/:id', usuarioController.eliminarUsuario);

module.exports = router;