<script setup>
import { computed, onMounted, ref } from 'vue'
import { getAlarmas } from '../services/alarmas-service.js'
import { getLecturas } from '../services/historical-service.js'
import { getSensores } from '../services/sensores-service.js'
import {
  Thermometer,
  Droplets
} from 'lucide-vue-next'

import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,
  Filler
} from 'chart.js'

import { Line } from 'vue-chartjs'

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,
  Filler
)

const sensoresActivos = ref(0)
const totalAlarmas = ref(0)
const statsError = ref('')
const lecturasGrafica = ref([])

async function loadHomeStats() {
  try {
    statsError.value = ''

    const [sensoresResponse, alarmasResponse] = await Promise.all([
      getSensores({
        page: 1,
        limit: 1,
        activo: true
      }),
      getAlarmas({
        page: 1,
        limit: 1
      })
    ])

    sensoresActivos.value = sensoresResponse.data.total
    totalAlarmas.value = alarmasResponse.data.total
  } catch (error) {
    statsError.value = error.message
  }

  try {
    const lecturasResponse = await getLecturas({
      pagina: 0,
      size: 30
    })

    lecturasGrafica.value = lecturasResponse.datos || []
  } catch (error) {
    statsError.value = error.message
  }
}

onMounted(() => {
  loadHomeStats()
})

const chartPoints = computed(() => {
  return lecturasGrafica.value
    .flatMap(lectura => {
      if (hasFlatReading(lectura)) {
        return normalizeFlatChartPoint(lectura)
      }

      return (lectura.mediciones || [])
        .filter(medicion =>
          medicion.esValido &&
          medicion.valorNumerico !== null &&
          normalizeReadingType(medicion.tipo)
        )
        .map(medicion => ({
          timestamp: lectura.timestamp,
          type: normalizeReadingType(medicion.tipo),
          value: medicion.valorNumerico
        }))
    })
    .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
    .slice(-16)
})

function hasFlatReading(lectura) {
  return Boolean(lectura.fecha || lectura.tipoSensor) &&
    lectura.valor !== null &&
    lectura.valor !== undefined
}

function normalizeFlatChartPoint(lectura) {
  const type = normalizeReadingType(lectura.tipoSensor)

  if (!type || lectura.valor === null || lectura.valor === undefined) {
    return []
  }

  return [{
    timestamp: lectura.fecha,
    type,
    value: lectura.valor
  }]
}

const chartData = computed(() => {
  const points = chartPoints.value
  const hasData = points.length > 0
  const labels = hasData
    ? [...new Set(points.map(point => formatChartTime(point.timestamp)))]
    : ['Sin datos']

  return {
    labels,
    datasets: [
      buildDataset({
        label: 'Temperatura (C)',
        type: 'Temperatura',
        labels,
        points,
        borderColor: '#45c876',
        backgroundColor: 'rgba(69, 200, 118, 0.12)',
        hasData
      }),
      buildDataset({
        label: 'Humedad (%)',
        type: 'Humedad',
        labels,
        points,
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59, 130, 246, 0.08)',
        hasData
      })
    ]
  }
})

const chartValueSummary = computed(() => {
  const temperatureValues = chartPoints.value
    .filter(point => point.type === 'Temperatura')
    .map(point => point.value)
  const humidityValues = chartPoints.value
    .filter(point => point.type === 'Humedad')
    .map(point => point.value)

  return {
    temperatura: getLatestValue(temperatureValues),
    humedad: getLatestValue(humidityValues)
  }
})

function buildDataset({ label, type, labels, points, borderColor, backgroundColor, hasData }) {
  return {
    label,
    data: hasData
      ? labels.map(labelTime => {
          const point = points.find(item =>
            item.type === type &&
            formatChartTime(item.timestamp) === labelTime
          )

          return point?.value ?? null
        })
      : [0],
    borderColor,
    backgroundColor,
    fill: false,
    tension: 0.45,
    pointRadius: hasData ? 3 : 0,
    borderWidth: 3,
    spanGaps: true
  }
}

function normalizeReadingType(type) {
  const value = type?.toLowerCase() || ''

  if (value.includes('temperatura')) return 'Temperatura'
  if (value.includes('humedad')) return 'Humedad'

  return null
}

function getLatestValue(values) {
  if (!values.length) return '--'

  return values[values.length - 1].toFixed(1)
}

function formatChartTime(timestamp) {
  return new Date(timestamp).toLocaleTimeString('es-MX', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: 'top',
      align: 'end',
      labels: {
        usePointStyle: true,
        pointStyle: 'circle',
        boxWidth: 6,
        boxHeight: 6,
        color: '#94a3b8',
        font: {
          size: 11,
          weight: '600'
        }
      }
    },
    tooltip: {
      enabled: true,
      callbacks: {
        label(context) {
          const unit = context.dataset.label.includes('Humedad') ? '%' : ' C'
          return `${context.dataset.label}: ${context.parsed.y}${unit}`
        }
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: '#9ca3af',
        font: {
          size: 10
        }
      },
      border: {
        display: false
      }
    },
    y: {
      display: true,
      min: 0,
      max: 100,
      grid: {
        color: '#edf2f7'
      },
      ticks: {
        color: '#9ca3af',
        font: {
          size: 10
        },
        callback(value) {
          return `${value}`
        }
      },
      border: {
        display: false
      }
    }
  }
}
</script>

