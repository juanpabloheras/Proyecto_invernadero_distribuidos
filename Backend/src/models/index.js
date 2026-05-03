const sequelize = require('../config/database');


const Sensor = require('./Sensor');
const ConfiguracionAlarma = require('./ConfiguracionAlarma');
const MedioNotificacion = require('./MedioNotificacion');
const Usuario = require('./Usuario');


// Relaciones

// N:M ConfiguracionAlarma - MedioNotificacion
ConfiguracionAlarma.belongsToMany(MedioNotificacion, {
    through: 'alarma_medios',
    foreignKey: 'configuracionAlarmaId',
    otherKey: 'medioId',
    as: 'medios'
});

MedioNotificacion.belongsToMany(ConfiguracionAlarma, {
    through: 'alarma_medios',
    foreignKey: 'medioId',
    otherKey: 'configuracionAlarmaId',
    as: 'alarmas'
})


// Exportar todo
module.exports = {
  sequelize,
  Sensor,
  ConfiguracionAlarma,
  MedioNotificacion,
  Usuario
};