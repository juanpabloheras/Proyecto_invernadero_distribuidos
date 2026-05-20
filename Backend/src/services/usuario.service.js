const bcrypt = require('bcrypt');
const usuarioDao = require('../data/usuario.dao');
const AppError = require('../utils/AppError');

const obtenerUsuarioPorFirebaseUid = async (firebaseUid) => {
  const usuario = await usuarioDao.obtenerPorFirebaseUid(firebaseUid);

  if (!usuario){
    throw new AppError('Usuario no encontrado en la base de datos', 404);
  }

  const usuarioSinContrasenia = usuario.toJSON();
  delete usuarioSinContrasenia.contrasenia;

  return usuarioSinContrasenia;
}

const registrarUsuario = async (data) => {
  const { nombre, correo, contrasenia, rol } = data;

  if (!nombre || nombre.trim() === '') {
    throw new AppError('El nombre es obligatorio', 400);
  }

  if (!correo || correo.trim() === '') {
    throw new AppError('El correo es obligatorio', 400);
  }

  if (!contrasenia || contrasenia.trim() === '') {
    throw new AppError('La contraseña es obligatoria', 400);
  }

  if (contrasenia.length < 6) {
    throw new AppError('La contraseña debe tener al menos 6 caracteres', 400);
  }

  const usuarioExistente = await usuarioDao.obtenerPorCorreo(correo);

  if (usuarioExistente) {
    throw new AppError('Ya existe un usuario con ese correo', 400);
  }

  const contraseniaHasheada = await bcrypt.hash(contrasenia, 10);

  const usuarioCreado = await usuarioDao.crear({
    nombre,
    correo,
    contrasenia: contraseniaHasheada,
    rol: rol || 'USUARIO'
  });

  const usuarioSinContrasenia = usuarioCreado.toJSON();
  delete usuarioSinContrasenia.contrasenia;

  return usuarioSinContrasenia;
};

const iniciarSesion = async (correo, contrasenia) => {
  if (!correo || !contrasenia) {
    throw new AppError('Correo y contraseña son obligatorios', 400);
  }

  const usuario = await usuarioDao.obtenerPorCorreo(correo);

  if (!usuario) {
    throw new AppError('Credenciales incorrectas', 401);
  }

  const contraseniaValida = await bcrypt.compare(contrasenia, usuario.contrasenia);

  if (!contraseniaValida) {
    throw new AppError('Credenciales incorrectas', 401);
  }

  const usuarioSinContrasenia = usuario.toJSON();
  delete usuarioSinContrasenia.contrasenia;

  return usuarioSinContrasenia;
};

const obtenerUsuarios = async () => {
  return await usuarioDao.obtenerTodos();
};

const obtenerUsuarioPorId = async (idUsuario) => {
  const usuario = await usuarioDao.obtenerPorId(idUsuario);

  if (!usuario) {
    throw new AppError('Usuario no encontrado', 404);
  }

  return usuario;
};

const actualizarUsuario = async (idUsuario, data) => {
  const usuario = await usuarioDao.obtenerPorId(idUsuario);

  if (!usuario) {
    throw new AppError('Usuario no encontrado', 404);
  }

  if (data.contrasenia) {
    data.contrasenia = await bcrypt.hash(data.contrasenia, 10);
  }

  return await usuarioDao.actualizar(idUsuario, data);
};

const eliminarUsuario = async (idUsuario) => {
  const usuarioEliminado = await usuarioDao.eliminar(idUsuario);

  if (!usuarioEliminado) {
    throw new AppError('Usuario no encontrado', 404);
  }

  return true;
};

module.exports = {
  registrarUsuario,
  iniciarSesion,
  obtenerUsuarios,
  obtenerUsuarioPorId,
  actualizarUsuario,
  eliminarUsuario,
  obtenerUsuarioPorFirebaseUid
};
