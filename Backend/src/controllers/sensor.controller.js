const sensorService = require("../services/sensor.service");

const obtenerSensores = async (req, res) => {
  try {
    const data = await sensorService.obtenerSensores();

    res.json({ data });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const crearSensor = async (req, res) => {
  try {
    const nuevo = await sensorService.crearSensor(req.body);

    res.status(201).json({
      message: "Sensor creado",
      data: nuevo,
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

const obtenerSensorPorId = async (req, res) => {
  try {

    const { id } = req.params;

    const sensor = await sensorService.obtenerSensorPorId(id);

    res.json({
      message: 'Sensor obtenido correctamente',
      data: sensor
    });

  } catch (error) {
    res.status(404).json({
      error: error.message
    });
  }

};

const eliminarSensor = async (req, res) => {
  try {
    const { id } = req.params;

    await sensorService.eliminarSensor(id);

    res.json({
      message: 'Sensor eliminado correctamente'
    });


  } catch (error) {

    res.status(404).json({
      error: error.message
    });
  }
  
  
}



module.exports = {
  obtenerSensores,
  crearSensor,
  obtenerSensorPorId,
  eliminarSensor
};