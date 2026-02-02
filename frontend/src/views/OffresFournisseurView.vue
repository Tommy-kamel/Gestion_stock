<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-6 flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Offres Fournisseurs</h1>
        <p class="text-gray-500">Gérez les offres reçues pour les demandes d'achat</p>
      </div>
      <button @click="showNewOffreModal = true" 
              class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle Offre
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-lg shadow p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Recherche</label>
          <input v-model="filters.search" type="text" placeholder="N° offre, fournisseur..."
                 class="w-full border rounded-lg px-3 py-2">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Demande d'achat</label>
          <select v-model="filters.demandeAchatId" class="w-full border rounded-lg px-3 py-2">
            <option value="">Toutes</option>
            <option v-for="da in demandesAchat" :key="da.id" :value="da.id">
              {{ da.numero }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Statut</label>
          <select v-model="filters.statut" class="w-full border rounded-lg px-3 py-2">
            <option value="">Tous</option>
            <option value="EN_ATTENTE">En attente</option>
            <option value="ACCEPTEE">Acceptée</option>
            <option value="REFUSEE">Refusée</option>
            <option value="EXPIREE">Expirée</option>
          </select>
        </div>
        <div class="flex items-end">
          <button @click="resetFilters" class="text-gray-500 hover:text-gray-700">
            Réinitialiser
          </button>
        </div>
      </div>
    </div>

    <!-- Liste des DA avec leurs offres -->
    <div class="space-y-6">
      <div v-for="da in filteredDemandesAchat" :key="da.id" 
           class="bg-white rounded-lg shadow overflow-hidden">
        <!-- En-tête DA -->
        <div class="bg-gray-50 px-6 py-4 border-b flex justify-between items-center">
          <div>
            <h3 class="font-semibold text-lg">{{ da.numero }}</h3>
            <p class="text-sm text-gray-500">{{ formatDate(da.dateCreation) }} - {{ da.statut }}</p>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm" :class="getOffreCountClass(da)">
              {{ getOffresForDA(da.id).length }}/3 offres
            </span>
            <button v-if="canSelectBestOffre(da)" 
                    @click="selectBestOffre(da.id)"
                    class="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 text-sm">
              Sélectionner la meilleure offre
            </button>
          </div>
        </div>
        
        <!-- Liste des offres pour cette DA -->
        <div class="p-4">
          <table v-if="getOffresForDA(da.id).length > 0" class="min-w-full">
            <thead>
              <tr class="text-left text-gray-500 text-sm">
                <th class="pb-2">N° Offre</th>
                <th class="pb-2">Fournisseur</th>
                <th class="pb-2">Date</th>
                <th class="pb-2">Validité</th>
                <th class="pb-2">Délai</th>
                <th class="pb-2 text-right">Montant Total</th>
                <th class="pb-2">Statut</th>
                <th class="pb-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="offre in getOffresForDA(da.id)" :key="offre.id"
                  :class="getOffreRowClass(offre)">
                <td class="py-3 font-medium">{{ offre.numero }}</td>
                <td class="py-3">{{ offre.fournisseur?.nom }}</td>
                <td class="py-3">{{ formatDate(offre.dateOffre) }}</td>
                <td class="py-3">{{ formatDate(offre.dateValidite) }}</td>
                <td class="py-3">{{ offre.delaiLivraison }} jours</td>
                <td class="py-3 text-right font-semibold">{{ formatMontant(offre.montantTotal) }}</td>
                <td class="py-3">
                  <span :class="getStatutBadgeClass(offre.statut)">
                    {{ formatStatut(offre.statut) }}
                  </span>
                </td>
                <td class="py-3">
                  <div class="flex space-x-2">
                    <button @click="viewOffre(offre)" class="text-blue-600 hover:text-blue-800">
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                              d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                              d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                    </button>
                    <button v-if="offre.statut === 'EN_ATTENTE'" 
                            @click="deleteOffre(offre)"
                            class="text-red-600 hover:text-red-800">
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          <p v-else class="text-gray-500 text-center py-4">
            Aucune offre reçue pour cette demande d'achat
          </p>
        </div>
      </div>
    </div>

    <!-- Modal Nouvelle Offre -->
    <div v-if="showNewOffreModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div class="p-6 border-b">
          <h2 class="text-xl font-bold">Nouvelle Offre Fournisseur</h2>
        </div>
        <form @submit.prevent="createOffre" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Demande d'achat *</label>
              <select v-model="newOffre.demandeAchatId" required
                      class="w-full border rounded-lg px-3 py-2">
                <option value="">Sélectionner...</option>
                <option v-for="da in demandesAchatValidees" :key="da.id" :value="da.id">
                  {{ da.numero }}
                </option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Fournisseur *</label>
              <select v-model="newOffre.fournisseurId" required
                      class="w-full border rounded-lg px-3 py-2">
                <option value="">Sélectionner...</option>
                <option v-for="f in fournisseurs" :key="f.id" :value="f.id">
                  {{ f.nom }}
                </option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date offre *</label>
              <input v-model="newOffre.dateOffre" type="date" required
                     class="w-full border rounded-lg px-3 py-2">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date validité *</label>
              <input v-model="newOffre.dateValidite" type="date" required
                     class="w-full border rounded-lg px-3 py-2">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Délai livraison (jours) *</label>
              <input v-model.number="newOffre.delaiLivraison" type="number" min="1" required
                     class="w-full border rounded-lg px-3 py-2">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Conditions paiement</label>
              <input v-model="newOffre.conditionsPaiement" type="text"
                     class="w-full border rounded-lg px-3 py-2">
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Observations</label>
            <textarea v-model="newOffre.observations" rows="3"
                      class="w-full border rounded-lg px-3 py-2"></textarea>
          </div>
          
          <!-- Lignes de l'offre -->
          <div v-if="newOffre.demandeAchatId" class="border-t pt-4">
            <h3 class="font-semibold mb-2">Lignes de prix</h3>
            <table class="w-full text-sm">
              <thead>
                <tr class="text-left text-gray-500">
                  <th class="pb-2">Article</th>
                  <th class="pb-2">Qté demandée</th>
                  <th class="pb-2">Qté proposée</th>
                  <th class="pb-2">Prix unit.</th>
                  <th class="pb-2">Remise %</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(ligne, idx) in lignesDA" :key="idx">
                  <td class="py-2">{{ ligne.article?.designation }}</td>
                  <td class="py-2">{{ ligne.quantite }}</td>
                  <td class="py-2">
                    <input v-model.number="lignesPrix[idx].quantiteProposee" type="number" 
                           class="w-20 border rounded px-2 py-1">
                  </td>
                  <td class="py-2">
                    <input v-model.number="lignesPrix[idx].prixUnitaire" type="number" step="0.01"
                           class="w-24 border rounded px-2 py-1">
                  </td>
                  <td class="py-2">
                    <input v-model.number="lignesPrix[idx].tauxRemise" type="number" step="0.01"
                           class="w-20 border rounded px-2 py-1">
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div class="flex justify-end space-x-3 pt-4 border-t">
            <button type="button" @click="showNewOffreModal = false"
                    class="px-4 py-2 border rounded-lg hover:bg-gray-50">
              Annuler
            </button>
            <button type="submit" :disabled="loading"
                    class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50">
              {{ loading ? 'Création...' : 'Créer l\'offre' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal Détail Offre -->
    <div v-if="selectedOffre" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <div class="p-6 border-b flex justify-between items-center">
          <h2 class="text-xl font-bold">Offre {{ selectedOffre.numero }}</h2>
          <button @click="selectedOffre = null" class="text-gray-500 hover:text-gray-700">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        <div class="p-6">
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <p class="text-sm text-gray-500">Fournisseur</p>
              <p class="font-medium">{{ selectedOffre.fournisseur?.nom }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Demande d'achat</p>
              <p class="font-medium">{{ selectedOffre.demandeAchat?.numero }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Date offre</p>
              <p class="font-medium">{{ formatDate(selectedOffre.dateOffre) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Validité</p>
              <p class="font-medium">{{ formatDate(selectedOffre.dateValidite) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Délai livraison</p>
              <p class="font-medium">{{ selectedOffre.delaiLivraison }} jours</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Statut</p>
              <span :class="getStatutBadgeClass(selectedOffre.statut)">
                {{ formatStatut(selectedOffre.statut) }}
              </span>
            </div>
          </div>
          
          <h3 class="font-semibold mb-2">Lignes de l'offre</h3>
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-gray-500 border-b">
                <th class="pb-2">Article</th>
                <th class="pb-2 text-right">Quantité</th>
                <th class="pb-2 text-right">Prix unit.</th>
                <th class="pb-2 text-right">Remise</th>
                <th class="pb-2 text-right">Montant</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ligne in selectedOffre.lignes" :key="ligne.id" class="border-b">
                <td class="py-2">{{ ligne.article?.designation }}</td>
                <td class="py-2 text-right">{{ ligne.quantiteProposee }}</td>
                <td class="py-2 text-right">{{ formatMontant(ligne.prixUnitaire) }}</td>
                <td class="py-2 text-right">{{ ligne.tauxRemise }}%</td>
                <td class="py-2 text-right font-medium">{{ formatMontant(ligne.montantLigne) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr class="font-bold">
                <td colspan="4" class="py-2 text-right">Total:</td>
                <td class="py-2 text-right">{{ formatMontant(selectedOffre.montantTotal) }}</td>
              </tr>
            </tfoot>
          </table>
          
          <div v-if="selectedOffre.observations" class="mt-4">
            <p class="text-sm text-gray-500">Observations</p>
            <p class="text-gray-700">{{ selectedOffre.observations }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

// Data
const offres = ref([])
const demandesAchat = ref([])
const fournisseurs = ref([])
const loading = ref(false)
const showNewOffreModal = ref(false)
const selectedOffre = ref(null)
const lignesDA = ref([])
const lignesPrix = ref([])

// Filtres
const filters = ref({
  search: '',
  demandeAchatId: '',
  statut: ''
})

// Nouvelle offre
const newOffre = ref({
  demandeAchatId: '',
  fournisseurId: '',
  dateOffre: new Date().toISOString().split('T')[0],
  dateValidite: '',
  delaiLivraison: 7,
  conditionsPaiement: '',
  observations: ''
})

// Computed
const demandesAchatValidees = computed(() => 
  demandesAchat.value.filter(da => da.statut === 'VALIDEE')
)

const filteredDemandesAchat = computed(() => {
  let result = demandesAchat.value.filter(da => da.statut === 'VALIDEE' || da.statut === 'EN_COMMANDE')
  
  if (filters.value.demandeAchatId) {
    result = result.filter(da => da.id === parseInt(filters.value.demandeAchatId))
  }
  
  return result
})

// Watchers
watch(() => newOffre.value.demandeAchatId, async (daId) => {
  if (daId) {
    await loadLignesDA(daId)
  } else {
    lignesDA.value = []
    lignesPrix.value = []
  }
})

// Methods
function getOffresForDA(daId) {
  let result = offres.value.filter(o => o.demandeAchat?.id === daId)
  
  if (filters.value.statut) {
    result = result.filter(o => o.statut === filters.value.statut)
  }
  
  if (filters.value.search) {
    const search = filters.value.search.toLowerCase()
    result = result.filter(o => 
      o.numero?.toLowerCase().includes(search) ||
      o.fournisseur?.nom?.toLowerCase().includes(search)
    )
  }
  
  return result.sort((a, b) => (a.montantTotal || 0) - (b.montantTotal || 0))
}

function getOffreCountClass(da) {
  const count = getOffresForDA(da.id).length
  if (count >= 3) return 'text-green-600 font-semibold'
  if (count > 0) return 'text-yellow-600'
  return 'text-red-600'
}

function canSelectBestOffre(da) {
  const offresDA = getOffresForDA(da.id)
  return offresDA.length >= 3 && 
         offresDA.every(o => o.statut === 'EN_ATTENTE') &&
         da.statut === 'VALIDEE'
}

function getOffreRowClass(offre) {
  if (offre.statut === 'ACCEPTEE') return 'bg-green-50'
  if (offre.statut === 'REFUSEE') return 'bg-red-50'
  if (offre.statut === 'EXPIREE') return 'bg-gray-50'
  return ''
}

function getStatutBadgeClass(statut) {
  const classes = {
    'EN_ATTENTE': 'px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs',
    'ACCEPTEE': 'px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs',
    'REFUSEE': 'px-2 py-1 bg-red-100 text-red-800 rounded-full text-xs',
    'EXPIREE': 'px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-xs'
  }
  return classes[statut] || classes['EN_ATTENTE']
}

function formatStatut(statut) {
  const labels = {
    'EN_ATTENTE': 'En attente',
    'ACCEPTEE': 'Acceptée',
    'REFUSEE': 'Refusée',
    'EXPIREE': 'Expirée'
  }
  return labels[statut] || statut
}

function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

function formatMontant(montant) {
  if (!montant) return '-'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(montant)
}

function resetFilters() {
  filters.value = { search: '', demandeAchatId: '', statut: '' }
}

async function loadData() {
  try {
    const [offresRes, daRes, fournRes] = await Promise.all([
      axios.get(`${API_URL}/achats/offres`),
      axios.get(`${API_URL}/achats/da`),
      axios.get(`${API_URL}/referentiels/fournisseurs`)
    ])
    offres.value = offresRes.data
    demandesAchat.value = daRes.data
    fournisseurs.value = fournRes.data
  } catch (error) {
    console.error('Erreur chargement données:', error)
  }
}

async function loadLignesDA(daId) {
  try {
    const da = demandesAchat.value.find(d => d.id === parseInt(daId))
    if (da && da.lignes) {
      lignesDA.value = da.lignes
      lignesPrix.value = da.lignes.map(l => ({
        ligneDemandeAchatId: l.id,
        articleId: l.article?.id,
        quantiteProposee: l.quantite,
        prixUnitaire: 0,
        tauxRemise: 0
      }))
    }
  } catch (error) {
    console.error('Erreur chargement lignes DA:', error)
  }
}

async function createOffre() {
  loading.value = true
  try {
    // Créer l'offre
    const response = await axios.post(`${API_URL}/achats/offres`, newOffre.value)
    const offreId = response.data.id
    
    // Ajouter les lignes
    for (const ligne of lignesPrix.value) {
      if (ligne.prixUnitaire > 0) {
        await axios.post(`${API_URL}/achats/offres/${offreId}/lignes`, ligne)
      }
    }
    
    showNewOffreModal.value = false
    resetNewOffre()
    await loadData()
  } catch (error) {
    console.error('Erreur création offre:', error)
    alert(error.response?.data?.error || 'Erreur lors de la création')
  } finally {
    loading.value = false
  }
}

async function selectBestOffre(daId) {
  if (!confirm('Confirmer la sélection automatique de l\'offre la moins chère ?')) return
  
  try {
    await axios.post(`${API_URL}/achats/offres/da/${daId}/select-best`)
    await loadData()
  } catch (error) {
    console.error('Erreur sélection offre:', error)
    alert(error.response?.data?.error || 'Erreur lors de la sélection')
  }
}

async function deleteOffre(offre) {
  if (!confirm(`Supprimer l'offre ${offre.numero} ?`)) return
  
  try {
    await axios.delete(`${API_URL}/achats/offres/${offre.id}`)
    await loadData()
  } catch (error) {
    console.error('Erreur suppression:', error)
    alert(error.response?.data?.error || 'Erreur lors de la suppression')
  }
}

function viewOffre(offre) {
  selectedOffre.value = offre
}

function resetNewOffre() {
  newOffre.value = {
    demandeAchatId: '',
    fournisseurId: '',
    dateOffre: new Date().toISOString().split('T')[0],
    dateValidite: '',
    delaiLivraison: 7,
    conditionsPaiement: '',
    observations: ''
  }
  lignesDA.value = []
  lignesPrix.value = []
}

onMounted(loadData)
</script>
