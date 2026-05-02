const { DataTypes } = require('sequelize');
const  sequelize  = require('../config/database');

const ConfiguracionAlarma = sequelize.define('ConfiguracionAlarma',
    {
        idConfiguracionAlarma: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        nombreAlarma: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
        tipoAlarma: {
            type: DataTypes.ENUM('HUMEDAD', 'TEMPERATURA'),
            allowNull: false
        },
        operador: {
            type: DataTypes.ENUM('MAYOR_QUE', 'MENOR_QUE', 'IGUAL_A'),
            allowNull: false
        },
        valorCritico: {
            type: DataTypes.FLOAT,
            allowNull: false
        },
        activa: {
            type: DataTypes.BOOLEAN,
            defaultValue: true
        }
    },
    {
        tableName: 'configuraciones_alarmas',
        timestamps: true
    }
);

module.exports = ConfiguracionAlarma;