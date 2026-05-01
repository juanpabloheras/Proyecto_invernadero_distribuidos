const sensorDao = require('../dao/sensor.dao');

const crearSensor = async (data) => {
  const { nombre, tipo } = data;

  if (!nombre || nombre.trim() === '') {
    throw new Error('El nombre del sensor es obligatorio');
  }

  if (!tipo || !['TEMPERATURA', 'HUMEDAD'].includes(tipo)) {
    throw new Error('Tipo de sensor inválido');
  }

  return await sensorDao.crear(data);
};

const obtenerSensores = async () => {
  return await sensorDao.obtenerTodos();
};

const obtenerSensorPorId = async (id) => {
  const sensor = await sensorDao.obtenerPorId(id);

  if (!sensor) {
    throw new Error('Sensor no encontrado');
  }

  return sensor;
};

const eliminarSensor = async (id) => {
  const sensor = await sensorDao.obtenerPorId(id);

  if (!sensor) {
    throw new Error('Sensor no encontrado');
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