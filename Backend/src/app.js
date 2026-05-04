require("dotenv").config();

const express = require("express");
const cors = require("cors");
const path = require("path");
const swaggerUi = require("swagger-ui-express");
const errorHandler = require("./middlewares/error.middleware");
const sensorRoutes = require("./routes/sensor.routes");
const usuarioRoutes = require("./routes/usuario.routes");
const configuracionAlarmaRoutes = require('./routes/configuracionAlarma.routes');



const app = express();



app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.json({
    message: "API del invernadero funcionando",
  });
});

app.get("/openapi.yaml", (req, res) => {
  res.sendFile(path.join(__dirname, "../docs/openapi.yaml"));
});

app.use(
  "/api-docs",
  swaggerUi.serve,
  swaggerUi.setup(null, {
    swaggerOptions: {
      url: "/openapi.yaml",
    },
  })
);

app.use("/api/sensores", sensorRoutes);
app.use("/api/configuraciones-alarma",configuracionAlarmaRoutes );
app.use("/api/usuarios", usuarioRoutes);

app.use(errorHandler);



module.exports = app;
