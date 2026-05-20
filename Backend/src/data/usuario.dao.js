const { Usuario } = require('../models');

const crear = async (data) => {
  return await Usuario.create(data);
};

const obtenerPorFirebaseUid = async (firebaseUid) => {
  return await Usuario.findOne({
    where: { firebaseUid }
  });
};


const obtenerTodos = async () => {
  return await Usuario.findAll({
    attributes: { exclude: ['contrasenia'] }
  });
};

const obtenerPorId = async (idUsuario) => {
  return await Usuario.findByPk(idUsuario, {
    attributes: { exclude: ['contrasenia'] }
  });
};

const obtenerPorCorreo = async (correo) => {
  return await Usuario.findOne({
    where: { correo }
  });
};

const actualizar = async (idUsuario, data) => {
  const usuario = await Usuario.findByPk(idUsuario);

  if (!usuario) {
    return null;
  }

  await usuario.update(data);

  const usuarioActualizado = usuario.toJSON();
  delete usuarioActualizado.contrasenia;

  return usuarioActualizado;
};

const eliminar = async (idUsuario) => {
  const usuario = await Usuario.findByPk(idUsuario);

  if (!usuario) {
    return null;
  }

  await usuario.destroy();
  return usuario;
};

module.exports = {
  crear,
  obtenerTodos,
  obtenerPorId,
  obtenerPorCorreo,
  actualizar,
  eliminar,
  obtenerPorFirebaseUid
};
