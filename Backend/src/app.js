require("dotenv").config();

const express = require("express");
const cors = require("cors");

const sensorRoutes = require("./routes/sensor.routes");

const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.json({
    message: "API del invernadero funcionando",
  });
});

app.use("/api/sensores", sensorRoutes);

module.exports = app;