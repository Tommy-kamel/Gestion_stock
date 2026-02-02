<template>
  <div class="space-y-6">
    <!-- En-tête -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Demandes d'achat</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des demandes d'achat internes</p>
      </div>
      <button @click="showCreateModal = true" class="mt-4 sm:mt-0 btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle demande
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Rechercher</label>
          <input v-model="filters.search" type="text" placeholder="N° DA, article..." 
                 class="w-full border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Statut</label>
          <select v-model="filters.statut" class="w-full border-gray-300 rounded-lg">
            <option value="">Tous</option>
            <option value="BROUILLON">Brouillon</option>
            <option value="SOUMIS">Soumis</option>
            <option value="APPROUVE">Approuvé</option>
            <option value="EN_ATTENTE_PROFORMA">En attente proforma</option>
            <option value="EN_ATTENTE_FINANCE">En attente finance</option>
            <option value="VALIDE_FINANCE">Validé finance</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Date début</label>
          <input v-model="filters.dateDebut" type="date" class="w-full border-gray-300 rounded-lg">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Date fin</label>
          <input v-model="filters.dateFin" type="date" class="w-full border-gray-300 rounded-lg">
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Demandeur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Dépôt cible</th>
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
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ da.demandeur?.nom }} {{ da.demandeur?.prenom }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ da.depotCible?.nom }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(da.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ da.statut?.libelle }}
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
                  <button v-if="da.statut?.code === 'BROUILLON'" @click="soumettre(da)" class="text-green-600 hover:text-green-900" title="Soumettre">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </button>
                  <button v-if="da.statut?.code === 'SOUMIS'" @click="approuver(da)" class="text-blue-600 hover:text-blue-900" title="Approuver">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredDemandes.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                Aucune demande d'achat trouvée
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
        <div class="relative bg-white rounded-xl shadow-xl max-w-2xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Nouvelle demande d'achat</h3>
          
          <form @submit.prevent="creerDemande" class="space-y-4">
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Entreprise</label>
                <select v-model="newDemande.entrepriseId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="entreprise in entreprises" :key="entreprise.id" :value="entreprise.id">{{ entreprise.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Site</label>
                <select v-model="newDemande.siteId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="site in sites" :key="site.id" :value="site.id">{{ site.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Dépôt cible</label>
                <select v-model="newDemande.depotCibleId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="depot in depots" :key="depot.id" :value="depot.id">{{ depot.nom }}</option>
                </select>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date demande</label>
              <input v-model="newDemande.dateDemande" type="date" required class="w-full border-gray-300 rounded-lg">
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Observations</label>
              <textarea v-model="newDemande.observations" rows="2" class="w-full border-gray-300 rounded-lg"></textarea>
            </div>

            <!-- Articles -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">Articles</label>
                <button type="button" @click="addArticleLine" class="text-sm text-indigo-600 hover:text-indigo-700">
                  + Ajouter ligne
                </button>
              </div>
              <div class="space-y-2">
                <div v-for="(line, index) in newDemande.details" :key="index" class="flex items-center space-x-2">
                  <select v-model="line.articleId" required class="flex-1 border-gray-300 rounded-lg text-sm">
                    <option value="">Article</option>
                    <option v-for="art in articles" :key="art.id" :value="art.id">{{ art.reference }} - {{ art.designation }}</option>
                  </select>
                  <input v-model.number="line.quantite" type="number" min="1" required placeholder="Qté" 
                         class="w-20 border-gray-300 rounded-lg text-sm">
                  <input v-model.number="line.prixUnitaire" type="number" min="0" step="0.01" required placeholder="Prix" 
                         class="w-24 border-gray-300 rounded-lg text-sm">
                  <button type="button" @click="removeArticleLine(index)" class="text-red-500 hover:text-red-700">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Créer</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">
            Demande {{ selectedDemande?.numeroDa }}
          </h3>
          
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <p class="text-sm text-gray-500">Date demande</p>
              <p class="font-medium">{{ formatDate(selectedDemande?.dateDemande) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Statut</p>
              <span :class="getStatutClass(selectedDemande?.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                {{ selectedDemande?.statut?.libelle }}
              </span>
            </div>
            <div>
              <p class="text-sm text-gray-500">Demandeur</p>
              <p class="font-medium">{{ selectedDemande?.demandeur?.nom }} {{ selectedDemande?.demandeur?.prenom }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Dépôt cible</p>
              <p class="font-medium">{{ selectedDemande?.depotCible?.nom }}</p>
            </div>
          </div>

          <div class="mb-4">
            <h4 class="font-medium text-gray-900 mb-2">Articles demandés</h4>
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                  <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Quantité</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-for="detail in selectedDemande?.details" :key="detail.id">
                  <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
                  <td class="px-4 py-2 text-sm">{{ detail.quantite }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="flex justify-end">
            <button @click="showDetailsModal = false" class="btn-secondary">Fermer</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { achatApi, stockApi, referenceApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const demandes = ref([])
const depots = ref([])
const articles = ref([])
const entreprises = ref([])
const sites = ref([])

const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const selectedDemande = ref(null)

const filters = reactive({
  search: '',
  statut: '',
  dateDebut: '',
  dateFin: ''
})

const newDemande = reactive({
  entrepriseId: '',
  siteId: '',
  depotCibleId: '',
  dateDemande: new Date().toISOString().split('T')[0],
  observations: '',
  details: [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
})

const filteredDemandes = computed(() => {
  return demandes.value.filter(da => {
    if (filters.search) {
      const search = filters.search.toLowerCase()
      if (!da.numeroDa?.toLowerCase().includes(search)) return false
    }
    if (filters.statut && da.statut?.code !== filters.statut) return false
    return true
  })
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('fr-FR')
}

const getStatutClass = (code) => {
  const classes = {
    'BROUILLON': 'bg-gray-100 text-gray-800',
    'SOUMIS': 'bg-blue-100 text-blue-800',
    'APPROUVE': 'bg-green-100 text-green-800',
    'EN_ATTENTE_PROFORMA': 'bg-yellow-100 text-yellow-800',
    'EN_ATTENTE_FINANCE': 'bg-purple-100 text-purple-800',
    'VALIDE_FINANCE': 'bg-green-100 text-green-800',
    'REJETE_FINANCE': 'bg-red-100 text-red-800'
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

const viewDetails = (da) => {
  selectedDemande.value = da
  showDetailsModal.value = true
}

const creerDemande = async () => {
  try {
    const data = {
      dateDemande: newDemande.dateDemande,
      entrepriseId: newDemande.entrepriseId,
      siteId: newDemande.siteId,
      depotId: newDemande.depotCibleId,
      demandeurId: authStore.user?.id || 1,
      motifAchat: newDemande.observations,
      details: newDemande.details.filter(d => d.articleId).map(d => ({
        articleId: d.articleId,
        quantiteDemandee: d.quantite,
        prixUnitaire: d.prixUnitaire
      }))
    }
    await achatApi.creerDemandeAchat(data)
    showCreateModal.value = false
    loadDemandes()
    // Reset form
    newDemande.entrepriseId = ''
    newDemande.siteId = ''
    newDemande.depotCibleId = ''
    newDemande.observations = ''
    newDemande.details = [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
  } catch (error) {
    console.error('Erreur création DA:', error)
    alert('Erreur lors de la création')
  }
}

const soumettre = async (da) => {
  if (confirm('Soumettre cette demande pour approbation ?')) {
    try {
      await achatApi.soumettreDemandeAchat(da.id)
      loadDemandes()
    } catch (error) {
      console.error('Erreur soumission:', error)
    }
  }
}

const approuver = async (da) => {
  if (confirm('Approuver cette demande ?')) {
    try {
      await achatApi.approuverDemandeAchat(da.id, authStore.user?.id || 1)
      loadDemandes()
    } catch (error) {
      console.error('Erreur approbation:', error)
    }
  }
}

const loadDemandes = async () => {
  try {
    const response = await achatApi.getDemandesAchat()
    demandes.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement DA:', error)
    // Données de démo
    demandes.value = [
      { id: 1, numeroDa: 'DA-2024-001', dateDemande: '2024-01-15', demandeur: { nom: 'RAKOTO', prenom: 'Admin' }, depotCible: { nom: 'Dépôt Central' }, statut: { code: 'BROUILLON', libelle: 'Brouillon' } },
      { id: 2, numeroDa: 'DA-2024-002', dateDemande: '2024-01-16', demandeur: { nom: 'RAKOTO', prenom: 'Admin' }, depotCible: { nom: 'Dépôt Central' }, statut: { code: 'SOUMIS', libelle: 'Soumis' } }
    ]
  }
}

const loadDepots = async () => {
  try {
    const response = await stockApi.getDepots()
    depots.value = response.data || []
  } catch (error) {
    depots.value = [
      { id: 1, nom: 'Dépôt Central Tana' },
      { id: 2, nom: 'Dépôt Secondaire' }
    ]
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

const loadEntreprises = async () => {
  try {
    const response = await referenceApi.getEntreprises()
    entreprises.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement entreprises:', error)
    entreprises.value = [
      { id: 1, nom: 'Madagascar Distribution SARL' }
    ]
  }
}

const loadSites = async () => {
  try {
    const response = await referenceApi.getSites()
    sites.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement sites:', error)
    sites.value = [
      { id: 1, nom: 'Site Principal Tana' }
    ]
  }
}

onMounted(() => {
  loadDemandes()
  loadDepots()
  loadArticles()
  loadEntreprises()
  loadSites()
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
