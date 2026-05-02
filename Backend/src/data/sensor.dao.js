// src/dao/sensor.dao.js
const { Sensor } = require('../models');


const crear = async (data) => {
    return await Sensor.create(data);
};

const obtenerTodos = async () => {
    return await Sensor.findAll();
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