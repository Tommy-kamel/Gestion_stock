<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Création de proforma</h1>
        <p class="mt-1 text-sm text-gray-500">Demandes d'achat client validées</p>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° DA Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date souhaitée</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="demande in demandes" :key="demande.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ demande.numeroDa }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(demande.dateDemande) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ demande.client?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(demande.dateSouhaitee) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(demande.status?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ demande.status?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(demande)" class="text-indigo-600 hover:text-indigo-900" title="Voir détails">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  </button>
                  <button @click="creerProforma(demande)" class="text-green-600 hover:text-green-900" title="Créer proforma">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="demandes.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">Aucune demande validée</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Créer proforma pour {{ selectedDemande?.numeroDa }}</h3>
          
          <div class="mb-4 bg-blue-50 p-4 rounded-lg">
            <div class="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span class="font-medium text-gray-700">Client:</span>
                <span class="ml-2 text-gray-900">{{ selectedDemande?.client?.nom }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-700">Date demande:</span>
                <span class="ml-2 text-gray-900">{{ formatDate(selectedDemande?.dateDemande) }}</span>
              </div>
            </div>
          </div>

          <form @submit.prevent="submitProforma" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date proforma</label>
              <input v-model="newProforma.dateProforma" type="date" required class="w-full px-3 py-2 border border-gray-300 rounded-lg">
            </div>

            <div>
              <div class="flex justify-between mb-2">
                <label class="text-sm font-medium text-gray-700">Articles</label>
              </div>
              <div class="border border-gray-200 rounded-lg overflow-hidden">
                <table class="min-w-full">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté demandée</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unitaire</th>
                      <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total</th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(line, idx) in newProforma.details" :key="idx">
                      <td class="px-4 py-2 text-sm">
                        {{ getArticleLabel(line.articleId) }}
                      </td>
                      <td class="px-4 py-2">
                        <input v-model.number="line.quantite" type="number" min="0.01" step="0.01" required 
                               class="w-24 px-2 py-1 border border-gray-300 rounded text-sm">
                      </td>
                      <td class="px-4 py-2">
                        <input v-model.number="line.prixUnitaire" type="number" min="0" step="0.01" required 
                               class="w-32 px-2 py-1 border border-gray-300 rounded text-sm">
                      </td>
                      <td class="px-4 py-2 text-sm font-medium">
                        {{ formatCurrency(line.quantite * line.prixUnitaire) }}
                      </td>
                    </tr>
                    <tr class="bg-gray-50 font-semibold">
                      <td colspan="3" class="px-4 py-2 text-right text-sm">Total général:</td>
                      <td class="px-4 py-2 text-sm">{{ formatCurrency(totalProforma) }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="flex justify-end space-x-3 pt-4 border-t">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Créer proforma</button>
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
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Détails - {{ selectedDemande?.numeroDa }}</h3>
          
          <div class="grid grid-cols-3 gap-4 mb-6">
            <div class="bg-gray-50 p-3 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Client</p>
              <p class="font-semibold text-gray-900">{{ selectedDemande?.client?.nom }}</p>
            </div>
            <div class="bg-gray-50 p-3 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Date demande</p>
              <p class="font-semibold text-gray-900">{{ formatDate(selectedDemande?.dateDemande) }}</p>
            </div>
            <div class="bg-gray-50 p-3 rounded-lg">
              <p class="text-xs text-gray-500 mb-1">Date souhaitée</p>
              <p class="font-semibold text-gray-900">{{ formatDate(selectedDemande?.dateSouhaitee) }}</p>
            </div>
          </div>

          <div>
            <h4 class="font-semibold text-gray-900 mb-3">Articles demandés</h4>
            <div class="border border-gray-200 rounded-lg overflow-hidden">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Référence</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Désignation</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Quantité</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unitaire</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="detail in selectedDemande?.details" :key="detail.id">
                    <td class="px-4 py-2 text-sm font-medium">{{ detail.article?.reference }}</td>
                    <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
                    <td class="px-4 py-2 text-sm">{{ detail.quantiteDemandee }}</td>
                    <td class="px-4 py-2 text-sm">{{ formatCurrency(detail.prixUnitaire) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="flex justify-end space-x-3 pt-4 mt-4 border-t">
            <button @click="showDetailsModal = false" class="btn-secondary">Fermer</button>
            <button @click="creerProforma(selectedDemande); showDetailsModal = false" class="btn-primary">Créer proforma</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { venteApi, stockApi } from '@/services/api'

const demandes = ref([])
const articles = ref([])
const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const selectedDemande = ref(null)

const newProforma = reactive({
  dateProforma: new Date().toISOString().split('T')[0],
  details: []
})

const totalProforma = computed(() => {
  return newProforma.details.reduce((sum, line) => {
    return sum + (line.quantite * line.prixUnitaire)
  }, 0)
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : 'N/A'
const formatCurrency = (value) => {
  if (!value && value !== 0) return '0 MGA'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(value)
}
const getStatutClass = (code) => ({ 
  'BROUILLON': 'bg-gray-100 text-gray-800', 
  'VALIDE': 'bg-green-100 text-green-800',
  'SOUMIS': 'bg-blue-100 text-blue-800'
}[code] || 'bg-gray-100 text-gray-800')

const getArticleLabel = (articleId) => {
  const article = articles.value.find(a => a.id === articleId)
  return article ? `${article.reference} - ${article.designation}` : ''
}

const viewDetails = async (demande) => {
  try {
    const response = await venteApi.getDemandeClient(demande.id)
    selectedDemande.value = response.data
    showDetailsModal.value = true
  } catch (e) {
    console.error('Erreur chargement détails:', e)
    selectedDemande.value = demande
    showDetailsModal.value = true
  }
}

const creerProforma = async (demande) => {
  try {
    // Charger les détails complets si nécessaire
    const response = await venteApi.getDemandeClient(demande.id)
    selectedDemande.value = response.data
    
    // Pré-remplir le formulaire avec les articles de la demande
    newProforma.details = selectedDemande.value.details.map(d => ({
      articleId: d.article.id,
      quantite: d.quantiteDemandee,
      prixUnitaire: d.prixUnitaire || d.article.prixVenteRef || 0
    }))
    
    showCreateModal.value = true
  } catch (e) {
    console.error('Erreur préparation proforma:', e)
    alert('Erreur lors de la préparation de la proforma')
  }
}

const submitProforma = async () => {
  try {
    if (!selectedDemande.value) {
      alert('Aucune demande sélectionnée')
      return
    }

    const data = {
      demandeAchatClientId: selectedDemande.value.id,
      dateProforma: newProforma.dateProforma,
      details: newProforma.details.map(d => ({
        articleId: d.articleId,
        quantite: d.quantite,
        prixUnitaire: d.prixUnitaire
      }))
    }
    
    await venteApi.creerProformaVente(data)
    showCreateModal.value = false
    alert('Proforma créée avec succès!')
    loadDemandes()
    
    // Reset
    newProforma.dateProforma = new Date().toISOString().split('T')[0]
    newProforma.details = []
    selectedDemande.value = null
  } catch (e) {
    console.error('Erreur création proforma:', e)
    alert('Erreur lors de la création: ' + (e.response?.data || e.message))
  }
}

const loadDemandes = async () => {
  try {
    const response = await venteApi.getDemandesClientValidees()
    demandes.value = response.data || []
  } catch (e) {
    console.error('Erreur chargement demandes:', e)
    demandes.value = []
  }
}

const loadArticles = async () => {
  try {
    const response = await stockApi.getArticles()
    articles.value = response.data || []
  } catch (e) { 
    console.error('Erreur chargement articles:', e)
    articles.value = []
  }
}

onMounted(() => { 
  loadDemandes()
  loadArticles()
})
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
