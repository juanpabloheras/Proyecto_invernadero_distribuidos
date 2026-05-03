const bcrypt = require('bcrypt');
const usuarioDao = require('../data/usuario.dao');

const registrarUsuario = async (data) => {
  const { nombre, correo, contrasenia, rol } = data;

  if (!nombre || nombre.trim() === '') {
    throw new Error('El nombre es obligatorio');
  }

  if (!correo || correo.trim() === '') {
    throw new Error('El correo es obligatorio');
  }

  if (!contrasenia || contrasenia.trim() === '') {
    throw new Error('La contraseña es obligatoria');
  }

  if (contrasenia.length < 6) {
    throw new Error('La contraseña debe tener al menos 6 caracteres');
  }

  const usuarioExistente = await usuarioDao.obtenerPorCorreo(correo);

  if (usuarioExistente) {
    throw new Error('Ya existe un usuario con ese correo');
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
    throw new Error('Correo y contraseña son obligatorios');
  }

  const usuario = await usuarioDao.obtenerPorCorreo(correo);

  if (!usuario) {
    throw new Error('Credenciales incorrectas');
  }

  const contraseniaValida = await bcrypt.compare(contrasenia, usuario.contrasenia);

  if (!contraseniaValida) {
    throw new Error('Credenciales incorrectas');
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
    throw new Error('Usuario no encontrado');
  }

  return usuario;
};

const actualizarUsuario = async (idUsuario, data) => {
  const usuario = await usuarioDao.obtenerPorId(idUsuario);

  if (!usuario) {
    throw new Error('Usuario no encontrado');
  }

  if (data.contrasenia) {
    data.contrasenia = await bcrypt.hash(data.contrasenia, 10);
  }

  return await usuarioDao.actualizar(idUsuario, data);
};

const eliminarUsuario = async (idUsuario) => {
  const usuarioEliminado = await usuarioDao.eliminar(idUsuario);

  if (!usuarioEliminado) {
    throw new Error('Usuario no encontrado');
  }

  return true;
};

module.exports = {
  registrarUsuario,
  iniciarSesion,
  obtenerUsuarios,
  obtenerUsuarioPorId,
  actualizarUsuario,
  eliminarUsuario
};