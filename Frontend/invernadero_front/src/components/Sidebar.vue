<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Home, RadioTower, Bell, BarChart3, X, LogOut } from 'lucide-vue-next'

const notificaciones = ref([])
const sseConectado = ref(false)
const router = useRouter()
let eventSource = null


const menuItems = [
  {
    label: 'Inicio',
    path: '/home',
    icon: Home
  },
  {
    label: 'Sensores',
    path: '/sensores',
    icon: RadioTower
  },
  {
    label: 'Alarmas',
    path: '/alarmas',
    icon: Bell
  },
  {
    label: 'Reportes',
    path: '/reportes',
    icon: BarChart3
  }
]

const currentUser = ref(null)

const userName = computed(() => currentUser.value?.nombre || 'Usuario')
const userEmail = computed(() => currentUser.value?.correo || 'Sin correo')
const userInitials = computed(() => {
  const name = userName.value.trim()

  if (!name) return 'US'

  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map(part => part[0].toUpperCase())
    .join('')
})

onMounted(() => {
  
  const storedUser = localStorage.getItem('currentUser')

  if (storedUser) {
    currentUser.value = JSON.parse(storedUser)
  }
  eventSource = new EventSource('http://localhost:8082/notificaciones/stream')

  eventSource.onopen = () => {
    sseConectado.value = true
  }

  eventSource.onmessage = (event) => {
    const notificacion = JSON.parse(event.data)

    notificaciones.value.unshift({
      id: crypto.randomUUID(),
      ...notificacion,
      fecha: new Date()
    })
  }

  eventSource.onerror = () => {
    sseConectado.value = false
  }
})

const eliminarNotificacion = (id) => {
  notificaciones.value = notificaciones.value.filter(n => n.id !== id)
}
const limpiarTodas = () => {
  notificaciones.value = []
}

const logout = () => {
  localStorage.removeItem('currentUser')

  if (eventSource) {
    eventSource.close()
  }

  router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <h1>GreenMonitor</h1>
    </div>

    <nav class="sidebar-nav">
      <RouterLink v-for="item in menuItems" :key="item.path" :to="item.path" class="nav-item" active-class="active">
        <component :is="item.icon" class="nav-icon" :size="18" />
        <span>{{ item.label }}</span>
      </RouterLink>
    </nav>

    <section class="sidebar-notificaciones">
      <div class="notificaciones-header">
        <span>Notificaciones</span>

        <button @click="limpiarTodas" class="btn-clear">
          Limpiar
        </button>
        <small>{{ sseConectado ? 'En vivo' : 'Sin conexion' }}</small>
      </div>
      <article v-for="notificacion in notificaciones" :key="notificacion.id" class="notification-card">
        <strong>{{ notificacion.tipoEvento || 'Alarma' }}</strong>
        <button @click="eliminarNotificacion(notificacion.id)" class="btn-delete">
          <X :size="16" />
        </button>
        <p>{{ notificacion.mensaje }}</p>
      </article>
      <p v-if="notificaciones.length === 0" class="notifications-vacia">
        Sin notificaciones
      </p>
    </section>

    <div class="sidebar-user">
      <div class="avatar">
        {{ userInitials }}
      </div>

      <div class="user-info">
        <strong>{{ userName }}</strong>
        <span>{{ userEmail }}</span>
      </div>

      <button class="logout-button" type="button" @click="logout">
        <LogOut :size="17" />
      </button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  box-sizing: border-box;
  width: 260px;
  min-width: 260px;
  height: 100dvh;
  position: fixed;
  top: 0;
  left: 0;
  flex-shrink: 0;
  background: #f8fbff;
  border-right: 1px solid #e6edf5;
  display: flex;
  flex-direction: column;
  padding: 32px 0 0;
  overflow-y: hidden;
}

.sidebar-header {
  padding: 0 28px 36px;
}

.sidebar-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #1f2937;
  letter-spacing: -0.4px;
}

.sidebar-header p {
  margin: 4px 0 0;
  font-size: 13px;
  font-weight: 500;
  color: #94a3b8;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-item {
  position: relative;
  height: 48px;
  padding: 0 28px;
  display: flex;
  align-items: center;
  gap: 14px;
  text-decoration: none;
  color: #718096;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: #f1f8f4;
  color: #43c774;
}

.nav-icon {
  color: currentColor;
  stroke-width: 2.3;
}

.nav-item.active {
  background: #ffffff;
  color: #35c96f;
}

.nav-item.active::after {
  content: "";
  position: absolute;
  right: 0;
  width: 3px;
  height: 100%;
  background: #35c96f;
  border-radius: 3px 0 0 3px;
}

.sidebar-notificaciones {
  padding: 22px 28px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: #f8fbff ;
  margin: 10px;
  border-radius: 5px;
  border: 0.5px solid #e6edf5;

  max-height: 320px;
  overflow-y: auto;
}

.notificaciones-header {
  display: flex;
  flex-direction: column;
  gap: 5px;
  justify-content: space-between;
  align-items: center;
}

.notificaciones-header span {
  font-size: 23px;
  font-weight: 700;
  padding: 10px;
}

.notificaciones-header small {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.notification-card {
  background-color: #ffffff;
  border: 1px solid #e6edf5;
  border-radius: 8px;
  padding: 12px;
}

.notification-card strong {
  display: block;
  font-size: 14px;
  color: #1f2937;
}


.notification-card:hover {
  background-color: #f1f8f4;
  cursor: pointer;
}

.notification-card p {
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.4;
  color: #718096;
}

.notifications-vacia {
  margin: 0;
  padding: 0 10px;
  font-size: 12px;
  color: #94a3b8;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-delete {
  background: transparent;
  border: none;
  cursor: pointer;
  color: #94a3b8;
}

.btn-delete:hover {
  color: #ef4444;
}

.btn-clear {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 12px;
  color: #43c774;
  font-weight: 600;
}

.btn-clear:hover {
  text-decoration: underline;
}

.sidebar-user {
  margin-top: auto;
  padding: 22px 28px;
  border-top: 1px solid #e6edf5;
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #48c878;
  color: #ffffff;
  display: grid;
  place-items: center;
  font-size: 13px;
  font-weight: 800;
}

.user-info {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-info strong {
  font-size: 14px;
  color: #1f2937;
}

.user-info span {
  margin-top: 3px;
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-button {
  width: 34px;
  height: 34px;
  border: 1px solid #e6edf5;
  border-radius: 9px;
  background: #ffffff;
  color: #718096;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: 0.2s ease;
  flex-shrink: 0;
}

.logout-button:hover {
  color: #b91c1c;
  border-color: #f0c8c8;
  background: #fffafa;
}

@media (max-width: 760px) {
  .sidebar {
    width: 100%;
    min-width: 0;
    height: auto;
    position: static;
    padding: 18px 0 0;
    border-right: none;
    border-bottom: 1px solid #e6edf5;
  }

  .sidebar-header {
    padding: 0 20px 16px;
  }

  .sidebar-nav {
    flex-direction: row;
    gap: 0;
    overflow-x: auto;
    padding: 0 12px 12px;
  }

  .nav-item {
    height: 42px;
    flex: 0 0 auto;
    padding: 0 12px;
    border-radius: 8px;
    gap: 8px;
    white-space: nowrap;
  }

  .nav-item.active::after {
    left: 12px;
    right: 12px;
    bottom: 0;
    width: auto;
    height: 3px;
    border-radius: 3px 3px 0 0;
  }

  .sidebar-user {
    display: none;
  }
}
</style>
