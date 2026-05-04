const grpcLib = require("@grpc/grpc-js");
const protoLoader = require("@grpc/proto-loader");
const path = require("path");
const { grpc: grpcConfig } = require("../config/env");

const protoPath = path.join(__dirname, "../proto/alarma-notificacion.proto");

const packageDefinition = protoLoader.loadSync(protoPath, {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true
});

const proto = grpcLib.loadPackageDefinition(packageDefinition).alarmas;

const client = new proto.AlarmaNotificacionService(
    grpcConfig.alarmaUrl || "localhost:50051", 
    grpcLib.credentials.createInsecure()
);

const notificarConfiguracionAlarmaCreada = (payload) => {
    return new Promise((resolve, reject) => {
        const deadline = new Date(Date.now() + grpcConfig.timeoutMs);
        client.NotificarConfiguracionCreada(payload, { deadline }, (error, response) => {
            if (error) {
                return reject(error);
            }

            resolve(response);
        });
    });
};

module.exports = {
    notificarConfiguracionAlarmaCreada
};