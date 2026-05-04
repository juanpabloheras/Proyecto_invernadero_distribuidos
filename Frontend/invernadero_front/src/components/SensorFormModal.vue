<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'create'
  },
  sensor: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const form = ref({
  nombre: '',
  tipo: 'TEMPERATURA',
  activo: true
})

const title = computed(() =>
  props.mode === 'edit' ? 'Editar sensor' : 'Agregar sensor'
)

watch(
  () => [props.isOpen, props.sensor, props.mode],
  () => {
    if (props.mode === 'edit' && props.sensor) {
      form.value = {
        nombre: props.sensor.nombre,
        tipo: props.sensor.tipo,
        activo: Boolean(props.sensor.activo)
      }
      return
    }

    form.value = {
      nombre: '',
      tipo: 'TEMPERATURA',
      activo: true
    }
  },
  { immediate: true }
)

function onSubmit() {
  if (!form.value.nombre.trim()) return

  emit('save', {
    nombre: form.value.nombre.trim(),
    tipo: form.value.tipo,
    activo: form.value.activo
  })
}
</script>

<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="emit('close')">
    <section class="sensor-modal">
      <header class="modal-header">
        <div>
          <p class="modal-eyebrow">Sensor</p>
          <h2>{{ title }}</h2>
        </div>

        <button class="modal-close" type="button" @click="emit('close')">
          x
        </button>
      </header>

      <form class="sensor-form" @submit.prevent="onSubmit">
        <label class="form-field">
          <span>Nombre</span>
          <input
            v-model="form.nombre"
            type="text"
            placeholder="Ej. Sensor humedad zona norte"
          />
        </label>

        <label class="form-field">
          <span>Tipo</span>
          <select v-model="form.tipo">
            <option value="TEMPERATURA">Temperatura</option>
            <option value="HUMEDAD">Humedad</option>
          </select>
        </label>

        <label class="toggle-field">
          <input v-model="form.activo" type="checkbox" />
          <span>Sensor activo</span>
        </label>

        <div class="modal-actions">
          <button class="secondary-button" type="button" @click="emit('close')">
            Cancelar
          </button>
          <button class="primary-button" type="submit">
            Guardar
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 50;
  background: rgba(15, 23, 42, 0.38);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.sensor-modal {
  width: min(100%, 460px);
  background: #ffffff;
  border: 1px solid #dce5d9;
  border-radius: 14px;
  box-shadow: 0 22px 55px rgba(15, 23, 42, 0.18);
  padding: 22px;
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 20px;
}

.modal-eyebrow {
  margin: 0 0 4px;
  color: #0a7a43;
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
}

.modal-header h2 {
  margin: 0;
  color: #17233a;
  font-size: 22px;
  font-weight: 800;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: 1px solid #d6dfd3;
  border-radius: 8px;
  background: #ffffff;
  color: #64748b;
  cursor: pointer;
  font-size: 16px;
  font-weight: 800;
}

.sensor-form {
  display: grid;
  gap: 16px;
}

.form-field {
  display: grid;
  gap: 8px;
}

.form-field span,
.toggle-field span {
  color: #374151;
  font-size: 13px;
  font-weight: 800;
}

.form-field input,
.form-field select {
  width: 100%;
  height: 44px;
  border: 1px solid #d5dfd2;
  border-radius: 10px;
  background: #ffffff;
  color: #1f2937;
  font-size: 14px;
  font-weight: 600;
  padding: 0 12px;
  outline: none;
}

.form-field input:focus,
.form-field select:focus {
  border-color: #0a7a43;
  box-shadow: 0 0 0 3px rgba(10, 122, 67, 0.12);
}

.toggle-field {
  min-height: 42px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.toggle-field input {
  width: 18px;
  height: 18px;
  accent-color: #0a7a43;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.primary-button,
.secondary-button {
  height: 42px;
  border-radius: 10px;
  padding: 0 16px;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}

.primary-button {
  border: 1px solid #0a7a43;
  background: #0a7a43;
  color: #ffffff;
}

.secondary-button {
  border: 1px solid #d6dfd3;
  background: #ffffff;
  color: #4b5563;
}
</style>
