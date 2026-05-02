const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const MedioNotificacion = sequelize.define('MedioNotificacion',
    {
        id: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        nombre: {
            type: DataTypes.ENUM('EMAIL', 'APP', 'SMS'),
            allowNull: false
        }
    },
    {
        tableName: 'medios_notificacion',
        timestamps: true
    }
);

module.exports = MedioNotificacion;