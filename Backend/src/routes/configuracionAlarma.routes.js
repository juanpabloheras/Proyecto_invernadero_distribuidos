const express = require("express");
const router = express.Router();
const configuracionAlarmaController = require("../controllers/configuracionAlarma.controller");
const verificarTokenFirebase = require("../middlewares/firebase-auth");

router.use(verificarTokenFirebase);

router.get("/", configuracionAlarmaController.obtenerConfiguracionesAlarmas);
router.post("/", configuracionAlarmaController.crearConfiguracionAlarma);
router.get("/:id", configuracionAlarmaController.obtenerConfiguracionAlarmaPorId);
router.delete("/:id", configuracionAlarmaController.eliminarConfiguracionAlarma);

module.exports = router;