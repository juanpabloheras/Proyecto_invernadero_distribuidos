<script setup>
import { ref, computed, onMounted } from 'vue'
import { getSensores } from '../services/sensores-service'
import {
  Search,
  Plus,
  Thermometer,
  Droplets,
  Pencil,
  Trash2
} from 'lucide-vue-next'

const search = ref('')
const currentPage = ref(1)
const itemsPerPage = 6
const sensors = ref([])
const isLoading = ref(false)
const errorMessage = ref('')

async function loadSensores() {
  try {
    isLoading.value = true
    errorMessage.value = ''

    const response = await getSensores({
      page: 1,
      limit: 100,
      sort: 'createdAt',
      order: 'DESC'
    })

    sensors.value = response.data.data
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadSensores()
})

const filteredSensors = computed(() => {
  const term = search.value.trim().toLowerCase()

  if (!term) return sensors.value

  return sensors.value.filter(sensor =>
    String(sensor.idSensor).includes(term) ||
    sensor.nombre.toLowerCase().includes(term) ||
    getTypeLabel(sensor.tipo).toLowerCase().includes(term)
  )
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredSensors.value.length / itemsPerPage))
})

const paginatedSensors = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredSensors.value.slice(start, end)
})

const activeCount = computed(() =>
  sensors.value.filter(sensor => sensor.activo).length
)

const maintenanceCount = computed(() =>
  sensors.value.filter(sensor => !sensor.activo).length
)

const connectivityPercentage = computed(() => {
  if (!sensors.value.length) return 0
  return Math.round((activeCount.value / sensors.value.length) * 100)
})

function getTypeLabel(type) {
  const labels = {
    TEMPERATURA: 'Temperatura',
    HUMEDAD: 'Humedad',
    HUMEDAD_SUELO: 'Humedad del suelo'
  }

  return labels[type] || type
}

