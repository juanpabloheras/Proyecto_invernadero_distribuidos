<script setup>
import { computed, onMounted, ref } from 'vue'
import { getLecturas, getLecturasPorFechas } from '../services/historical-service.js'
import {
  CalendarDays,
  Thermometer,
  Droplets,
  Activity,
  Database,
  CircleCheck
} from 'lucide-vue-next'

const fechaInicio = ref('')
const fechaFin = ref('')
const registros = ref([])
const totalLecturas = ref(0)
const currentPage = ref(1)
const itemsPerPage = 10
const isLoading = ref(false)
const errorMessage = ref('')

const promedioTemperatura = computed(() => getAverageByType('Temperatura'))
const promedioHumedad = computed(() => getAverageByType('Humedad'))
const totalMediciones = computed(() => registros.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(totalMediciones.value / itemsPerPage)))
const paginatedRegistros = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return registros.value.slice(start, start + itemsPerPage)
})

async function consultarReportes(page = 1) {
  try {
    isLoading.value = true
    errorMessage.value = ''
    currentPage.value = page

    const lecturas = await fetchAllLecturas()

    totalLecturas.value = lecturas.length
    registros.value = mapLecturasToRegistros(lecturas)
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    isLoading.value = false
  }
}

async function fetchAllLecturas() {
  const size = 100
  const firstPage = await fetchLecturasPage({
    pagina: 0,
    size
  })

  const lecturas = [...(firstPage.datos || [])]
  const totalPaginas = firstPage.totalPaginas || 1

  for (let pagina = 1; pagina < totalPaginas; pagina += 1) {
    const response = await fetchLecturasPage({
      pagina,
      size
    })

    lecturas.push(...(response.datos || []))
  }

  return lecturas
}

function fetchLecturasPage(params) {
  if (!fechaInicio.value && !fechaFin.value) {
    return getLecturas(params)
  }

  return getLecturasPorFechas({
    ...params,
    inicio: buildDateBoundary(fechaInicio.value, 'start'),
    fin: buildDateBoundary(fechaFin.value, 'end')
  })
}

function buildDateBoundary(value, boundary) {
  if (!value) {
    return boundary === 'start'
      ? '1970-01-01T00:00:00.000Z'
      : new Date().toISOString()
  }

  const time = boundary === 'start' ? 'T00:00:00.000' : 'T23:59:59.999'
  return new Date(`${value}${time}`).toISOString()
}

function mapLecturasToRegistros(lecturas) {
  return lecturas.flatMap(lectura => {
    if (hasFlatReading(lectura)) {
      return [mapLecturaPlanaToRegistro(lectura)]
    }

    return (lectura.mediciones || [])
      .filter(medicion => medicion.esValido && medicion.valorNumerico !== null)
      .map((medicion, index) => ({
        id: `${lectura.id || lectura.sensorId}-${lectura.timestamp}-${index}`,
        timestamp: lectura.timestamp,
        fechaHora: formatDateTime(lectura.timestamp),
        sensor: lectura.sensorId,
        tipo: normalizeTipo(medicion.tipo),
        valor: medicion.valorNumerico,
        unidad: normalizeUnidad(medicion.unidad)
      }))
  })
}

function hasFlatReading(lectura) {
  return Boolean(lectura.fecha || lectura.tipoSensor) &&
    lectura.valor !== null &&
    lectura.valor !== undefined
}

function mapLecturaPlanaToRegistro(lectura) {
  return {
    id: lectura.id,
    timestamp: lectura.fecha,
    fechaHora: formatDateTime(lectura.fecha),
    sensor: lectura.sensorId || 'Sensor historico',
    tipo: normalizeTipo(lectura.tipoSensor),
    valor: lectura.valor,
    unidad: normalizeUnidad(lectura.unidad)
  }
}

function getAverageByType(tipo) {
  const datos = registros.value.filter(registro => registro.tipo === tipo)

  if (!datos.length) return 0

  const suma = datos.reduce((total, registro) => total + registro.valor, 0)
  return (suma / datos.length).toFixed(1)
}

