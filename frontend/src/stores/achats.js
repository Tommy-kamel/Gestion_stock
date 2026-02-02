import { defineStore } from 'pinia'
import axios from 'axios'

export const useAchatsStore = defineStore('achats', {
  state: () => ({
    // Demandes d'achat
    demandesAchat: [],
    demandeAchatSelectionnee: null,
    
    // Bons de commande
    bonsCommande: [],
    bonCommandeSelectionne: null,
    
    // Réceptions
    receptions: [],
    receptionSelectionnee: null,
    
    // Factures fournisseur
    facturesFournisseur: [],
    factureFournisseurSelectionnee: null,
    
    // État
    loading: false,
    error: null
  }),

  getters: {
    demandesEnAttente: (state) => 
      state.demandesAchat.filter(da => da.statut === 'BROUILLON' || da.statut === 'EN_ATTENTE_VALIDATION'),
    
    demandesValidees: (state) => 
      state.demandesAchat.filter(da => da.statut === 'VALIDEE'),
    
    bonsCommandeEnCours: (state) => 
      state.bonsCommande.filter(bc => bc.statut === 'EN_COURS' || bc.statut === 'PARTIELLEMENT_RECU'),
    
    facturesNonPayees: (state) => 
      state.facturesFournisseur.filter(f => f.statut !== 'PAYEE')
  },

  actions: {
    // =============== DEMANDES D'ACHAT ===============
    async fetchDemandesAchat() {
      this.loading = true
      try {
        const response = await axios.get('/api/achats/da')
        this.demandesAchat = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement des demandes'
      } finally {
        this.loading = false
      }
    },

    async createDemandeAchat(demande, demandeurId) {
      this.loading = true
      try {
        console.log('[DEBUG] createDemandeAchat - payload:', demande, 'params:', { demandeurId })
        const response = await axios.post('/api/achats/da', demande, {
          params: { demandeurId }
        })
        this.demandesAchat.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateDemandeAchat(id, demande) {
      this.loading = true
      try {
        const response = await axios.put(`/api/achats/da/${id}`, demande)
        const index = this.demandesAchat.findIndex(da => da.id === id)
        if (index !== -1) {
          this.demandesAchat[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la mise à jour'
        throw error
      } finally {
        this.loading = false
      }
    },

    async soumettreDemandeAchat(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/da/${id}/soumettre`)
        const index = this.demandesAchat.findIndex(da => da.id === id)
        if (index !== -1) {
          this.demandesAchat[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la soumission'
        throw error
      } finally {
        this.loading = false
      }
    },

    async validerDemandeAchat(id, approbateurId, niveau) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/da/${id}/approuver`, null, {
          params: { approbateurId, niveau }
        })
        const index = this.demandesAchat.findIndex(da => da.id === id)
        if (index !== -1) {
          this.demandesAchat[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la validation'
        throw error
      } finally {
        this.loading = false
      }
    },

    async rejeterDemandeAchat(id, rejeteurId, motif) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/da/${id}/rejeter`, { motif }, {
          params: { rejeteurId }
        })
        const index = this.demandesAchat.findIndex(da => da.id === id)
        if (index !== -1) {
          this.demandesAchat[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du rejet'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== BONS DE COMMANDE ===============
    async fetchBonsCommande() {
      this.loading = true
      try {
        const response = await axios.get('/api/achats/bc')
        this.bonsCommande = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async createBonCommande(bc, acheteurId) {
      this.loading = true
      try {
        const response = await axios.post('/api/achats/bc', bc, {
          params: { acheteurId }
        })
        this.bonsCommande.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async creerBCDepuisDA(daId, fournisseurId, acheteurId) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/da/${daId}/creer-bc`, null, {
          params: { fournisseurId, acheteurId }
        })
        this.bonsCommande.push(response.data)
        // Rafraîchir la DA
        await this.fetchDemandesAchat()
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création du BC'
        throw error
      } finally {
        this.loading = false
      }
    },

    async envoyerBonCommande(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/bc/${id}/envoyer`)
        const index = this.bonsCommande.findIndex(bc => bc.id === id)
        if (index !== -1) {
          this.bonsCommande[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de l\'envoi'
        throw error
      } finally {
        this.loading = false
      }
    },

    async confirmerBonCommande(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/bc/${id}/confirmer`)
        const index = this.bonsCommande.findIndex(bc => bc.id === id)
        if (index !== -1) {
          this.bonsCommande[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la confirmation'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== RÉCEPTIONS ===============
    async fetchReceptions() {
      this.loading = true
      try {
        const response = await axios.get('/api/achats/receptions')
        this.receptions = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    async creerReception(reception) {
      this.loading = true
      try {
        const response = await axios.post('/api/achats/receptions', reception)
        this.receptions.push(response.data)
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la création'
        throw error
      } finally {
        this.loading = false
      }
    },

    async validerReception(id) {
      this.loading = true
      try {
        const response = await axios.post(`/api/achats/receptions/${id}/valider`)
        const index = this.receptions.findIndex(r => r.id === id)
        if (index !== -1) {
          this.receptions[index] = response.data
        }
        return response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors de la validation'
        throw error
      } finally {
        this.loading = false
      }
    },

    // =============== FACTURES FOURNISSEUR ===============
    async fetchFacturesFournisseur() {
      this.loading = true
      try {
        const response = await axios.get('/api/achats/factures-fournisseur')
        this.facturesFournisseur = response.data
      } catch (error) {
        this.error = error.response?.data?.message || 'Erreur lors du chargement'
      } finally {
        this.loading = false
      }
    },

    clearError() {
      this.error = null
    }
  }
})
