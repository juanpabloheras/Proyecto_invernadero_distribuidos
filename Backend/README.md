# 🌱 Backend Invernadero

API REST desarrollada con Node.js + Express para la gestión de un sistema de invernadero.

## 📑 Índice

- [Tecnologías](#-tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Arquitectura](#arquitectura)
- [Endpoints](#endpoints)
- [Cómo agregar un método](#cómo-agregar-un-nuevo-método-endpoint)
- [Nuevos módulos](#nota-agregar-un-nuevo-módulo-o-dominio)
- [Instalación](#-instalación)
- [Ejecución](#️-ejecución-del-proyecto)


### 🚀 Tecnologías
- Node.js
- Express
- Nodemon
- Dotenv 
- CORS


### Instalación

```bash
git clone <url-del-repositorio>
cd backend-invernadero
npm install
```

### Ejecución del proyecto 
```bash
npm run dev
```
O en modo producción:
```bash
npm start
```

### Estructura del proyecto
```bash
src/
├─ app.js
├─ server.js
├─ config/
│  └─ env.js
├─ routes/
│  └─ sensor.routes.js
├─ controllers/
│  └─ sensor.controller.js
├─ services/
│  └─ sensor.service.js
├─ daos/
│  └─ sensor.dao.js
├─ models/
│  └─ sensor.model.js
└─ middlewares/
   └─ error.middleware.js
```

### Arquitectura
El proyecto sigue una arquitectura por capas
```bash
Route → Controller → Service → Dao → Model
```
 * Routes: Definen las rutas de la API y el método HTTP
 * Controllers: Reciben la petición (`req`) y envían la respuesta (`res`).
 * Services: Contienen la lógica de negocio.
 * Daos: Acceso a los models
 * Models: Manejan los datos (base de datos o memoria.)


### Endpoints
- Obtener todas las lecturas 
```bash
GET /api/sensores
```

- Crear una nueva lectura

```bash
POST /api/sensores
```

Body:
```JSON
{
  "temperatura": 25,
  "humedad": 70,
  "humedadSuelo": 50,
  "luz": 800
}
```


## Cómo agregar un nuevo método (endpoint)

Para agregar un nuevo endpoint a la API se sigue el sig flujo:
```bash
Route -> Controller -> Service -> DAO -> Model
```

📌 Paso 1: Definir la ruta

Se define el método HTTP y la URL.
```js
// src/routes/sensor.routes.js
router.delete("/:id", sensorController.eliminarLectura);
```

📌 Paso 2: Crear el controller

Maneja la petición (`req`) y la respuesta (`res`).

```js
// src/controllers/sensor.controller.js
const eliminarLectura = (req, res) => {
  try { 
    const { id } = req.params;

    sensorService.eliminarLectura(id);

    res.json({
      message: "Lectura eliminada",
    });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

```

📌 Paso 3: Agregar la lógica en el service

Aquí va la lógica del negocio.
```js
// src/services/sensor.service.js
const eliminarSensor = async (id) => {
  const sensor = await sensorDao.obtenerPorId(id);

  if (!sensor) {
    throw new Error('Sensor no encontrado');
  }

  await sensorDao.eliminar(id);

  return true;
};
```

📌 Paso 4: Modificar el DAO

Se encarga de manipular los datos.
```js
// src/models/sensor.dao.js
const obtenerPorId = async (id) => {
  return await Sensor.findByPk(id);
};

const eliminar = async (id) => {
  const sensor = await Sensor.findByPk(id);

  if (!sensor) {
    return null;
  }

  await sensor.destroy();

  return sensor;
};
```


### Nota: Agregar un nuevo módulo o dominio

Si se desea agregar un dominio diferente a sensores, se deben crear nuevos archivos siguiendo la misma arquitectura.

Por ejemplo, para agregar el módulo de alarmas:

```bash
src/
├─ routes/
│  └─ alarma.routes.js
├─ controllers/
│  └─ alarma.controller.js
├─ services/
│  └─ alarma.service.js
├─ data/
│  └─ alarma.dao.js
└─ models/
   └─ alarma.model.js
```

Luego se registra la nueva ruta en `app.js`:
```js
const alarmaRoutes = require("./routes/alarma.routes");

app.use("/api/alarmas", alarmaRoutes);
```

## Colaboradores:
- Juan Pablo Heras
- Pedro Morales 
- Jack Murrieta
- Alejandro Valdez
- Christopher Alvarez