const { MedioNotificacion, ConfiguracionAlarma } = require('../models');

const crear = async (data) => {
    return await ConfiguracionAlarma.create(data);
};

const obtenerTodas = async ({ limit, offset, tipoAlarma, activa, sort, order }) => {
    const where = {};

    if (tipoAlarma) {
        where.tipoAlarma = tipoAlarma;
    }

    if (activa !== undefined) {
        where.activa = activa === 'true' || activa === true;
    }

    // Se espera que se valide el valor de sort y order antes de llamar a esta función.
    const orderClause = [[sort, order]];

    return await ConfiguracionAlarma.findAndCountAll({
        where,
        limit,
        offset,
        distinct: true,
        order: orderClause,
        include: [
            {
                model: MedioNotificacion,
                as: 'medios',
                through: {
                    attributes: []
                }
            }
        ]
    });
};

const obtenerPorId = async (id) => {
    return await ConfiguracionAlarma.findByPk(id, {
        include: [
            {
                model: MedioNotificacion,
                as: 'medios',
                through: {
                    attributes: []
                }
            }
        ]
    });
};

const obtenerActivas = async () => {
    return await ConfiguracionAlarma.findAll({
        where: { activa: true }
    });
};

const eliminar = async (id) => {
    const configuracion = await ConfiguracionAlarma.findByPk(id);

    if (!configuracion) {
        return null
    }
    await configuracion.destroy();

    return configuracion;
};

module.exports = {
    crear,
    obtenerTodas,
    obtenerPorId,
    obtenerActivas,
    eliminar
};