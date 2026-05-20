const usuarioService = require('../services/usuario.service');


async function obtenerUsuarioActual(req, res, next) {
  try { 
    const firebaseUid = req.firebaseUser.uid;

    const usuario = await usuarioService.obtenerUsuarioPorFirebaseUid(firebaseUid);

    return res.json(usuario);
  } catch (error) {
    next(error);
  }
} 
const registrarUsuario = async (req, res, next) => {
  try {
    const nuevoUsuario = await usuarioService.registrarUsuario(req.body);

    res.status(201).json({
      message: 'Usuario registrado correctamente',
      data: nuevoUsuario
    });
  } catch (error) {
    next(error);
  }
};

const iniciarSesion = async (req, res, next) => {
  try {
    const { correo, contrasenia } = req.body;

    const usuario = await usuarioService.iniciarSesion(correo, contrasenia);

    res.json({
      message: 'Inicio de sesión correcto',
      data: usuario
    });
  } catch (error) {
    next(error);
  }
};

const obtenerUsuarios = async (req, res, next) => {
  try {
    const usuarios = await usuarioService.obtenerUsuarios();

    res.json({
      data: usuarios
    });
  } catch (error) {
    next(error);
  }
};

const obtenerUsuarioPorId = async (req, res, next) => {
  try {
    const { id } = req.params;

    const usuario = await usuarioService.obtenerUsuarioPorId(id);

    res.json({
      message: 'Usuario obtenido correctamente',
      data: usuario
    });
  } catch (error) {
    next(error);
  }
};

const actualizarUsuario = async (req, res, next) => {
  try {
    const { id } = req.params;

    const usuarioActualizado = await usuarioService.actualizarUsuario(id, req.body);

    res.json({
      message: 'Usuario actualizado correctamente',
      data: usuarioActualizado
    });
  } catch (error) {
    next(error);
  }
};

const eliminarUsuario = async (req, res, next) => {
  try {
    const { id } = req.params;

    await usuarioService.eliminarUsuario(id);

    res.json({
      message: 'Usuario eliminado correctamente'
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  registrarUsuario,
  iniciarSesion,
  obtenerUsuarios,
  obtenerUsuarioPorId,
  actualizarUsuario,
  eliminarUsuario
};