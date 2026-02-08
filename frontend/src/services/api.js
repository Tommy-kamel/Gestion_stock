import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter le token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api

// ===================== ACHATS =====================
export const achatApi = {
  // Demandes d'achat
  getDemandesAchat: () => api.get('/achats/demandes'),
  getDemandeAchat: (id) => api.get(`/achats/demandes/${id}`),
  creerDemandeAchat: (data) => api.post('/achats/demandes', data),
  validerDemandeAchat: (id) => api.put(`/achats/demandes/${id}/valider`),
  rejeterDemandeAchat: (id, motif) => api.put(`/achats/demandes/${id}/rejeter?motif=${encodeURIComponent(motif)}`),
  soumettreDemandeAchat: (id) => api.put(`/achats/demandes/${id}/soumettre`),
  approuverDemandeAchat: (id, personnelId) => api.post(`/achats/demandes/${id}/approuver?personnelId=${personnelId}`),
  
  // Proformas
  getProformas: (demandeId) => api.get(`/proformas/demande/${demandeId}`),
  getAllProformas: () => api.get('/proformas'),
  creerProforma: (data) => api.post('/proformas', data),
  selectionnerMeilleurProforma: (demandeId) => api.post(`/proformas/demande/${demandeId}/selectionner-meilleur`),
  
  // Validation finance
  validerFinance: (demandeId, personnelId) => api.post(`/achats/demandes/${demandeId}/valider-finance?personnelId=${personnelId}`),
  rejeterFinance: (demandeId, personnelId, motif) => api.post(`/achats/demandes/${demandeId}/rejeter-finance?personnelId=${personnelId}&motif=${motif}`),
  
  // Bons de commande
  getBonsCommande: () => api.get('/bons-commande-achat'),
  getBonCommande: (id) => api.get(`/bons-commande-achat/${id}`),
  creerBonCommande: (data) => api.post('/bons-commande-achat', data),
  livrerEtPayerBonCommande: (id, data) => api.post(`/bons-commande-achat/${id}/livrer-et-payer`, data),
  
  // Bons de livraison
  getBonsLivraison: () => api.get('/achats/bons-livraison'),
  creerBonLivraison: (bcId, data) => api.post(`/achats/bons-commande/${bcId}/bon-livraison`, data),
  validerReception: (blId, personnelId) => api.post(`/achats/bons-livraison/${blId}/valider?personnelId=${personnelId}`),
  
  // Factures
  getFactures: () => api.get('/achats/factures'),
  creerFacture: (blId, numeroFacture, dateFacture) => 
    api.post(`/achats/bons-livraison/${blId}/facture?numeroFacture=${numeroFacture}&dateFacture=${dateFacture}`)
}

// ===================== VENTES =====================
export const venteApi = {
  // Demandes de proforma
  getDemandesProforma: () => api.get('/ventes/demandes-proforma'),
  creerDemandeProforma: (data) => api.post('/ventes/demandes-proforma', data),
  
  // Devis
  getDevis: () => api.get('/ventes/devis'),
  creerDevis: (data) => api.post('/ventes/devis', data),
  validerDevis: (id) => api.post(`/ventes/devis/${id}/valider`),
  
  // Bons de commande
  getBonsCommande: () => api.get('/ventes/bons-commande'),
  creerBonCommande: (devisId, personnelId) => api.post(`/ventes/devis/${devisId}/bon-commande?personnelId=${personnelId}`),
  
  // Bons de livraison
  getBonsLivraison: () => api.get('/ventes/bons-livraison'),
  creerBonLivraison: (bcId, adresseLivraison) => 
    api.post(`/ventes/bons-commande/${bcId}/bon-livraison?adresseLivraison=${encodeURIComponent(adresseLivraison)}`),
  expedierLivraison: (blId, personnelId) => api.post(`/ventes/bons-livraison/${blId}/expedier?personnelId=${personnelId}`),
  confirmerReception: (blId) => api.post(`/ventes/bons-livraison/${blId}/confirmer-reception`),
  
  // Factures
  getFactures: () => api.get('/ventes/factures'),
  creerFacture: (blId, echeanceJours) => api.post(`/ventes/bons-livraison/${blId}/facture?echeanceJours=${echeanceJours}`)
}

