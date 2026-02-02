import { defineStore } from 'pinia'
import axios from 'axios'

export const useVentesStore = defineStore('ventes', {
  state: () => ({
    // Commandes / Devis
    commandesVente: [],
    commandeSelectionnee: null,
    
    // Livraisons
    livraisons: [],
    livraisonSelectionnee: null,
    
    // Factures client
    facturesClient: [],
    factureClientSelectionnee: null,
    
    // Encaissements
    encaissements: [],
    encaissementSelectionne: null,
    
    // État
    loading: false,
    error: null
  }),

  getters: {
    devis: (state) => 
      state.commandesVente.filter(c => c.statut === 'DEVIS' || c.statut === 'DEVIS_ENVOYE'),
    
    commandesConfirmees: (state) => 
      state.commandesVente.filter(c => 
        c.statut === 'CONFIRMEE' || 
        c.statut === 'EN_PREPARATION' || 
        c.statut === 'PRETE_LIVRAISON'
      ),
    
    commandesALivrer: (state) => 
      state.commandesVente.filter(c => c.statut === 'PRETE_LIVRAISON'),
    
    facturesEnAttente: (state) => 
      state.facturesClient.filter(f => f.statut === 'EMISE' || f.statut === 'PARTIELLEMENT_PAYEE'),
    
    totalEncaissementsMois: (state) => {
      const debutMois = new Date()
      debutMois.setDate(1)
      debutMois.setHours(0, 0, 0, 0)
      
      return state.encaissements
        .filter(e => new Date(e.dateEncaissement) >= debutMois)
        .reduce((sum, e) => sum + e.montant, 0)
    }
  },

  actions: {
    // =============== COMMANDES / DEVIS ===============
    async fetchCommandesVente() {
      this.loading = true
      try {
        const response = await axios.get('/api/ventes/commandes')
        this.commandesVente = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createDevis(devis, commercialId) {
      this.loading = true
      try {
        const response = await axios.post('/api/ventes/devis', devis, {
          params: { commercialId }
        })
        this.commandesVente.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async envoyerDevis(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/commandes/${id}/envoyer-devis`)
        const index = this.commandesVente.findIndex(c => c.id === id)
        if (index !== -1) {
          this.commandesVente[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de l\'envoi'
        throw error
      } finally {
        this.loading = false
      }
    },

    async confirmerCommande(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/commandes/${id}/confirmer`)
        const index = this.commandesVente.findIndex(c => c.id === id)
        if (index !== -1) {
          this.commandesVente[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la confirmation'
        throw error
      } finally {
        this.loading = false
      }
    },

    async preparerCommande(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/commandes/${id}/preparer`)
        const index = this.commandesVente.findIndex(c => c.id === id)
        if (index !== -1) {
          this.commandesVente[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la préparation'
        throw error
      } finally {
        this.loading = false
      }
    },

    async marquerPreteLivraison(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/commandes/${id}/prete-livraison`)
        const index = this.commandesVente.findIndex(c => c.id === id)
        if (index !== -1) {
          this.commandesVente[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      } finally {
        this.loading = false
      }
    },

    async annulerCommande(id, motif) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/commandes/${id}/annuler`, null, {
          params: { motif }
        })
        const index = this.commandesVente.findIndex(c => c.id === id)
        if (index !== -1) {
          this.commandesVente[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de l\'annulation'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== LIVRAISONS ===============
    async fetchLivraisons() {
      this.loading = true
      try {
        const response = await axios.get('/api/ventes/livraisons')
        this.livraisons = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async creerLivraison(livraison) {
      this.loading = true
      try {
        const response = await axios.post('/api/ventes/livraisons', livraison)
        this.livraisons.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async expedierLivraison(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/livraisons/${id}/expedier`)
        const index = this.livraisons.findIndex(l => l.id === id)
        if (index !== -1) {
          this.livraisons[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      } finally {
        this.loading = false
      }
    },

    async confirmerReceptionClient(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/livraisons/${id}/livree`)
        const index = this.livraisons.findIndex(l => l.id === id)
        if (index !== -1) {
          this.livraisons[index] = response.data
        }
        // Rafraîchir les commandes aussi
        await this.fetchCommandesVente()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== FACTURES CLIENT ===============
    async fetchFacturesClient() {
      this.loading = true
      try {
        const response = await axios.get('/api/ventes/factures-client')
        this.facturesClient = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async creerFacture(livraisonId) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/livraisons/${livraisonId}/facturer`)
        this.facturesClient.push(response.data)
        // Rafraîchir les livraisons
        await this.fetchLivraisons()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async validerFacture(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/ventes/factures-client/${id}/valider`)
        const index = this.facturesClient.findIndex(f => f.id === id)
        if (index !== -1) {
          this.facturesClient[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la validation'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== ENCAISSEMENTS ===============
    async fetchEncaissements() {
      this.loading = true
      try {
        const response = await axios.get('/api/ventes/encaissements')
        this.encaissements = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async creerEncaissement(encaissement) {
      this.loading = true
      try {
        const response = await axios.post('/api/ventes/encaissements', encaissement)
        this.encaissements.push(response.data)
        // Rafraîchir les factures
        await this.fetchFacturesClient()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    clearError() {
      this.error = null
    }
  }
})
