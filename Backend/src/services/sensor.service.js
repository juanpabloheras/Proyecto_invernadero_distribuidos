const sensorDao = require('../data/sensor.dao');
const AppError = require('../utils/AppError');


const crearSensor = async (data) => {
  const { nombre, tipo } = data;

  if (!nombre || nombre.trim() === '') {
    throw new AppError('El nombre del sensor es obligatorio', 400);
  }

  if (!tipo || !['TEMPERATURA', 'HUMEDAD'].includes(tipo)) {
    throw new AppError('Tipo de sensor inválido', 400);
  }

  return await sensorDao.crear(data);
};

const obtenerSensores = async (query) => {
  const page = parseInt(query.page) || 1;
  const limit = parseInt(query.limit) || 10;

  let { nombre, tipo, activo, sort, order } = query;

  sort = sort || 'createdAt';
  order = order ? order.toUpperCase() : 'DESC';

  if (page < 1) {
    throw new AppError('La página debe ser mayor o igual a 1', 400);
  }

  if (limit < 1 || limit > 100) {
    throw new AppError('El límite debe estar entre 1 y 100', 400);
  }

  if (tipo && !['TEMPERATURA', 'HUMEDAD'].includes(tipo)) {
    throw new AppError('Tipo de sensor inválido', 400);
  }

  if (activo !== undefined && !['true', 'false', true, false].includes(activo)) {
    throw new AppError('El filtro activo debe ser true o false', 400);
  }

  const camposPermitidos = ['nombre', 'tipo', 'activo', 'createdAt'];

  if (!camposPermitidos.includes(sort)) {
    throw new AppError('Campo de ordenamiento inválido', 400);
  }

  if (!['ASC', 'DESC'].includes(order)) {
    throw new AppError('Orden inválido. Debe ser ASC o DESC', 400);
  }

  const offset = (page - 1) * limit;

  const resultado = await sensorDao.obtenerTodos({
    limit,
    offset,
    nombre,
    tipo,
    activo,
    sort,
    order
  });

  return {
    total: resultado.count,
    pagina: page,
    limite: limit,
    totalPaginas: Math.ceil(resultado.count / limit),
    data: resultado.rows
  };
};

const obtenerSensorPorId = async (id) => {
  const sensor = await sensorDao.obtenerPorId(id);

  if (!sensor) {
    throw new AppError('Sensor no encontrado', 404);
  }

  return sensor;
};

const eliminarSensor = async (id) => {
  const sensor = await sensorDao.obtenerPorId(id);

  if (!sensor) {
    throw new AppError('Sensor no encontrado', 404);
  }

  await sensorDao.eliminar(id);

  return true;
};

module.exports = {
  crearSensor,
  obtenerSensores,
  obtenerSensorPorId,
  eliminarSensor
};