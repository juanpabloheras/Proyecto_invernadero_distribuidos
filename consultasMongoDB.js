
//tipo de consulta para reportes internos de invernaderos con todos los sensores NO METRICAS NI PROMEDIO SOLO VISUAL
db.getCollection("lecturas_invernadero").aggregate([
  {
    $project: {
      _id: 1,
      sensorId: 1,
      timestamp: 1,
      mediciones: {
        $map: {
          input: "$mediciones",
          as: "m",
          in: {
            tipo: "$$m.tipo",
            valorOriginal: "$$m.valorOriginal"
          }
        }
      }
    }
  },
  { $sort: { sensorId: 1 } }
])


//consulta para metricas de sensores api publica
db.getCollection("lecturas_invernadero").find(
  {},
  {
    _id: 0,
    sensorId: 1,
    timestamp: 1,
    "mediciones.tipo": 1,
    "mediciones.valorNumerico": 1,
    "mediciones.valorOriginal": 1,
    "mediciones.unidad": 1,
    "mediciones.esValido": 1
  }
)