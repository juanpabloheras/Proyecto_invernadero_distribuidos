<script setup>
import { ref, computed } from 'vue'
import {
  CalendarDays,
  Leaf,
  Thermometer,
  Droplets,
  Activity,
  Database,
  CircleCheck
} from 'lucide-vue-next'

const fechaInicio = ref('2026-05-01')
const fechaFin = ref('2026-05-03')
const invernaderoSeleccionado = ref('Invernadero Alpha')

const registros = ref([
  {
    id: 1,
    fechaHora: '2026-05-03 08:30',
    sensor: 'Temp_Sensor_01',
    tipo: 'Temperatura',
    valor: 24.5,
    unidad: '°C'
  },
  {
    id: 2,
    fechaHora: '2026-05-03 08:15',
    sensor: 'Humidity_Node_A',
    tipo: 'Humedad',
    valor: 62,
    unidad: '%'
  },
  {
    id: 3,
    fechaHora: '2026-05-03 08:00',
    sensor: 'Soil_Moist_04',
    tipo: 'Humedad de suelo',
    valor: 38.2,
    unidad: '%'
  },
  {
    id: 4,
    fechaHora: '2026-05-03 07:45',
    sensor: 'Temp_Sensor_01',
    tipo: 'Temperatura',
    valor: 23.8,
    unidad: '°C'
  },
  {
    id: 5,
    fechaHora: '2026-05-03 07:30',
    sensor: 'Humidity_Node_A',
    tipo: 'Humedad',
    valor: 64.1,
    unidad: '%'
  },
  {
    id: 6,
    fechaHora: '2026-05-02 18:10',
    sensor: 'Temp_Sensor_02',
    tipo: 'Temperatura',
    valor: 25.1,
    unidad: '°C'
  }
])

const promedioTemperatura = computed(() => {
  const datos = registros.value.filter(registro => registro.tipo === 'Temperatura')

  if (!datos.length) return 0

  const suma = datos.reduce((total, registro) => total + registro.valor, 0)
  return (suma / datos.length).toFixed(1)
})

const promedioHumedad = computed(() => {
  const datos = registros.value.filter(registro => registro.tipo === 'Humedad')

  if (!datos.length) return 0

  const suma = datos.reduce((total, registro) => total + registro.valor, 0)
  return (suma / datos.length).toFixed(1)
})

const promedioHumedadSuelo = computed(() => {
  const datos = registros.value.filter(registro => registro.tipo === 'Humedad de suelo')

  if (!datos.length) return 0

  const suma = datos.reduce((total, registro) => total + registro.valor, 0)
  return (suma / datos.length).toFixed(1)
})

function consultarReportes() {
  console.log('Consultando reportes con:', {
    fechaInicio: fechaInicio.value,
    fechaFin: fechaFin.value,
    invernadero: invernaderoSeleccionado.value
  })

  /*
    Después aquí conectarías el backend.

    Ejemplo:

    const response = await ReportesApi.obtenerPorFiltros({
      fechaInicio: fechaInicio.value,
      fechaFin: fechaFin.value,
      invernadero: invernaderoSeleccionado.value
    })

    registros.value = response.data.datos
  */
}
</script>

<template>
  <section class="reports-page">
    <header class="page-header">
      <h1>Reportes y análisis</h1>
    </header>

    <section class="filters-card">
      <div class="filter-group">
        <label>Fecha inicial</label>

        <div class="input-icon">
          <CalendarDays :size="16" />
          <input v-model="fechaInicio" type="date" />
        </div>
      </div>

      <div class="filter-group">
        <label>Fecha final</label>

        <div class="input-icon">
          <CalendarDays :size="16" />
          <input v-model="fechaFin" type="date" />
        </div>
      </div>


      <button class="search-button" @click="consultarReportes">
        Consultar datos
      </button>
    </section>

    <main class="reports-layout">
      <section class="main-column">
        <section class="records-card">
          <div class="card-header">
            <h2>Registros recientes</h2>
            <span>Últimas 24 horas</span>
          </div>

          <div class="table-wrapper">
            <table class="records-table">
              <thead>
                <tr>
                  <th>Fecha y hora</th>
                  <th>Sensor</th>
                  <th>Tipo</th>
                  <th>Valor</th>
                  <th>Unidad</th>
                </tr>
              </thead>

              <tbody>
                <tr v-for="registro in registros" :key="registro.id">
                  <td>{{ registro.fechaHora }}</td>
                  <td>{{ registro.sensor }}</td>
                  <td>{{ registro.tipo }}</td>
                  <td class="value-cell">{{ registro.valor }}</td>
                  <td>{{ registro.unidad }}</td>
                </tr>

                <tr v-if="registros.length === 0">
                  <td colspan="5" class="empty-state">
                    No hay registros disponibles para el rango seleccionado.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </section>

      <aside class="side-column">
        <section class="average-card">
          <div class="average-header">
            <div class="average-icon">
              <Activity :size="18" />
            </div>

            <h2>Promedio del periodo</h2>
          </div>

          <div class="average-list">
            <div class="average-row">
              <span>
                <Thermometer :size="15" />
                Temperatura
              </span>
              <strong>{{ promedioTemperatura }}°C</strong>
            </div>

            <div class="average-row">
              <span>
                <Droplets :size="15" />
                Humedad
              </span>
              <strong>{{ promedioHumedad }}%</strong>
            </div>

            <div class="average-row">
              <span>
                <Leaf :size="15" />
                Humedad suelo
              </span>
              <strong>{{ promedioHumedadSuelo }}%</strong>
            </div>
          </div>
        </section>

        <section class="status-card">
          <div class="status-icon">
            <CircleCheck :size="22" />
          </div>

          <div>
            <h2>Estado actual</h2>
            <p>Todos los sensores se encuentran transmitiendo correctamente.</p>
          </div>
        </section>

        <section class="data-card">
          <div class="data-icon">
            <Database :size="22" />
          </div>

          <div>
            <h2>Total de registros</h2>
            <strong>{{ registros.length }}</strong>
            <p>Lecturas disponibles en el rango seleccionado.</p>
          </div>
        </section>
      </aside>
    </main>
  </section>
