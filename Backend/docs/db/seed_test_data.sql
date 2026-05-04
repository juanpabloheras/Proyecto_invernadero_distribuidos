USE invernadero_db;

INSERT INTO usuarios
  (idUsuario, nombre, correo, contrasenia, createdAt, updatedAt)
VALUES
  (101, 'Pedro Morales', 'pedro@test.com', '$2b$10$kA9dq/xNn4XVCF9U46jwZO.O2m.o3rKmpSqFzqyaDelpVz5Gf9vHS', NOW(), NOW()),
  (102, 'Admin Invernadero', 'admin@test.com', '$2b$10$UsWcGlDp1oQiDviUTbIAY.xtcHp6APtRN6iJxERlMg.JxAWXJWRq6', NOW(), NOW()),
  (103, 'Operador Demo', 'operador@test.com', '$2b$10$rmraM1UmuSWMPTC.kdiQPetgka/bcITePToSDv3JUODFmoP89uUG.', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre),
  contrasenia = VALUES(contrasenia),
  updatedAt = NOW();

INSERT INTO sensores
  (idSensor, nombre, tipo, activo, createdAt, updatedAt)
VALUES
  (101, 'Sensor temperatura entrada', 'TEMPERATURA', 1, NOW(), NOW()),
  (102, 'Sensor humedad zona norte', 'HUMEDAD', 1, NOW(), NOW()),
  (103, 'Sensor temperatura zona sur', 'TEMPERATURA', 0, NOW(), NOW()),
  (104, 'Sensor humedad semilleros', 'HUMEDAD', 1, NOW(), NOW()),
  (105, 'Sensor temperatura vivero', 'TEMPERATURA', 1, NOW(), NOW()),
  (106, 'Sensor humedad central', 'HUMEDAD', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre),
  tipo = VALUES(tipo),
  activo = VALUES(activo),
  updatedAt = NOW();

INSERT INTO medios_notificacion
  (id, nombre, createdAt, updatedAt)
VALUES
  (101, 'EMAIL', NOW(), NOW()),
  (102, 'APP', NOW(), NOW()),
  (103, 'SMS', NOW(), NOW())
ON DUPLICATE KEY UPDATE
  nombre = VALUES(nombre),
  updatedAt = NOW();

INSERT INTO configuraciones_alarmas
  (idConfiguracionAlarma, nombreAlarma, tipoAlarma, operador, valorCritico, activa, createdAt, updatedAt)
VALUES
  (101, 'Temperatura alta invernadero', 'TEMPERATURA', 'MAYOR_QUE', 35.5, 1, NOW(), NOW()),
  (102, 'Temperatura baja nocturna', 'TEMPERATURA', 'MENOR_QUE', 12.0, 1, NOW(), NOW()),
  (103, 'Humedad baja semilleros', 'HUMEDAD', 'MENOR_QUE', 40.0, 1, NOW(), NOW()),
  (104, 'Humedad ideal central', 'HUMEDAD', 'IGUAL_A', 65.0, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  nombreAlarma = VALUES(nombreAlarma),
  tipoAlarma = VALUES(tipoAlarma),
  operador = VALUES(operador),
  valorCritico = VALUES(valorCritico),
  activa = VALUES(activa),
  updatedAt = NOW();

INSERT INTO alarma_medios
  (configuracionAlarmaId, medioId, createdAt, updatedAt)
VALUES
  (101, 101, NOW(), NOW()),
  (101, 102, NOW(), NOW()),
  (102, 102, NOW(), NOW()),
  (103, 101, NOW(), NOW()),
  (103, 103, NOW(), NOW()),
  (104, 102, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  updatedAt = NOW();
