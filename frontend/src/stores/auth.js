import { defineStore } from 'pinia'
import { authApi } from '@/services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    isAuthenticated: false
  }),

  getters: {
    isLoggedIn: (state) => state.isAuthenticated,
    userRole: (state) => state.user?.role,
    userEntreprise: (state) => state.user?.entreprise,
    userName: (state) => state.user ? `${state.user.prenom || ''} ${state.user.nom || ''}`.trim() : '',
    userInitials: (state) => {
      if (!state.user) return 'U'
      const prenom = state.user.prenom || ''
      const nom = state.user.nom || ''
      return (prenom.charAt(0) + nom.charAt(0)).toUpperCase() || 'U'
    }
  },

  actions: {
    async login(credentials) {
      try {
        const response = await authApi.login(credentials)
        const { token, user } = response.data

        this.token = token
        this.user = user
        this.isAuthenticated = true

        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(user))

        return { success: true }
      } catch (error) {
        console.error('Login error:', error)
        return { success: false, error: error.response?.data?.error || 'Erreur de connexion' }
      }
    },

    async logout() {
      this.user = null
      this.token = null
      this.isAuthenticated = false

      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    async checkAuth() {
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')

      if (token && user) {
        this.token = token
        this.user = JSON.parse(user)
        this.isAuthenticated = true
      }
    },

    async getProfile() {
      try {
        const response = await authApi.getProfile()
        this.user = response.data
        localStorage.setItem('user', JSON.stringify(response.data))
      } catch (error) {
        console.error('Error fetching profile:', error)
        this.logout()
      }
    }
  }
})