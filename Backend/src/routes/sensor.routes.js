const express = require("express");
const router = express.Router();

const sensorController = require("../controllers/sensor.controller");

router.get("/", sensorController.obtenerSensores);
router.post("/", sensorController.crearSensor);
router.get("/:id", sensorController.obtenerSensorPorId);
router.delete("/:id", sensorController.eliminarSensor);

module.exports = router;