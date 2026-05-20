const app = require("./app");
const sequelize = require("./config/database");
const { port } = require("./config/env");
const { iniciarServidorGrpc } = require("./grpc/alarmaNotificacion.server");

async function iniciarServidor() {
  try {
    await sequelize.authenticate();
    console.log('Conexión a la base de datos exitosa');

    await sequelize.sync();
    console.log('Tablas sincronizadas correctamente');

    app.listen(port, () => {
      console.log(`Servidor corriendo en http://localhost:${port}`);
    });

    await iniciarServidorGrpc();
  } catch (error) {
    console.error('Error al iniciar el servidor:', error);
    process.exit(1);
  }
}

iniciarServidor();
