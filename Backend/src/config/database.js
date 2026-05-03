const { Sequelize } = require('sequelize');
const { db } = require('./env');

const sequelize = new Sequelize(
    db.name,
    db.user,
    db.password,
    {
        host: db.host,
        port: db.port,
        dialect: 'mysql',
        logging: false,
    }
);

module.exports = sequelize ;