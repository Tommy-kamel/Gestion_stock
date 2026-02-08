<template>
  <div class="space-y-6">
    <!-- En-tête -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Demandes d'achat client</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des demandes d'achat des clients</p>
      </div>
      <button @click="showCreateModal = true" class="mt-4 sm:mt-0 btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle demande client
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Client</label>
          <select v-model="filters.client" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            <option value="">Tous</option>
            <option v-for="client in clients" :key="client.id" :value="client.id">{{ client.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Statut</label>
          <select v-model="filters.statut" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            <option value="">Tous</option>
            <option value="BROUILLON">Brouillon</option>
            <option value="SOUMIS">Soumis</option>
            <option value="VALIDE">Validé</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Date début</label>
          <input v-model="filters.dateDebut" type="date" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Date fin</label>
          <input v-model="filters.dateFin" type="date" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
        </div>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">N° DA</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date souhaitée</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="da in filteredDemandes" :key="da.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="font-medium text-gray-900">{{ da.numeroDa }}</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(da.dateDemande) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ da.client?.nom || 'N/A' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(da.dateSouhaitee) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(da.status?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ da.status?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(da)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  </button>
                  <!-- <button v-if="da.status?.code === 'BROUILLON'" @click="soumettre(da)" 
                          class="text-blue-600 hover:text-blue-900" title="Soumettre">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                    </svg>
                  </button> -->
                  <button v-if="da.status?.code !== 'VALIDE' && da.status?.code !== 'CLOTURE'" @click="valider(da)" 
                          class="text-green-600 hover:text-green-900" title="Valider">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredDemandes.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                Aucune demande d'achat client trouvée
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-6">Nouvelle demande d'achat client</h3>
          
          <form @submit.prevent="creerDemande" class="space-y-6">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Client *</label>
                <select v-model="newDemande.clientId" required class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                  <option value="">Sélectionner un client</option>
                  <option v-for="client in clients" :key="client.id" :value="client.id">{{ client.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Date demande *</label>
                <input v-model="newDemande.dateDemande" type="date" required class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Date souhaitée</label>
              <input v-model="newDemande.dateSouhaitee" type="date" class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            </div>

            <!-- Articles -->
            <div>
              <div class="flex items-center justify-between mb-3">
                <label class="block text-sm font-medium text-gray-700">Articles demandés *</label>
                <button type="button" @click="addArticleLine" class="px-3 py-2 text-sm text-white bg-indigo-600 rounded-lg hover:bg-indigo-700">
                  + Ajouter une ligne
                </button>
              </div>
              <div class="overflow-x-auto border border-gray-200 rounded-lg">
                <table class="min-w-full">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Article</th>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider w-32">Quantité</th>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider w-40">Prix unitaire</th>
                      <th class="px-4 py-3 w-16"></th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(line, index) in newDemande.details" :key="index">
                      <td class="px-4 py-3">
                        <select v-model="line.articleId" required class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                          <option value="">Sélectionner un article</option>
                          <option v-for="art in articles" :key="art.id" :value="art.id">{{ art.reference }} - {{ art.designation }}</option>
                        </select>
                      </td>
                      <td class="px-4 py-3">
                        <input v-model.number="line.quantite" type="number" min="1" step="0.01" required placeholder="Qté" 
                               class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                      </td>
                      <td class="px-4 py-3">
                        <input v-model.number="line.prixUnitaire" type="number" min="0" step="0.01" required placeholder="Prix unitaire" 
                               class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                      </td>
                      <td class="px-4 py-3 text-center">
                        <button type="button" @click="removeArticleLine(index)" class="text-red-500 hover:text-red-700">
                          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="flex justify-end space-x-3 pt-6 border-t">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Créer la demande</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-6">
            Demande {{ selectedDemande?.numeroDa }}
          </h3>
          
          <div class="grid grid-cols-3 gap-6 mb-8">
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">Date demande</p>
              <p class="font-semibold text-gray-900">{{ formatDate(selectedDemande?.dateDemande) }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">Client</p>
              <p class="font-semibold text-gray-900">{{ selectedDemande?.client?.nom || 'N/A' }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">Statut</p>
              <span :class="getStatutClass(selectedDemande?.status?.code)" class="inline-block px-3 py-1 text-sm font-semibold rounded-full">
                {{ selectedDemande?.status?.libelle }}
              </span>
            </div>
          </div>

          <div v-if="selectedDemande?.dateSouhaitee" class="mb-8">
            <div class="bg-blue-50 p-4 rounded-lg">
              <p class="text-sm text-blue-700 mb-1 font-medium">Date de livraison souhaitée</p>
              <p class="font-semibold text-gray-900">{{ formatDate(selectedDemande.dateSouhaitee) }}</p>
            </div>
          </div>

          <div class="mb-6">
            <h4 class="font-semibold text-gray-900 mb-4 text-lg">Articles demandés</h4>
            <div class="overflow-x-auto border border-gray-200 rounded-lg">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Référence</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Désignation</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Quantité</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Prix unitaire</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Total</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="detail in selectedDemande?.details" :key="detail.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ detail.article?.reference || 'N/A' }}</td>
                    <td class="px-6 py-4 text-sm text-gray-700">{{ detail.article?.designation || 'N/A' }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900 font-semibold">{{ detail.quantiteDemandee }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900">{{ formatCurrency(detail.prixUnitaire) }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900 font-semibold">{{ formatCurrency(detail.quantiteDemandee * detail.prixUnitaire) }}</td>
                  </tr>
                  <tr v-if="!selectedDemande?.details || selectedDemande.details.length === 0">
                    <td colspan="5" class="px-6 py-8 text-center text-gray-500">
                      Aucun article
                    </td>
                  </tr>
                  <tr v-if="selectedDemande?.details && selectedDemande.details.length > 0" class="bg-gray-50 font-semibold">
                    <td colspan="4" class="px-6 py-4 text-right text-sm text-gray-900">Total général:</td>
                    <td class="px-6 py-4 text-sm text-gray-900">{{ formatCurrency(totalGeneral) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="flex justify-end space-x-3 pt-6 border-t">
            <button @click="showDetailsModal = false" class="btn-secondary">Fermer</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { venteApi, stockApi, referenceApi } from '@/services/api'

const demandes = ref([])
const clients = ref([])
const articles = ref([])

const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const selectedDemande = ref(null)

const filters = reactive({
  client: '',
  statut: '',
  dateDebut: '',
  dateFin: ''
})

const newDemande = reactive({
  clientId: '',
  dateDemande: new Date().toISOString().split('T')[0],
  dateSouhaitee: '',
  details: [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
})

const filteredDemandes = computed(() => {
  return demandes.value.filter(da => {
    if (filters.client && da.client?.id !== filters.client) return false
    if (filters.statut && da.status?.code !== filters.statut) return false
    if (filters.dateDebut && da.dateDemande < filters.dateDebut) return false
    if (filters.dateFin && da.dateDemande > filters.dateFin) return false
    return true
  })
})

const totalGeneral = computed(() => {
  if (!selectedDemande.value?.details) return 0
  return selectedDemande.value.details.reduce((sum, detail) => {
    return sum + (detail.quantiteDemandee * detail.prixUnitaire)
  }, 0)
})

const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('fr-FR')
}

const formatCurrency = (value) => {
  if (!value && value !== 0) return '0 MGA'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(value)
}

const getStatutClass = (code) => {
  const classes = {
    'BROUILLON': 'bg-gray-100 text-gray-800',
    'SOUMIS': 'bg-blue-100 text-blue-800',
    'VALIDE': 'bg-green-100 text-green-800',
    'CLOTURE': 'bg-gray-400 text-white'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const addArticleLine = () => {
  newDemande.details.push({ articleId: '', quantite: 1, prixUnitaire: 0 })
}

const removeArticleLine = (index) => {
  if (newDemande.details.length > 1) {
    newDemande.details.splice(index, 1)
  }
}

const valider = async (da) => {
  if (confirm('Valider cette demande d\'achat client ?')) {
    try {
      await venteApi.validerDemandeClient(da.id)
      loadDemandes()
      alert('Demande validée avec succès')
    } catch (error) {
      console.error('Erreur validation:', error)
      alert('Erreur lors de la validation')
    }
  }
}

const soumettre = async (da) => {
  if (confirm('Soumettre cette demande pour validation ?')) {
    try {
      await venteApi.soumettreDemandeClient(da.id)
      loadDemandes()
      alert('Demande soumise avec succès')
    } catch (error) {
      console.error('Erreur soumission:', error)
      alert('Erreur lors de la soumission')
    }
  }
}

const viewDetails = async (da) => {
  try {
    const response = await venteApi.getDemandeClient(da.id)
    selectedDemande.value = response.data
    showDetailsModal.value = true
  } catch (error) {
    console.error('Erreur chargement détails:', error)
    selectedDemande.value = da
    showDetailsModal.value = true
  }
}

const creerDemande = async () => {
  try {
    const data = {
      dateDemande: newDemande.dateDemande,
      clientId: newDemande.clientId,
      dateSouhaitee: newDemande.dateSouhaitee || null,
      details: newDemande.details.filter(d => d.articleId).map(d => ({
        articleId: d.articleId,
        quantiteDemandee: d.quantite,
        prixUnitaire: d.prixUnitaire
      }))
    }
    await venteApi.creerDemandeClient(data)
    showCreateModal.value = false
    loadDemandes()
    alert('Demande créée avec succès')
    // Reset form
    newDemande.clientId = ''
    newDemande.dateDemande = new Date().toISOString().split('T')[0]
    newDemande.dateSouhaitee = ''
    newDemande.details = [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
  } catch (error) {
    console.error('Erreur création DA client:', error)
    alert('Erreur lors de la création: ' + (error.response?.data || error.message))
  }
}

const loadDemandes = async () => {
  try {
    const response = await venteApi.getDemandesClient()
    demandes.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement demandes client:', error)
    demandes.value = []
  }
}

const loadClients = async () => {
  try {
    const response = await referenceApi.getClients()
    clients.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement clients:', error)
    clients.value = []
  }
}

const loadArticles = async () => {
  try {
    const response = await stockApi.getArticles()
    articles.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement articles:', error)
    articles.value = []
  }
}

onMounted(() => {
  loadDemandes()
  loadClients()
  loadArticles()
})
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors;
}
.btn-secondary {
  @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors;
}
</style>
