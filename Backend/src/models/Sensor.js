const { DataTypes } = require('sequelize');
const  sequelize  = require('../config/database');

const Sensor = sequelize.define('Sensor',
    {
        idSensor: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true,
        },
        nombre: {
            type: DataTypes.STRING(100),
            allowNull: false,
        },
        tipo: {
            type: DataTypes.ENUM('TEMPERATURA', 'HUMEDAD'),
            allowNull: false,
        },
        activo: {
            type: DataTypes.BOOLEAN,
            defaultValue: true
        }
    },
    {
        tableName: 'sensores',
        timestamps: true
    }
);

module.exports = Sensor;