function formatDateTime(timestamp) {
  return new Date(timestamp).toLocaleString('es-MX', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function normalizeTipo(tipo) {
  const value = tipo?.toLowerCase() || ''

  if (value.includes('temperatura')) return 'Temperatura'
  if (value.includes('humedad') && value.includes('suelo')) return 'Humedad de suelo'
  if (value.includes('humedad')) return 'Humedad'

  return tipo || 'Desconocido'
}

function normalizeUnidad(unidad) {
  if (!unidad) return ''
  if (unidad.includes('C')) return 'C'
  return unidad
}

function previousPage() {
  if (currentPage.value > 1) currentPage.value--
}

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

function goToPage(page) {
  currentPage.value = page
}

onMounted(() => {
  consultarReportes()
})
</script>

<template>
  <section class="reports-page">
    <header class="page-header">
      <h1>Reportes y analisis</h1>
    </header>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

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

      <button class="search-button" @click="consultarReportes(1)">
        Consultar datos
      </button>
    </section>

    <main class="reports-layout">
      <section class="main-column">
        <section class="records-card">
          <div class="card-header">
            <h2>Registros recientes</h2>
            <span>{{ totalMediciones }} mediciones</span>
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
                <tr v-if="isLoading">
                  <td colspan="5" class="empty-state">
                    Cargando registros...
                  </td>
                </tr>

                <tr v-for="registro in paginatedRegistros" :key="registro.id">
                  <td>{{ registro.fechaHora }}</td>
                  <td>{{ registro.sensor }}</td>
                  <td>{{ registro.tipo }}</td>
                  <td class="value-cell">{{ registro.valor }}</td>
                  <td>{{ registro.unidad }}</td>
                </tr>

                <tr v-if="!isLoading && paginatedRegistros.length === 0">
                  <td colspan="5" class="empty-state">
                    No hay registros disponibles para el rango seleccionado.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="table-footer">
            <p>
              Mostrando {{ paginatedRegistros.length }} de {{ totalMediciones }} mediciones
              en {{ totalLecturas }} lecturas
            </p>

            <div class="pagination">
              <button class="page-arrow" @click="previousPage" :disabled="currentPage === 1">
                &lsaquo;
              </button>

              <button
                v-for="page in totalPages"
                :key="page"
                class="page-number"
                :class="{ active: page === currentPage }"
                @click="goToPage(page)"
              >
                {{ page }}
              </button>

              <button class="page-arrow" @click="nextPage" :disabled="currentPage === totalPages">
                &rsaquo;
              </button>
            </div>
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
              <strong>{{ promedioTemperatura }} C</strong>
            </div>

            <div class="average-row">
              <span>
                <Droplets :size="15" />
                Humedad
              </span>
              <strong>{{ promedioHumedad }}%</strong>
            </div>

          </div>
        </section>

        <section class="status-card">
          <div class="status-icon">
            <CircleCheck :size="22" />
          </div>

          <div>
            <h2>Estado actual</h2>
            <p>Datos historicos consultados desde MongoDB.</p>
          </div>
        </section>

        <section class="data-card">
          <div class="data-icon">
            <Database :size="22" />
          </div>

          <div>
            <h2>Total de lecturas</h2>
            <strong>{{ totalLecturas }}</strong>
            <p>Eventos historicos disponibles.</p>
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
  margin-bottom: 22px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  color: #1f2937;
  letter-spacing: -0.5px;
}

.error-message {
  margin: 0 0 16px;
  color: #b91c1c;
  font-size: 14px;
  font-weight: 700;
}

.filters-card {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
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

.average-card,
.status-card,
.data-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
}

.records-card {
  background: #eef5ec;
  border: 1px solid #dde8da;
  border-radius: 16px;
  padding: 22px;
}

.card-header {
  padding: 0 0 18px;
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
  background: #ffffff;
  border: 1px solid #dce5d9;
  border-radius: 14px 14px 0 0;
}

.records-table {
  width: 100%;
  border-collapse: collapse;
}

.records-table th {
  background: #ffffff;
  color: #6b7280;
  font-size: 12px;
  font-weight: 800;
  text-align: left;
  padding: 16px 20px;
  border-bottom: 1px solid #e5ece3;
  vertical-align: middle;
}

.records-table td {
  padding: 20px;
  border-bottom: 1px solid #e5ece3;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
  vertical-align: middle;
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

.table-footer {
  background: #ffffff;
  border: 1px solid #dce5d9;
  border-top: none;
  border-radius: 0 0 14px 14px;
  padding: 14px 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
}

.table-footer p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 6px;
}

.page-number,
.page-arrow {
  min-width: 32px;
  height: 32px;
  border: 1px solid #d6dfd3;
  background: #ffffff;
  border-radius: 8px;
  color: #4b5563;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s ease;
}

.page-number.active {
  background: #0a7a43;
  color: #ffffff;
  border-color: #0a7a43;
}

.page-arrow:disabled {
  opacity: 0.45;
  cursor: not-allowed;
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
  .filters-card {
    grid-template-columns: 1fr;
  }

  .records-table {
    min-width: 760px;
  }

  .table-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
