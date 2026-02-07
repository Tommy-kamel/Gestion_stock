import { defineStore } from 'pinia'
import axios from 'axios'

export const useReferentielStore = defineStore('referentiel', {
  state: () => ({
    // Articles
    articles: [],
    categories: [],
    
    // Tiers
    fournisseurs: [],
    clients: [],
    
    // Dépôts
    depots: [],
    
    // Utilisateurs
    utilisateurs: [],
    
    // État
    loading: false,
    error: null
  }),

  getters: {
    articlesActifs: (state) => state.articles.filter(a => a.actif),
    
    fournisseursActifs: (state) => state.fournisseurs.filter(f => f.actif),
    
    clientsActifs: (state) => state.clients.filter(c => c.actif),
    
    articlesByCategorie: (state) => (categorieId) => 
      state.articles.filter(a => a.categorie?.id === categorieId),
    
    getArticleById: (state) => (id) => 
      state.articles.find(a => a.id === id),
    
    getFournisseurById: (state) => (id) => 
      state.fournisseurs.find(f => f.id === id),
    
    getClientById: (state) => (id) => 
      state.clients.find(c => c.id === id)
  },

  actions: {
    // =============== ARTICLES ===============
    async fetchArticles() {
      this.loading = true
      try {
        const response = await axios.get('/api/referentiels/articles')
        this.articles = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createArticle(article) {
      this.loading = true
      try {
        const response = await axios.post('/api/referentiels/articles', article)
        this.articles.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateArticle(id, article) {
      this.loading = true
      try {
        const response = await axios.put(`/api/referentiels/articles/${id}`, article)
        const index = this.articles.findIndex(a => a.id === id)
        if (index !== -1) {
          this.articles[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== CATÉGORIES ===============
    async fetchCategories() {
      this.loading = true
      try {
        const response = await axios.get('/api/referentiels/categories')
        this.categories = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createCategorie(categorie) {
      this.loading = true
      try {
        const response = await axios.post('/api/referentiels/categories', categorie)
        this.categories.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== FOURNISSEURS ===============
    async fetchFournisseurs() {
      this.loading = true
      try {
        const response = await axios.get('/api/referentiels/fournisseurs')
        this.fournisseurs = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createFournisseur(fournisseur) {
      this.loading = true
      try {
        const response = await axios.post('/api/referentiels/fournisseurs', fournisseur)
        this.fournisseurs.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateFournisseur(id, fournisseur) {
      this.loading = true
      try {
        const response = await axios.put(`/api/referentiels/fournisseurs/${id}`, fournisseur)
        const index = this.fournisseurs.findIndex(f => f.id === id)
        if (index !== -1) {
          this.fournisseurs[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== CLIENTS ===============
    async fetchClients() {
      this.loading = true
      try {
        const response = await axios.get('/api/referentiels/clients')
        this.clients = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createClient(client) {
      this.loading = true
      try {
        const response = await axios.post('/api/referentiels/clients', client)
        this.clients.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateClient(id, client) {
      this.loading = true
      try {
        const response = await axios.put(`/api/referentiels/clients/${id}`, client)
        const index = this.clients.findIndex(c => c.id === id)
        if (index !== -1) {
          this.clients[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== DÉPÔTS ===============
    async fetchDepots() {
      this.loading = true
      try {
        const response = await axios.get('/api/referentiels/depots')
        this.depots = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createDepot(depot) {
      this.loading = true
      try {
        const response = await axios.post('/api/referentiels/depots', depot)
        this.depots.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== UTILISATEURS ===============
    async fetchUtilisateurs() {
      this.loading = true
      try {
        const response = await axios.get('/api/referentiels/utilisateurs')
        this.utilisateurs = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    // =============== CHARGEMENT INITIAL ===============
    async loadAll() {
      await Promise.all([
        this.fetchArticles(),
        this.fetchCategories(),
        this.fetchFournisseurs(),
        this.fetchClients(),
        this.fetchDepots()
      ])
    },

    clearError() {
      this.error = null
    }
  }
})