function getTypeIcon(type) {
  if (type === 'TEMPERATURA') return Thermometer
  return Droplets
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

function onAddSensor() {
  console.log('Abrir modal o navegar a formulario para nuevo sensor')
}

function onEdit(sensor) {
  console.log('Editar sensor:', sensor)
}

function onDelete(sensor) {
  console.log('Eliminar sensor:', sensor)
}
</script>

<template>
  <section class="sensors-page">
    <header class="page-header">
      <h1>Gestión de Sensores</h1>
    </header>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <section class="table-card">
      <div class="toolbar">
        <div class="search-box">
          <Search :size="16" />
          <input
            v-model="search"
            type="text"
            placeholder="Busca sensores por nombre o ID..."
          />
        </div>

        <button class="add-button" @click="onAddSensor">
          <Plus :size="18" />
          Agregar Sensor
        </button>
      </div>

      <div class="table-wrapper">
        <table class="sensors-table">
          <colgroup>
            <col class="id-column" />
            <col class="name-column" />
            <col class="type-column" />
            <col class="status-column" />
            <col class="actions-column-width" />
          </colgroup>

          <thead>
            <tr>
              <th class="id-cell">ID</th>
              <th>Nombre</th>
              <th>Tipo</th>
              <th class="status-cell">Estado</th>
              <th class="actions-cell">ACCIONES</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="isLoading">
              <td colspan="5" class="empty-state">
                Cargando sensores...
              </td>
            </tr>

            <tr v-for="sensor in paginatedSensors" :key="sensor.idSensor">
              <td class="sensor-id id-cell">{{ sensor.idSensor }}</td>

              <td>{{ sensor.nombre }}</td>

              <td>
                <div class="type-cell">
                  <component
                    :is="getTypeIcon(sensor.tipo)"
                    :size="16"
                    class="type-icon"
                  />
                  <span>{{ getTypeLabel(sensor.tipo) }}</span>
                </div>
              </td>

              <td class="status-cell">
                <span
                  class="status-badge"
                  :class="sensor.activo ? 'active' : 'inactive'"
                >
                  {{ sensor.activo ? 'ACTIVO' : 'INACTIVO' }}
                </span>
              </td>

              <td class="actions-cell">
                <div class="actions">
                  <button class="icon-btn" @click="onEdit(sensor)">
                    <Pencil :size="15" />
                  </button>

                  <button class="icon-btn" @click="onDelete(sensor)">
                    <Trash2 :size="15" />
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="!isLoading && paginatedSensors.length === 0">
              <td colspan="5" class="empty-state">
                No sensores encontrados...
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-footer">
        <p>Mostrando {{ paginatedSensors.length }} de {{ filteredSensors.length }} sensores</p>

        <div class="pagination">
          <button class="page-arrow" @click="previousPage" :disabled="currentPage === 1">
            ‹
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
            ›
          </button>
        </div>
      </div>
    </section>

    
  </section>
</template>

<style scoped>
.sensors-page {
  width: 100%;
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

.table-card {
  background: #eef5ec;
  border: 1px solid #dde8da;
  border-radius: 16px;
  padding: 22px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.search-box {
  width: 100%;
  max-width: 420px;
  height: 46px;
  background: #ffffff;
  border: 1px solid #d5dfd2;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  color: #94a3b8;
}

.search-box input {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #334155;
}

.add-button {
  height: 46px;
  border: none;
  border-radius: 10px;
  background: #0a7a43;
  color: #ffffff;
  padding: 0 18px;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  white-space: nowrap;
  transition: 0.2s ease;
}

.add-button:hover {
  background: #086737;
}

.table-wrapper {
  overflow-x: auto;
  background: #ffffff;
  border: 1px solid #dce5d9;
  border-radius: 14px 14px 0 0;
}

.sensors-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.id-column {
  width: 8%;
}

.name-column {
  width: 40%;
}

.type-column {
  width: 24%;
}

.status-column {
  width: 16%;
}

.actions-column-width {
  width: 12%;
}

.sensors-table thead th {
  text-align: left;
  font-size: 12px;
  font-weight: 800;
  color: #6b7280;
  padding: 16px 20px;
  border-bottom: 1px solid #e5ece3;
  vertical-align: middle;
}

.sensors-table tbody td {
  padding: 20px;
  border-bottom: 1px solid #e5ece3;
  font-size: 14px;
  color: #374151;
  font-weight: 600;
  vertical-align: middle;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sensor-id {
  font-weight: 800;
  color: #334155;
}

.id-cell {
  padding-left: 20px !important;
}

.type-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 150px;
}

.type-icon {
  color: #0d8a4d;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 116px;
  height: 30px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 900;
}

.status-badge.active {
  background: #dcfce7;
  color: #1f9d57;
}

.status-badge.inactive {
  background: #ecf0eb;
  color: #6b7280;
}

.status-cell,
.actions-cell {
  text-align: center !important;
  padding-left: 12px !important;
  padding-right: 12px !important;
}

.actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 92px;
}

.icon-btn {
  width: 30px;
  height: 30px;
  border: none;
  background: transparent;
  color: #6b7280;
  border-radius: 8px;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: 0.2s ease;
}

.icon-btn:hover {
  background: #f3f6f2;
  color: #111827;
}

.empty-state {
  text-align: center;
  color: #94a3b8;
  font-weight: 600;
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

.summary-grid {
  margin-top: 22px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

.summary-card {
  background: #ffffff;
  border: 1px solid #dce5d9;
  border-radius: 14px;
  padding: 22px;
}

.summary-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.summary-top h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: #374151;
}

.summary-icon.success {
  color: #1f9d57;
}

.summary-icon.danger {
  color: #cc4357;
}

.summary-icon.neutral {
  color: #4b5563;
}

.summary-card h2 {
  margin: 0 0 6px;
  font-size: 44px;
  line-height: 1;
  font-weight: 800;
  color: #111827;
}

.summary-card p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
  font-weight: 600;
}

.danger-number {
  color: #cc4357 !important;
}

@media (max-width: 1100px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 780px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: none;
  }

  .table-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
