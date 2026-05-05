import { createRouter, createWebHistory } from 'vue-router'

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

router.beforeEach((to) => {
  const isLoggedIn = Boolean(localStorage.getItem('currentUser'))

  if (!to.meta.public && !isLoggedIn) {
    return '/login'
  }

  if (to.path === '/login' && isLoggedIn) {
    return '/home'
  }

  return true
})

export default router
