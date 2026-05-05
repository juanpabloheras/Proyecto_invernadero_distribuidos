<script setup>
import { ref, computed, onMounted } from 'vue'
import { addAlarma, deleteAlarma, getAlarmas } from '../services/alarmas-service.js'
import AlarmaDeleteModal from '../components/AlarmaDeleteModal.vue'
import {
  PlusCircle,
  Bell,
  UserRound,
  Thermometer,
  Droplets,
  Trash2
} from 'lucide-vue-next'

const nombreAlerta = ref('')
const tipoAlarmaSeleccionado = ref('TEMPERATURA')
const operadorSeleccionado = ref('MAYOR_QUE')
const valorAlerta = ref('')
const activaSeleccionada = ref(true)
const reglas = ref([])
const isLoading = ref(false)
const errorMessage = ref('')
const selectedAlarma = ref(null)
const isDeleteModalOpen = ref(false)

const reglasActivas = computed(() => reglas.value.filter(regla => regla.activa).length)
const reglasPausadas = computed(() => reglas.value.filter(regla => !regla.activa).length)

async function loadAlarmas() {
  try {
    isLoading.value = true
    errorMessage.value = ''

    const response = await getAlarmas({
      page: 1,
      limit: 100,
      sort: 'createdAt',
      order: 'DESC'
    })

    reglas.value = response.data.data
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadAlarmas()
})

function getRuleIcon(tipoAlarma) {
  const icons = {
    HUMEDAD: Droplets,
    TEMPERATURA: Thermometer
  }

  return icons[tipoAlarma] || Bell
}

function getTipoLabel(tipoAlarma) {
  const labels = {
    HUMEDAD: 'Humedad',
    TEMPERATURA: 'Temperatura'
  }

  return labels[tipoAlarma] || tipoAlarma
}

function getOperadorLabel(operador) {
  const labels = {
    MAYOR_QUE: 'Mayor que',
    MENOR_QUE: 'Menor que',
    IGUAL_A: 'Igual a'
  }

  return labels[operador] || operador
}

function getOperadorSymbol(operador) {
  const labels = {
    MAYOR_QUE: '>',
    MENOR_QUE: '<',
    IGUAL_A: '='
  }

  return labels[operador] || operador
}

async function guardarReglaAlerta() {
  const valorCritico = Number(valorAlerta.value)

  if (!nombreAlerta.value.trim() || Number.isNaN(valorCritico)) {
    errorMessage.value = 'Completa el nombre y el valor de la alarma'
    return
  }

  try {
    await addAlarma({
      nombreAlarma: nombreAlerta.value.trim(),
      tipoAlarma: tipoAlarmaSeleccionado.value,
      operador: operadorSeleccionado.value,
      valorCritico,
      activa: activaSeleccionada.value
    })

    await loadAlarmas()
    limpiarFormulario()
  } catch (error) {
    errorMessage.value = error.message
  }
}

function openDeleteModal(regla) {
  selectedAlarma.value = regla
  isDeleteModalOpen.value = true
}

function closeDeleteModal() {
  selectedAlarma.value = null
  isDeleteModalOpen.value = false
}

async function confirmarEliminarRegla() {
  if (!selectedAlarma.value) return

  try {
    await deleteAlarma(selectedAlarma.value.idConfiguracionAlarma)
    await loadAlarmas()
    closeDeleteModal()
  } catch (error) {
    errorMessage.value = error.message
  }
}

function limpiarFormulario() {
  nombreAlerta.value = ''
  tipoAlarmaSeleccionado.value = 'TEMPERATURA'
  operadorSeleccionado.value = 'MAYOR_QUE'
  valorAlerta.value = ''
  activaSeleccionada.value = true
}
</script>

