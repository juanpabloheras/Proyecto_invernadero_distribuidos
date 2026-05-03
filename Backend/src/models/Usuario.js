const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');


const Usuario = sequelize.define('Usuario',
    {
        idUsuario: {
            type: DataTypes.INTEGER,
            primaryKey: true,
            autoIncrement: true
        },
        nombre: {
            type: DataTypes.STRING(100),
            allowNull: true
        },
        correo: {
            type: DataTypes.STRING(100),
            allowNull: false,
            unique: true,
            validate: {
                isEmail: true
            }
        },
        contrasenia: {
            type: DataTypes.STRING(255),
            allowNull: false
        }
    },
    {
        tableName: 'usuarios',
        timestamps: true
    }
);

module.exports = Usuario;