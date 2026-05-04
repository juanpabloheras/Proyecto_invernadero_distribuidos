<script setup>
import {
  Bell,
  Settings,
  TrendingUp,
  Clock,
  Thermometer,
  Droplets,
  AlertTriangle,
  CheckCircle
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

const chartData = {
  labels: ['06:00 AM', '10:00 AM', '02:00 PM', '06:00 PM', '10:00 PM'],
  datasets: [
    {
      label: 'Tendencia de Datos',
      data: [18, 23, 22, 19, 28],
      borderColor: '#45c876',
      backgroundColor: 'rgba(69, 200, 118, 0.12)',
      fill: true,
      tension: 0.45,
      pointRadius: 0,
      borderWidth: 3
    }
  ]
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
      enabled: true
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
      display: false,
      grid: {
        display: false
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

      <div class="header-actions">
        <button class="icon-button">
          <Bell :size="20" />
        </button>

        <button class="icon-button">
          <Settings :size="20" />
        </button>
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
            <h2>23</h2>
          </div>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon humidity">
          <Droplets :size="20" />
        </div>

        <div>
          <p class="stat-label">Alarmas activas</p>

          <div class="stat-value-row">
            <h2>8</h2>
          </div>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon alert">
          <AlertTriangle :size="20" />
        </div>

        <div>
          <p class="stat-label">Alertas Activas</p>

          <div class="stat-value-row">
            <h2 class="danger-number">2</h2>
            <span class="danger-text">Atención</span>
          </div>
        </div>
      </article>
    </section>

    <section class="chart-card">
      <div class="chart-header">
        <h2>Historial del Día</h2>
      </div>

      <div class="chart-wrapper">
        <Line :data="chartData" :options="chartOptions" />
      </div>

      <div class="chart-footer">
        <span>
          <TrendingUp :size="14" />
          +12% vs ayer
        </span>

        <span>
          <Clock :size="14" />
          Actualizado hace 5 min
        </span>
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.icon-button {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: #94a3b8;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: 0.2s ease;
}

.icon-button:hover {
  background: #f1f5f9;
  color: #334155;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

.stat-icon.alert {
  background: #fff1f2;
  color: #e05262;
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

.danger-number {
  color: #cc4357 !important;
}

.danger-text {
  font-size: 12px;
  font-weight: 800;
  color: #cc4357;
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
  margin-bottom: 18px;
}

.chart-header h2 {
  margin: 0;
  font-size: 19px;
  font-weight: 800;
  color: #334155;
}

.chart-wrapper {
  width: 100%;
  height: 290px;
}

.chart-footer {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 28px;
  color: #94a3b8;
  font-size: 13px;
  font-weight: 600;
}

.chart-footer span {
  display: flex;
  align-items: center;
  gap: 6px;
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

  .chart-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>