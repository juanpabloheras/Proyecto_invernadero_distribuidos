import { createRouter, createWebHistory } from 'vue-router'
import { getCurrentUser } from '../services/auth-service.js'

import HomeView from '../views/HomeView.vue'
import LogInView from '../views/LogInView.vue'
import SensoresView from '../views/SensoresView.vue'
import AlarmasView from '../views/AlarmasView.vue'
import ReportesView from '../views/ReportesView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LogInView, meta: { public: true } },
  { path: '/home', component: HomeView },
  { path: '/sensores', component: SensoresView },
  { path: '/alarmas', component: AlarmasView },
  { path: '/reportes', component: ReportesView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const user = await getCurrentUser()

  if (!to.meta.public && !user) {
    return '/login'
  }

  if (to.path === '/login' && user) {
    return '/home'
  }

  return true
})

export default router
