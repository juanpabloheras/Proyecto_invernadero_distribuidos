require("dotenv").config();

module.exports = {
  port: process.env.PORT || 3000,
  db: {
    name: process.env.DB_NAME || 'invernadero_db',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || 'password',
    host: process.env.DB_HOST || 'localhost',
    port: process.env.DB_PORT || 3306,
  },
  grpc: {
    alarmaUrl: process.env.ALARMA_GRPC_URL || "localhost:50051",
    timeoutMs: Number(process.env.ALARMA_GRPC_TIMEOUT_MS) || 3000,
  }
};