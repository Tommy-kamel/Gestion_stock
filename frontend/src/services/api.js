import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercepteur pour ajouter le token d'authentification
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// API pour les achats
export const achatApi = {
  getDemandesAchat: () => api.get('/achats/demandes'),
  creerDemandeAchat: (data) => api.post('/achats/demandes', data),
  soumettreDemandeAchat: (id) => api.put(`/achats/demandes/${id}/soumettre`),
  approuverDemandeAchat: (id, approbateurId) => api.put(`/achats/demandes/${id}/approuver`, { approbateurId }),
  getDetailsDemande: (id) => api.get(`/achats/demandes/${id}/details`),
  ajouterDetail: (id, data) => api.post(`/achats/demandes/${id}/details`, data),
  modifierDetail: (id, detailId, data) => api.put(`/achats/demandes/${id}/details/${detailId}`, data),
  supprimerDetail: (id, detailId) => api.delete(`/achats/demandes/${id}/details/${detailId}`)
}

// API pour le stock
export const stockApi = {
  getArticles: () => api.get('/stock/articles'),
  getDepots: () => api.get('/stock/depots'),
  getSites: () => api.get('/stock/sites')
}

// API pour les références
export const referenceApi = {
  getEntreprises: () => api.get('/references/entreprises'),
  getSites: () => api.get('/references/sites'),
  getStatuts: () => api.get('/references/statuts')
}

// API pour l'authentification
export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getProfile: () => api.get('/auth/profile')
}