<template>
  <section class="home-page">
    <header class="home-header">
      <div>
        <h1>Hola, Administrador</h1>
      </div>

    </header>

    <section class="stats-grid">
      <article class="stat-card">
        <div class="stat-icon temperature">
          <Thermometer :size="20" />
        </div>

        <div>
          <p class="stat-label">Sensores activos</p>

          <div class="stat-value-row">
            <h2>{{ sensoresActivos }}</h2>
          </div>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon humidity">
          <Droplets :size="20" />
        </div>

        <div>
          <p class="stat-label">Alarmas registradas</p>

          <div class="stat-value-row">
            <h2>{{ totalAlarmas }}</h2>
          </div>
        </div>
      </article>

    </section>

    <p v-if="statsError" class="error-message">{{ statsError }}</p>

    <section class="chart-card">
      <div class="chart-header">
        <div>
          <h2>Temperatura y humedad recientes</h2>
          <p>Ultimas lecturas registradas por los sensores</p>
        </div>

        <div class="chart-metrics">
          <span>
            <strong>{{ chartValueSummary.temperatura }}</strong>
            C temp.
          </span>
          <span>
            <strong>{{ chartValueSummary.humedad }}</strong>
            % humedad
          </span>
        </div>
      </div>

      <div class="chart-wrapper">
        <Line :data="chartData" :options="chartOptions" />
      </div>

    </section>
  </section>
</template>

<style scoped>
.home-page {
  width: 100%;
  min-height: 100vh;
  background: #ffffff;
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 34px;
}

.home-header h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  color: #1f2937;
  letter-spacing: -0.7px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
  margin-bottom: 28px;
}

.stat-card {
  min-height: 104px;
  background: #ffffff;
  border: 1px solid #e5ebf2;
  border-radius: 12px;
  padding: 22px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  display: grid;
  place-items: center;
}

.stat-icon.temperature {
  background: #f0fdf4;
  color: #45c876;
}

.stat-icon.humidity {
  background: #eff6ff;
  color: #3b82f6;
}

.stat-label {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 800;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.6px;
}

.stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.stat-value-row h2 {
  margin: 0;
  font-size: 32px;
  line-height: 1;
  font-weight: 800;
  color: #1f2937;
}

.success-text {
  font-size: 12px;
  font-weight: 800;
  color: #2fbf71;
}

.error-message {
  margin: -12px 0 24px;
  color: #b91c1c;
  font-size: 14px;
  font-weight: 700;
}

.chart-card {
  background: #ffffff;
  border: 1px solid #e5ebf2;
  border-radius: 12px;
  padding: 26px 28px 18px;
  margin-bottom: 28px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  margin-bottom: 18px;
}

.chart-header h2 {
  margin: 0;
  font-size: 19px;
  font-weight: 800;
  color: #334155;
}

.chart-header p {
  margin: 5px 0 0;
  color: #94a3b8;
  font-size: 13px;
  font-weight: 600;
}

.chart-metrics {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.chart-metrics span {
  min-width: 102px;
  height: 42px;
  border: 1px solid #e5ebf2;
  border-radius: 10px;
  display: grid;
  align-content: center;
  padding: 0 12px;
  color: #94a3b8;
  font-size: 11px;
  font-weight: 800;
}

.chart-metrics strong {
  display: block;
  color: #1f2937;
  font-size: 18px;
  line-height: 1;
  font-weight: 900;
}

.chart-wrapper {
  width: 100%;
  height: 290px;
}

.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.info-card {
  background: #ffffff;
  border: 1px solid #e5ebf2;
  border-radius: 12px;
  padding: 20px 22px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
}

.info-card h3 {
  margin: 0 0 6px;
  font-size: 15px;
  font-weight: 800;
  color: #1f2937;
}

.info-card p {
  margin: 0;
  max-width: 420px;
  font-size: 13px;
  line-height: 1.4;
  font-weight: 600;
  color: #94a3b8;
}

.status-badge {
  height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 900;
  display: flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
}

.status-badge.online {
  background: #dcfce7;
  color: #22a462;
}

.status-badge.pending {
  background: #ffe4e6;
  color: #cc4357;
}

@media (max-width: 1050px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .bottom-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .home-header {
    align-items: flex-start;
    gap: 16px;
  }

  .home-header h1 {
    font-size: 24px;
  }

  .chart-wrapper {
    height: 230px;
  }

  .chart-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .chart-metrics {
    justify-content: flex-start;
  }

}
</style>