<template>
  <section class="alerts-page">
    <header class="page-header">
      <h1>Configuracion de alertas</h1>

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

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

        <div class="rules-table-card">
          <table class="rules-table">
            <thead>
              <tr>
                <th>Nombre de alerta</th>
                <th>Condicion</th>
                <th>Tipo</th>
                <th>Estado</th>
                <th class="actions-heading">Acciones</th>
              </tr>
            </thead>

            <tbody>
              <tr v-if="isLoading">
                <td colspan="5" class="empty-state">Cargando alarmas...</td>
              </tr>

              <tr v-for="regla in reglas" :key="regla.idConfiguracionAlarma">
                <td class="rule-name">{{ regla.nombreAlarma }}</td>

                <td>
                  {{ getOperadorLabel(regla.operador) }}
                  {{ regla.valorCritico }}
                  <span class="condition-symbol">
                    ({{ getOperadorSymbol(regla.operador) }})
                  </span>
                </td>

                <td>
                  <div class="sensor-cell">
                    <component :is="getRuleIcon(regla.tipoAlarma)" :size="15" />
                    <span>{{ getTipoLabel(regla.tipoAlarma) }}</span>
                  </div>
                </td>

                <td>
                  <span
                    class="status-badge"
                    :class="regla.activa ? 'active' : 'inactive'"
                  >
                    {{ regla.activa ? 'ACTIVA' : 'INACTIVA' }}
                  </span>
                </td>

                <td class="actions-cell">
                  <button class="icon-button" type="button" @click="openDeleteModal(regla)">
                    <Trash2 :size="16" />
                  </button>
                </td>
              </tr>

              <tr v-if="!isLoading && reglas.length === 0">
                <td colspan="5" class="empty-state">No hay alarmas registradas.</td>
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
                placeholder="Ej. Temperatura alta"
              />
            </div>

            <div class="form-group">
              <label>Tipo de alarma</label>
              <select v-model="tipoAlarmaSeleccionado">
                <option value="TEMPERATURA">Temperatura</option>
                <option value="HUMEDAD">Humedad</option>
              </select>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Operador</label>
                <select v-model="operadorSeleccionado">
                  <option value="MAYOR_QUE">Mayor que</option>
                  <option value="MENOR_QUE">Menor que</option>
                  <option value="IGUAL_A">Igual a</option>
                </select>
              </div>

              <div class="form-group">
                <label>Valor</label>
                <input
                  v-model="valorAlerta"
                  type="number"
                  step="0.1"
                  placeholder="0"
                />
              </div>
            </div>

            <label class="checkbox-field">
              <input v-model="activaSeleccionada" type="checkbox" />
              <span>Alarma activa</span>
            </label>

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

    <AlarmaDeleteModal
      :is-open="isDeleteModalOpen"
      :alarma="selectedAlarma"
      @close="closeDeleteModal"
      @confirm="confirmarEliminarRegla"
    />
  </section>
</template>

<style scoped>
.alerts-page {
  width: 100%;
  background: #ffffff;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
}

.page-header h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  color: #1f2937;
  letter-spacing: -0.5px;
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
  font-size: 20px;
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

.error-message {
  margin: 0 0 14px;
  color: #b91c1c;
  font-size: 14px;
  font-weight: 800;
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
  font-size: 12px;
  font-weight: 800;
  text-align: left;
  padding: 16px 22px;
}

.rules-table td {
  padding: 20px 22px;
  border-top: 1px solid #e5e7eb;
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
}

.rule-name {
  font-weight: 800 !important;
  color: #374151 !important;
}

.condition-symbol {
  color: #94a3b8;
  font-weight: 800;
}

.sensor-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sensor-cell svg {
  color: #0d8a4d;
  flex-shrink: 0;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 108px;
  height: 30px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 900;
}

.status-badge.active {
  background: #dcfce7;
  color: #15803d;
}

.status-badge.inactive {
  background: #f3f4f6;
  color: #64748b;
}

.actions-heading,
.actions-cell {
  text-align: center !important;
}

.icon-button {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #64748b;
  display: inline-grid;
  place-items: center;
  cursor: pointer;
}

.icon-button:hover {
  background: #f8fafc;
  color: #b91c1c;
}

.empty-state {
  text-align: center;
  color: #94a3b8 !important;
  font-weight: 700 !important;
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
  font-size: 18px;
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

.form-group label,
.checkbox-field span {
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

.checkbox-field {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 34px;
}

.checkbox-field input {
  width: 18px;
  height: 18px;
  accent-color: #08783f;
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
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .rules-table-card {
    overflow-x: auto;
  }

  .rules-table {
    min-width: 760px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
