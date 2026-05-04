<script setup>
import { ref, computed } from 'vue'
import {
  PlusCircle,
  Bell,
  UserRound,
  Thermometer,
  Droplets,
  Leaf,
  Wind
} from 'lucide-vue-next'

const nombreAlerta = ref('')
const sensorSeleccionado = ref('Temperatura - Zona A')
const operadorSeleccionado = ref('Mayor que')
const valorAlerta = ref('')

const reglas = ref([
  {
    id: 1,
    nombre: 'Humedad crítica',
    condicion: 'Humedad < 40%',
    sensor: 'Zona A-1',
    tipo: 'humidity',
    activa: true
  },
  {
    id: 2,
    nombre: 'Advertencia de sobrecalentamiento',
    condicion: 'Temperatura > 32°C',
    sensor: 'Zona B-4',
    tipo: 'temperature',
    activa: true
  },
  {
    id: 3,
    nombre: 'Caída nocturna del suelo',
    condicion: 'Humedad de suelo < 15%',
    sensor: 'Zona A-2',
    tipo: 'soil',
    activa: false
  },
  {
    id: 4,
    nombre: 'Pico de CO2',
    condicion: 'CO2 > 1200 ppm',
    sensor: 'Ventilación principal',
    tipo: 'air',
    activa: true
  }
])

const reglasActivas = computed(() => reglas.value.filter(regla => regla.activa).length)
const reglasPausadas = computed(() => reglas.value.filter(regla => !regla.activa).length)

function getRuleIcon(tipo) {
  const icons = {
    humidity: Droplets,
    temperature: Thermometer,
    soil: Leaf,
    air: Wind
  }

  return icons[tipo] || Bell
}

function toggleRule(regla) {
  regla.activa = !regla.activa
}

function guardarReglaAlerta() {
  if (!nombreAlerta.value.trim() || !valorAlerta.value.trim()) {
    alert('Completa el nombre y el valor de la alarma')
    return
  }

  const nuevaRegla = {
    id: Date.now(),
    nombre: nombreAlerta.value,
    condicion: `${sensorSeleccionado.value.split(' - ')[0]} ${operadorSeleccionado.value === 'Mayor que' ? '>' : '<'} ${valorAlerta.value}`,
    sensor: sensorSeleccionado.value.split(' - ')[1] || 'Zona A',
    tipo: sensorSeleccionado.value.toLowerCase().includes('humedad')
      ? 'humidity'
      : sensorSeleccionado.value.toLowerCase().includes('suelo')
        ? 'soil'
        : 'temperature',
    activa: true
  }

  reglas.value.push(nuevaRegla)
  limpiarFormulario()
}

function limpiarFormulario() {
  nombreAlerta.value = ''
  sensorSeleccionado.value = 'Temperatura - Zona A'
  operadorSeleccionado.value = 'Mayor que'
  valorAlerta.value = ''
}
</script>

<template>
  <section class="alerts-page">
    <header class="page-header">
      <h1>Configuración de alertas</h1>

      <div class="header-actions">
        <button class="settings-button">
          Ajustes
        </button>

        <div class="header-avatar">
          <UserRound :size="18" />
        </div>
      </div>
    </header>

    <main class="alerts-layout">
      <section class="rules-section">
        <div class="section-header">
          <h2>Reglas activas</h2>

          <div class="rule-counters">
            <span class="counter active">{{ reglasActivas }} activas</span>
            <span class="counter paused">{{ reglasPausadas }} pausadas</span>
          </div>
        </div>

        <div class="rules-table-card">
          <table class="rules-table">
            <thead>
              <tr>
                <th>Nombre de alerta</th>
                <th>Condición</th>
                <th>Sensor</th>
                <th>Estado</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="regla in reglas" :key="regla.id">
                <td class="rule-name">{{ regla.nombre }}</td>

                <td>{{ regla.condicion }}</td>

                <td>
                  <div class="sensor-cell">
                    <component :is="getRuleIcon(regla.tipo)" :size="15" />
                    <span>{{ regla.sensor }}</span>
                  </div>
                </td>

                <td>
                  <button
                    class="toggle"
                    :class="{ active: regla.activa }"
                    @click="toggleRule(regla)"
                  >
                    <span></span>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="create-panel">
        <section class="form-card">
          <div class="form-title">
            <PlusCircle :size="21" />
            <h2>Crear nueva alerta</h2>
          </div>

          <form class="alert-form" @submit.prevent="guardarReglaAlerta">
            <div class="form-group">
              <label>Nombre de la alerta</label>
              <input
                v-model="nombreAlerta"
                type="text"
                placeholder="Ej. Advertencia de poca luz"
              />
            </div>

            <div class="form-group">
              <label>Selección de sensor</label>
              <select v-model="sensorSeleccionado">
                <option>Temperatura - Zona A</option>
                <option>Temperatura - Zona B</option>
                <option>Humedad - Zona A</option>
                <option>Humedad de suelo - Zona C</option>
              </select>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Operador</label>
                <select v-model="operadorSeleccionado">
                  <option>Mayor que</option>
                  <option>Menor que</option>
                </select>
              </div>

              <div class="form-group">
                <label>Valor</label>
                <input
                  v-model="valorAlerta"
                  type="number"
                  placeholder="0"
                />
              </div>
            </div>

            <div class="divider"></div>

            <button type="submit" class="save-button">
              Guardar regla
            </button>

            <button type="button" class="discard-button" @click="limpiarFormulario">
              Descartar
            </button>
          </form>
        </section>
      </aside>
    </main>
  </section>
