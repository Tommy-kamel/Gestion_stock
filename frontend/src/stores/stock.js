import { defineStore } from 'pinia'
import axios from 'axios'

export const useStockStore = defineStore('stock', {
  state: () => ({
    // État des stocks
    stocks: [],
    stockParArticle: {},
    
    // Mouvements
    mouvements: [],
    
    // Inventaires
    inventaires: [],
    inventaireSelectionne: null,
    
    // Lots
    lots: [],
    
    // Dépôts et emplacements
    depots: [],
    emplacements: [],
    
    // État
    loading: false,
    error: null
  }),

  getters: {
    stocksEnAlerte: (state) => 
      state.stocks.filter(s => s.quantiteDisponible <= s.article?.seuilAlerte),
    
    stocksEnRupture: (state) => 
      state.stocks.filter(s => s.quantiteDisponible === 0),
    
    lotsProchesExpiration: (state) => {
      const dans30Jours = new Date()
      dans30Jours.setDate(dans30Jours.getDate() + 30)
      
      return state.lots.filter(l => 
        l.dateExpiration && 
        new Date(l.dateExpiration) <= dans30Jours &&
        l.quantiteRestante > 0
      )
    },
    
    valeurTotaleStock: (state) => 
      state.stocks.reduce((total, s) => total + (s.valeurStock || 0), 0),
    
    depotPrincipal: (state) => 
      state.depots.find(d => d.principal) || state.depots[0]
  },

  actions: {
    // =============== ÉTAT DES STOCKS ===============
    async fetchStocks(depotId = null) {
      this.loading = true
      try {
        const url = depotId 
          ? `/api/stock/etat?depotId=${depotId}` 
          : '/api/stock/etat'
        const response = await axios.get(url)
        this.stocks = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async fetchStockArticle(articleId, depotId = null) {
      this.loading = true
      try {
        const params = depotId ? { depotId } : {}
        const response = await axios.get(`/api/stock/articles/${articleId}`, { params })
        this.stockParArticle[articleId] = response.data
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      } finally {
        this.loading = false
      }
    },

    async verifierDisponibilite(articleId, quantite, depotId = null) {
      try {
        const params = { quantite }
        if (depotId) params.depotId = depotId
        
        const response = await axios.get(`/api/stock/articles/${articleId}/disponibilite`, { params })
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        return false
      }
    },

    // =============== MOUVEMENTS ===============
    async fetchMouvements(params = {}) {
      this.loading = true
      try {
        const response = await axios.get('/api/stock/mouvements', { params })
        this.mouvements = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async creerMouvementManuel(mouvement) {
      this.loading = true
      try {
        const response = await axios.post('/api/stock/mouvements', mouvement)
        this.mouvements.unshift(response.data)
        // Rafraîchir l'état des stocks
        await this.fetchStocks()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async transfererStock(transfert) {
      this.loading = true
      try {
        const response = await axios.post('/api/stock/transferts', transfert)
        // Rafraîchir l'état des stocks
        await this.fetchStocks()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du transfert'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== LOTS ===============
    async fetchLots(articleId = null, depotId = null) {
      this.loading = true
      try {
        const params = {}
        if (articleId) params.articleId = articleId
        if (depotId) params.depotId = depotId
        
        const response = await axios.get('/api/stock/lots', { params })
        this.lots = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    // =============== INVENTAIRES ===============
    async fetchInventaires() {
      this.loading = true
      try {
        const response = await axios.get('/api/stock/inventaires')
        this.inventaires = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async creerInventaire(inventaire) {
      this.loading = true
      try {
        const response = await axios.post('/api/stock/inventaires', inventaire)
        this.inventaires.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async enregistrerLigneInventaire(inventaireId, ligne) {
      this.loading = true
      try {
        const response = await axios.post(`/api/stock/inventaires/${inventaireId}/lignes`, ligne)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      } finally {
        this.loading = false
      }
    },

    async validerInventaire(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/stock/inventaires/${id}/valider`)
        const index = this.inventaires.findIndex(i => i.id === id)
        if (index !== -1) {
          this.inventaires[index] = response.data
        }
        // Rafraîchir l'état des stocks
        await this.fetchStocks()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la validation'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== DÉPÔTS & EMPLACEMENTS ===============
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

    async fetchEmplacements(depotId) {
      this.loading = true
      try {
        const response = await axios.get(`/api/referentiels/emplacements/depot/${depotId}`)
        this.emplacements = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    // =============== VALORISATION ===============
    async calculerValorisationFIFO(articleId) {
      try {
        const response = await axios.get(`/api/stock/valorisation/fifo/${articleId}`)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      }
    },

    async calculerValorisationCUMP(articleId) {
      try {
        const response = await axios.get(`/api/stock/valorisation/cump/${articleId}`)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      }
    },

    clearError() {
      this.error = null
    }
  }
})
