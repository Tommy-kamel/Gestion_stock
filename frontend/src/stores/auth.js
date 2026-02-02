import { defineStore } from 'pinia'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    
    userName: (state) => {
      if (!state.user) return ''
      return state.user.nom || ''
    },
    
    userInitials: (state) => {
      if (!state.user) return '??'
      const nom = state.user.nom || ''
      return nom[0] || '?'
    },

    // Pour l'instant, considérer l'utilisateur connecté comme super admin
    isSuperAdmin: (state) => {
      return !!state.user
    }
  },

  actions: {
    async login(username, password) {
      this.loading = true
      this.error = null
      
      try {
        const response = await api.post('/auth/login', { username, password })
        
        this.user = response.data.user
        this.token = response.data.token
        
        localStorage.setItem('user', JSON.stringify(this.user))
        localStorage.setItem('token', this.token)
        
        return { success: true }
      } catch (error) {
        this.error = error.response?.data?.error || 'Erreur de connexion'
        return { success: false, error: this.error }
      } finally {
        this.loading = false
      }
    },
    
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    },
    
    checkAuth() {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')
      
      if (user && token) {
        this.user = JSON.parse(user)
        this.token = token
        return true
      }
      return false
    }
  }
})