</template>

<style scoped>
.alerts-page {
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
  justify-content: space-between;
  margin: -32px -32px 32px;
}

.page-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #1f2937;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.settings-button {
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 800;
  color: #64748b;
  cursor: pointer;
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #dcfce7;
  color: #15803d;
  display: grid;
  place-items: center;
}

.alerts-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 24px;
}

.rules-section {
  min-width: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #374151;
}

.rule-counters {
  display: flex;
  align-items: center;
  gap: 8px;
}

.counter {
  height: 27px;
  padding: 0 14px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  font-weight: 900;
}

.counter.active {
  background: #dcfce7;
  color: #15803d;
}

.counter.paused {
  background: #f3f4f6;
  color: #4b5563;
}

.rules-table-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
}

.rules-table {
  width: 100%;
  border-collapse: collapse;
}

.rules-table th {
  background: #f8fafc;
  color: #6b7280;
  font-size: 13px;
  font-weight: 900;
  text-align: left;
  padding: 16px 22px;
}

.rules-table td {
  padding: 20px 22px;
  border-top: 1px solid #e5e7eb;
  font-size: 16px;
  font-weight: 600;
  color: #4b5563;
}

.rule-name {
  font-weight: 800 !important;
  color: #374151 !important;
}

.sensor-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sensor-cell svg {
  color: #475569;
  flex-shrink: 0;
}

.toggle {
  width: 48px;
  height: 26px;
  border: none;
  border-radius: 999px;
  background: #cbd5c0;
  padding: 3px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: 0.2s ease;
}

.toggle span {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #ffffff;
  display: block;
  transition: 0.2s ease;
}

.toggle.active {
  background: #48c878;
}

.toggle.active span {
  transform: translateX(22px);
}

.create-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
}

.form-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 22px;
  color: #0f8b4c;
}

.form-title h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #374151;
}

.alert-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
  min-width: 0;
}

.form-group label {
  font-size: 13px;
  font-weight: 900;
  color: #6b7280;
}

.form-group input,
.form-group select {
  width: 100%;
  min-width: 0;
  box-sizing: border-box;
  height: 42px;
  border: 1px solid #d1d5db;
  border-radius: 9px;
  padding: 0 12px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  background: #ffffff;
  outline: none;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #48c878;
  box-shadow: 0 0 0 3px rgba(72, 200, 120, 0.15);
}

.form-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 110px);
  gap: 12px;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 10px 0 8px;
}

.save-button,
.discard-button {
  width: 100%;
  height: 46px;
  border-radius: 9px;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;
  transition: 0.2s ease;
}

.save-button {
  border: none;
  background: #08783f;
  color: #ffffff;
}

.save-button:hover {
  background: #066b37;
}

.discard-button {
  border: 1px solid #d1d5db;
  background: #ffffff;
  color: #6b7280;
}

.discard-button:hover {
  background: #f8fafc;
  color: #475569;
}

@media (max-width: 1150px) {
  .alerts-layout {
    grid-template-columns: 1fr;
  }

  .create-panel {
    max-width: none;
  }
}

@media (max-width: 760px) {
  .page-header {
    margin: -32px -32px 24px;
    padding: 0 22px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .rules-table-card {
    overflow-x: auto;
  }

  .rules-table {
    min-width: 720px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>