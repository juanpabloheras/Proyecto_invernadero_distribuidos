const grpcLib = require("@grpc/grpc-js");
const protoLoader = require("@grpc/proto-loader");
const path = require("path");
const { grpc: grpcConfig } = require("../config/env");
const configuracionAlarmaService = require("../services/configuracionAlarma.service");

const protoPath = path.join(__dirname, "../proto/alarma-notificacion.proto");

const packageDefinition = protoLoader.loadSync(protoPath, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true
});

const proto = grpcLib.loadPackageDefinition(packageDefinition).alarmas;

const obtenerConfiguracionesActivas = async (call, callback) => {
    try {
        const configuraciones = await configuracionAlarmaService.obtenerConfiguracionesActivas();

        const payload = {
            configuraciones: configuraciones.map((c) => ({
                idConfiguracionAlarma: c.idConfiguracionAlarma,
                nombreAlarma: c.nombreAlarma,
                tipoAlarma: c.tipoAlarma,
                operador: c.operador,
                valorCritico: c.valorCritico,
                activa: c.activa
            }))
        };

        callback(null, payload);
    } catch (error) {
        console.error("Error en ObtenerConfiguracionesActivas:", error);
        callback({
            code: grpcLib.status.INTERNAL,
            message: error.message || "Error interno al obtener configuraciones activas"
        });
    }
};

const iniciarServidorGrpc = () => {
    return new Promise((resolve, reject) => {
        const server = new grpcLib.Server();

        server.addService(proto.AlarmaNotificacionService.service, {
            obtenerConfiguracionesActivas
        });

        const direccion = `${grpcConfig.serverHost}:${grpcConfig.serverPort}`;

        server.bindAsync(direccion, grpcLib.ServerCredentials.createInsecure(), (error, puerto) => {
            if (error) {
                return reject(error);
            }

            console.log(`Servidor gRPC corriendo en ${grpcConfig.serverHost}:${puerto}`);
            resolve(server);
        });
    });
};

module.exports = {
    iniciarServidorGrpc
};
