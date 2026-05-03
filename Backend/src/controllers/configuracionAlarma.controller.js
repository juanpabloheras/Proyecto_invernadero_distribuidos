const configuracionAlarmaService = require("../services/configuracionAlarma.service")

const obtenerConfiguracionesAlarmas = async (req, res, next) => {
    try {
        const data = await configuracionAlarmaService.obtenerConfiguracionesAlarmas(req.query);

        res.json({
            data
        });

    } catch (error) {
        next(error);
    }
};

const crearConfiguracionAlarma = async (req, res, next) => {
    try {
        const nuevaConfiguracion = await configuracionAlarmaService.crearConfiguracionAlarma(req.body);

        res.status(201).json({
            message: "Configuracion de alarma creada",
            data: nuevaConfiguracion,
        });
    } catch (error) {
        next(error);
    }
};


const obtenerConfiguracionAlarmaPorId = async (req, res, next) => {
    try {
        const { id } = req.params;

        const configuracionAlarma = await configuracionAlarmaService.obtenerConfiguracionAlarmaPorId(id);

        res.json({
            message: "Configuración obtenida correctamente",
            data: configuracionAlarma
        });

    } catch (error) {
        next(error);
    }
};

const eliminarConfiguracionAlarma = async (req, res, next) => {
    try {
        const { id } = req.params;
        await configuracionAlarmaService.eliminarConfiguracionAlarma(id);

        res.json({
            message: 'Configuración de alarma eliminada correctamente'
        });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    obtenerConfiguracionesAlarmas,
    crearConfiguracionAlarma,
    obtenerConfiguracionAlarmaPorId,
    eliminarConfiguracionAlarma
};