// ===================== STOCK =====================
export const stockApi = {
  // Articles
  getArticles: () => api.get('/stock/articles'),
  getArticle: (id) => api.get(`/stock/articles/${id}`),
  creerArticle: (data) => api.post('/stock/articles', data),
  modifierArticle: (id, data) => api.put(`/stock/articles/${id}`, data),
  
  // Dépôts
  getDepots: () => api.get('/stock/depots'),
  getDepot: (id) => api.get(`/stock/depots/${id}`),
  
  // État du stock
  getAllStocks: () => api.get('/stock/tous'),
  getStockValorise: () => api.get('/stock/valorisation'),
  getStocksByDepot: (depotId) => api.get(`/stock/niveau?depotId=${depotId}`),
  getStocksByArticle: (articleId) => api.get(`/stock/niveau/article/${articleId}`),
  getStock: (articleId, depotId) => api.get(`/stock/niveau/article/${articleId}/depot/${depotId}`),
  getQuantiteDisponible: (articleId, depotId) => api.get(`/stock/disponible?articleId=${articleId}&depotId=${depotId}`),
  
  // Lots
  getLots: (articleId, depotId) => {
    let url = '/stock/lots'
    const params = []
    if (articleId) params.push(`articleId=${articleId}`)
    if (depotId) params.push(`depotId=${depotId}`)
    if (params.length > 0) url += '?' + params.join('&')
    return api.get(url)
  },
  
  // Mouvements
  getMouvements: (articleId, depotId) => {
    let url = '/mouvements'
    const params = []
    if (articleId) params.push(`articleId=${articleId}`)
    if (depotId) params.push(`depotId=${depotId}`)
    if (params.length > 0) url += '?' + params.join('&')
    return api.get(url)
  },
  createMouvement: (data) => api.post('/mouvements', data),
  
  // Entrées/Sorties
  entreeStock: (articleId, depotId, quantite, prixUnitaire, personnelId, reference, numeroLot) => {
    const params = new URLSearchParams()
    params.append('articleId', articleId)
    params.append('depotId', depotId)
    params.append('quantite', quantite)
    params.append('prixUnitaire', prixUnitaire)
    params.append('personnelId', personnelId)
    if (reference) params.append('reference', reference)
    if (numeroLot) params.append('numeroLot', numeroLot)
    return api.post(`/stock/entree?${params.toString()}`)
  },
  
  sortieStock: (articleId, depotId, quantite, personnelId, reference) => {
    const params = new URLSearchParams()
    params.append('articleId', articleId)
    params.append('depotId', depotId)
    params.append('quantite', quantite)
    params.append('personnelId', personnelId)
    if (reference) params.append('reference', reference)
    return api.post(`/stock/sortie?${params.toString()}`)
  },
  
  // Valorisation
  getCMUP: (articleId, depotId) => api.get(`/stock/cmup?articleId=${articleId}&depotId=${depotId}`),
  getPrixFIFO: (articleId, depotId) => api.get(`/stock/fifo?articleId=${articleId}&depotId=${depotId}`),
  getPrixLIFO: (articleId, depotId) => api.get(`/stock/lifo?articleId=${articleId}&depotId=${depotId}`),
  
  // Statistiques
  getStatistiques: () => api.get('/stock/statistiques')
}

// ===================== FINANCES =====================
export const financeApi = {
  // Validation demandes
  getDemandesEnAttente: () => api.get('/finances/demandes-achat/en-attente'),
  validerDemande: (demandeId, personnelId) => api.post(`/finances/demandes-achat/${demandeId}/valider?personnelId=${personnelId}`),
  rejeterDemande: (demandeId, personnelId, motif) => 
    api.post(`/finances/demandes-achat/${demandeId}/rejeter?personnelId=${personnelId}&motif=${encodeURIComponent(motif)}`),
  
  // Caisses
  getCaisses: () => api.get('/finances/caisses'),
  getMouvementsCaisse: (caisseId) => api.get(`/finances/caisses/${caisseId}/mouvements`),
  enregistrerMouvement: (caisseId, data) => api.post(`/finances/caisses/${caisseId}/mouvements`, data),
  
  // Paiements
  getPaiementsAchat: () => api.get('/finances/paiements/achats'),
  enregistrerPaiementAchat: (data) => api.post('/finances/paiements/achats', data),
  
  getPaiementsVente: () => api.get('/finances/paiements/ventes'),
  enregistrerPaiementVente: (data) => api.post('/finances/paiements/ventes', data),
  
  // Totaux
  getTotalSoldes: () => api.get('/finances/total-soldes'),
  getTotalCreances: () => api.get('/finances/total-creances'),
  getTotalDettes: () => api.get('/finances/total-dettes')
}

// ===================== RÉFÉRENTIEL =====================
export const referenceApi = {
  // Statuts
  getStatuts: () => api.get('/references/statuts'),
  
  // Unités
  getUnites: () => api.get('/references/unites'),
  
  // Catégories
  getCategories: () => api.get('/references/categories'),
  
  // Méthodes valorisation
  getMethodesValorisation: () => api.get('/references/methodes-valorisation'),
  
  // Modes paiement
  getModesPaiement: () => api.get('/references/modes-paiement'),
  
  // Entreprises
  getEntreprises: () => api.get('/references/entreprises'),
  getEntreprisesInternes: () => api.get('/references/entreprises/internes'),
  getFournisseurs: () => api.get('/references/fournisseurs'),
  getClients: () => api.get('/references/entreprises/clients'),
  
  // Personnel
  getPersonnel: () => api.get('/references/personnel'),
  
  // Sites
  getSites: () => api.get('/references/sites')
}

// ===================== DASHBOARD =====================
export const dashboardApi = {
  getDashboard: () => api.get('/dashboard')
}