</template>

<style scoped>
.reports-page {
  width: 100%;
  min-height: 100vh;
  background: #ffffff;
}

.page-header {
  height: 72px;
  padding: 0 34px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  margin: -32px -32px 28px;
}

.page-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #1f2937;
}

.filters-card {
  display: grid;
  grid-template-columns: 1fr 1fr 1.2fr auto;
  gap: 16px;
  align-items: end;
  margin-bottom: 24px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 13px;
  font-weight: 900;
  color: #6b7280;
}

.input-icon {
  height: 44px;
  border: 1px solid #d1d5db;
  border-radius: 9px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 13px;
  color: #64748b;
  background: #ffffff;
}

.input-icon input {
  width: 100%;
  border: none;
  outline: none;
  color: #374151;
  font-weight: 700;
  background: transparent;
}

.filter-group select {
  height: 44px;
  border: 1px solid #d1d5db;
  border-radius: 9px;
  padding: 0 13px;
  outline: none;
  color: #374151;
  background: #ffffff;
  font-weight: 700;
}

.search-button {
  height: 44px;
  border: none;
  border-radius: 9px;
  background: #08783f;
  color: #ffffff;
  padding: 0 22px;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
  transition: 0.2s ease;
  white-space: nowrap;
}

.search-button:hover {
  background: #066b37;
}

.reports-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 24px;
}

.main-column,
.side-column {
  min-width: 0;
}

.side-column {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.records-card,
.average-card,
.status-card,
.data-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
}

.records-card {
  overflow: hidden;
}

.card-header {
  padding: 18px 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  color: #374151;
}

.card-header span {
  height: 26px;
  padding: 0 12px;
  border-radius: 999px;
  background: #dcfce7;
  color: #15803d;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  font-weight: 900;
}

.table-wrapper {
  overflow-x: auto;
}

.records-table {
  width: 100%;
  border-collapse: collapse;
}

.records-table th {
  background: #f8fafc;
  color: #6b7280;
  font-size: 12px;
  font-weight: 900;
  text-align: left;
  padding: 15px 22px;
}

.records-table td {
  padding: 16px 22px;
  border-top: 1px solid #e5e7eb;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.value-cell {
  color: #0f9f58 !important;
  font-weight: 900 !important;
}

.empty-state {
  text-align: center;
  color: #94a3b8 !important;
  padding: 28px !important;
}

.average-card {
  padding: 22px;
}

.average-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 22px;
}

.average-icon,
.status-icon,
.data-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: #dcfce7;
  color: #15803d;
  display: grid;
  place-items: center;
  flex-shrink: 0;
}

.average-header h2,
.status-card h2,
.data-card h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
  color: #374151;
}

.average-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.average-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.average-row span {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
  font-weight: 700;
}

.average-row strong {
  color: #111827;
  font-size: 15px;
  font-weight: 900;
}

.status-card,
.data-card {
  padding: 22px;
  display: flex;
  gap: 14px;
}

.status-card p,
.data-card p {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.4;
  font-weight: 600;
}

.data-card strong {
  display: block;
  margin-top: 8px;
  font-size: 34px;
  line-height: 1;
  color: #08783f;
  font-weight: 900;
}

@media (max-width: 1180px) {
  .filters-card {
    grid-template-columns: 1fr 1fr;
  }

  .reports-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .page-header {
    margin: -32px -32px 24px;
    padding: 0 22px;
  }

  .filters-card {
    grid-template-columns: 1fr;
  }

  .records-table {
    min-width: 760px;
  }
}
</style>