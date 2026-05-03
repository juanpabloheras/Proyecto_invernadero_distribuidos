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

const obtenerSensores = async () => {
  return await sensorDao.obtenerTodos();
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