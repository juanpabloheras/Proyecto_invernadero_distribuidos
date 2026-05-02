import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import LogInView from '../views/LogInView.vue'
import SensoresView from '../views/SensoresView.vue'
import AlarmasView from '../views/AlarmasView.vue'
import ReportesView from '../views/ReportesView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: LogInView },
  { path: '/sensores', component: SensoresView },
  { path: '/alarmas', component: AlarmasView },
  { path: '/reportes', component: ReportesView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router