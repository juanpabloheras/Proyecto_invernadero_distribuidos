require("dotenv").config();

const express = require("express");
const cors = require("cors");
const errorHandler = require("./middlewares/error.middleware");
const sensorRoutes = require("./routes/sensor.routes");
const configuracionAlarmaRoutes = require('./routes/configuracionAlarma.routes')



const app = express();



app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.json({
    message: "API del invernadero funcionando",
  });
});

app.use("/api/sensores", sensorRoutes);
app.use("/api/configuraciones-alarma",configuracionAlarmaRoutes );

app.use(errorHandler);



module.exports = app;