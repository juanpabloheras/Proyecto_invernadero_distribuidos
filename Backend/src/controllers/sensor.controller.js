const sensorService = require("../services/sensor.service");

const obtenerSensores = async (req, res, next) => {
  try {
    const data = await sensorService.obtenerSensores(req.query);
    res.json({ data });
  } catch (error) {
    next(error);
  }
};

const crearSensor = async (req, res, next) => {
  try {
    const nuevo = await sensorService.crearSensor(req.body);

    res.status(201).json({
      message: "Sensor creado",
      data: nuevo,
    });
  } catch (error) {
    next(error);
  }
};

const obtenerSensorPorId = async (req, res, next) => {
  try {

    const { id } = req.params;

    const sensor = await sensorService.obtenerSensorPorId(id);

    res.json({
      message: 'Sensor obtenido correctamente',
      data: sensor
    });

  } catch (error) {
    next(error);
  }

};

const eliminarSensor = async (req, res, next) => {
  try {
    const { id } = req.params;

    await sensorService.eliminarSensor(id);

    res.json({
      message: 'Sensor eliminado correctamente'
    });


  } catch (error) {

    next(error);
  }


}



module.exports = {
  obtenerSensores,
  crearSensor,
  obtenerSensorPorId,
  eliminarSensor
};