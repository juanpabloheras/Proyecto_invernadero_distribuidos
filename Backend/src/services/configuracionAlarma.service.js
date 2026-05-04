const configuracionAlarmaDao = require('../data/configuracionAlarma.dao');
const AppError = require('../utils/AppError');
const notificacionesGrpcClient = require("../grpc/alarmaNotificacion.client");

const obtenerConfiguracionesAlarmas = async (query) => {
    const page = parseInt(query.page) || 1;
    const limit = parseInt(query.limit) || 10;

    let { tipoAlarma, activa, sort, order } = query;

    sort = sort || 'createdAt';
    order = order ? order.toUpperCase() : 'DESC';

    if (page < 1) {
        throw new AppError('La página debe ser mayor o igual a 1', 400);
    }

    if (limit < 1 || limit > 100) {
        throw new AppError('El límite debe estar entre 1 y 100', 400);
    }

    if (tipoAlarma && !['HUMEDAD', 'TEMPERATURA'].includes(tipoAlarma)) {
        throw new AppError('Tipo de alarma inválido', 400);
    }

    const camposPermitidos = ['nombreAlarma', 'tipoAlarma', 'operador', 'valorCritico', 'activa', 'createdAt'];

    if (!camposPermitidos.includes(sort)) {
        throw new AppError('Campo de ordenamiento inválido', 400);
    }

    if (!['ASC', 'DESC'].includes(order)) {
        throw new AppError('Orden inválido. Debe ser ASC o DESC', 400);
    }

    const offset = (page - 1) * limit;

    const resultado = await configuracionAlarmaDao.obtenerTodas({
        limit,
        offset,
        tipoAlarma,
        activa,
        sort,
        order
    });

    return {
        total: resultado.count,
        pagina: page,
        limite: limit,
        totalPaginas: Math.ceil(resultado.count / limit),
        data: resultado.rows
    };
};

const crearConfiguracionAlarma = async (data) => {
    const { nombreAlarma, tipoAlarma, operador, valorCritico } = data;

    if (!nombreAlarma || nombreAlarma.trim() === '') {
        throw new AppError('El nombre de la alarma es obligatorio', 400);
    }

    if (!tipoAlarma || !['HUMEDAD', 'TEMPERATURA'].includes(tipoAlarma)) {
        throw new AppError('Solo hay tipos de alarma de HUMEDAD o TEMPERATURA', 400);
    }

    if (!operador || !['MAYOR_QUE', 'MENOR_QUE', 'IGUAL_A'].includes(operador)) {
        throw new AppError('Operador inválido. Debe ser MAYOR_QUE, MENOR_QUE o IGUAL_A', 400);
    }

    if (valorCritico === undefined || isNaN(Number(valorCritico))) {
        throw new AppError('Valor crítico debe ser un número', 400);
    }

    const nuevaConfiguracion = await configuracionAlarmaDao.crear(data);

    try{
        await notificacionesGrpcClient.notificarConfiguracionAlarmaCreada({
            idConfiguracionAlarma: nuevaConfiguracion.idConfiguracionAlarma,
            nombreAlarma: nuevaConfiguracion.nombreAlarma,
            tipoAlarma: nuevaConfiguracion.tipoAlarma,
            operador: nuevaConfiguracion.operador,
            valorCritico: nuevaConfiguracion.valorCritico,
            activa: nuevaConfiguracion.activa
        });
    } catch (error) {
        console.error("No se pudo notificar por gRPC la creación de alarma " + error.message);
    }

    return nuevaConfiguracion;
};

const obtenerConfiguracionAlarmaPorId = async (id) => {

    const configuracionAlarma = await configuracionAlarmaDao.obtenerPorId(id);

    if (!configuracionAlarma) {
        throw new AppError('Configuración de alarma no encontrada', 404);
    }

    return configuracionAlarma;
};

const eliminarConfiguracionAlarma = async (id) => {

    const configuracionAlarma = await configuracionAlarmaDao.obtenerPorId(id);

    if (!configuracionAlarma) {
        throw new AppError('Configuración de alarma no encontrada', 404);
    }

    await configuracionAlarmaDao.eliminar(id);

    return true;
};

module.exports = {
    obtenerConfiguracionesAlarmas,
    crearConfiguracionAlarma,
    obtenerConfiguracionAlarmaPorId,
    eliminarConfiguracionAlarma
};


