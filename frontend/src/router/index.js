import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import DemandesAchatView from '@/views/achats/DemandesAchatView.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/',
    name: 'DemandesAchat',
    component: DemandesAchatView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard pour vérifier l'authentification
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token')

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
