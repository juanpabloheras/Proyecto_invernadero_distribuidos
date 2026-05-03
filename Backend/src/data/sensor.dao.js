const { Sensor } = require('../models');


const crear = async (data) => {
    return await Sensor.create(data);
};

const obtenerTodos = async ({ limit, offset, nombre, tipo, activo, sort, order }) => {
  const where = {};

  if (nombre) {
    where.nombre = {
      [Op.like]: `%${nombre}%`
    };
  }

  if (tipo) {
    where.tipo = tipo;
  }

  if (activo !== undefined) {
    where.activo = activo === 'true' || activo === true;
  }

  return await Sensor.findAndCountAll({
    where,
    limit,
    offset,
    distinct: true,
    order: [[sort, order]]
  });
};

const obtenerPorId = async (id) => {
    return await Sensor.findByPk(id);
};

const eliminar = async (id) => {
    const sensor = await Sensor.findByPk(id);

    if (!sensor) {
        return null;
    }

    await sensor.destroy();

    return sensor;
};

module.exports = {
    obtenerPorId,
    eliminar,
    obtenerTodos,
    crear
};