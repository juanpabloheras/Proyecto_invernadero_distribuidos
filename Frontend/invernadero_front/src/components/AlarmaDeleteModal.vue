<script setup>
defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  alarma: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'confirm'])
</script>

<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="emit('close')">
    <section class="alarm-modal">
      <header class="modal-header">
        <div>
          <h2>Eliminar alarma</h2>
        </div>

        <button class="modal-close" type="button" @click="emit('close')">
          x
        </button>
      </header>

      <p class="delete-copy">
        Estas a punto de eliminar
        <strong>{{ alarma?.nombreAlarma }}</strong>.
      </p>

      <div class="modal-actions">
        <button class="secondary-button" type="button" @click="emit('close')">
          Cancelar
        </button>
        <button class="danger-button" type="button" @click="emit('confirm')">
          Eliminar
        </button>
      </div>
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

.alarm-modal {
  width: min(100%, 420px);
  box-sizing: border-box;
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
  color: #b91c1c;
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

.delete-copy {
  margin: 0 0 22px;
  color: #4b5563;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.5;
}

.delete-copy strong {
  color: #17233a;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.secondary-button,
.danger-button {
  height: 42px;
  border-radius: 10px;
  padding: 0 16px;
  font-size: 14px;
  font-weight: 800;
  cursor: pointer;
}

.secondary-button {
  border: 1px solid #d6dfd3;
  background: #ffffff;
  color: #4b5563;
}

.danger-button {
  border: 1px solid #b91c1c;
  background: #b91c1c;
  color: #ffffff;
}
</style>
