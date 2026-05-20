const express = require("express");
const router = express.Router();

const sensorController = require("../controllers/sensor.controller");
const verificarTokenFirebase = require("../middlewares/firebase-auth");

router.use(verificarTokenFirebase);

router.get("/", sensorController.obtenerSensores);
router.post("/", sensorController.crearSensor);
router.get("/:id", sensorController.obtenerSensorPorId);
router.delete("/:id", sensorController.eliminarSensor);

module.exports = router;