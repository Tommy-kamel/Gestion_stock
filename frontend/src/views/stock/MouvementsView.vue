<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Mouvements de stock</h1>
        <p class="mt-1 text-sm text-gray-500">Historique des entrées et sorties</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau mouvement
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <div class="grid grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Type</label>
          <select v-model="filterType" class="w-full px-4 py-3 border border-gray-200 rounded-lg">
            <option value="">Tous les types</option>
            <option value="ENTREE">Entrées</option>
            <option value="SORTIE">Sorties</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Dépôt</label>
          <select v-model="filterDepot" class="w-full px-4 py-3 border border-gray-200 rounded-lg">
            <option value="">Tous les dépôts</option>
            <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Date début</label>
          <input v-model="filterDateDebut" type="date" class="w-full px-4 py-3 border border-gray-200 rounded-lg">
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Date fin</label>
          <input v-model="filterDateFin" type="date" class="w-full px-4 py-3 border border-gray-200 rounded-lg">
        </div>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Article</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dépôt</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantité</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="mvt in filteredMouvements" :key="mvt.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ mvt.article?.designation }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDateTime(mvt.dateMouvement) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getTypeClass(mvt.typeMouvement)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ mvt.typeMouvement }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ mvt.depot?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium" :class="mvt.typeMouvement === 'SORTIE' ? 'text-red-600' : 'text-green-600'">
                {{ mvt.typeMouvement === 'SORTIE' ? '-' : '+' }}{{ mvt.quantiteEntree || mvt.quantiteSortie }}
              </td>
            </tr>
            <tr v-if="filteredMouvements.length === 0">
              <td colspan="5" class="px-6 py-12 text-center text-gray-500">Aucun mouvement</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="closeModal"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-6">Nouveau mouvement</h3>
          
          <form @submit.prevent="submitMouvement" class="space-y-6">
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Type *</label>
                <select v-model="formData.typeMouvement" required class="w-full px-4 py-3 border border-gray-200 rounded-lg">
                  <option value="">Sélectionner le type</option>
                  <option value="ENTREE">Entrée</option>
                  <option value="SORTIE">Sortie</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Dépôt *</label>
                <select v-model="formData.depotId" required class="w-full px-4 py-3 border border-gray-200 rounded-lg">
                  <option value="">Sélectionner un dépôt</option>
                  <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Date</label>
                <input v-model="formData.dateMouvement" type="date" class="w-full px-4 py-3 border border-gray-200 rounded-lg">
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Motif / Référence</label>
              <input v-model="formData.motif" type="text" placeholder="Motif du mouvement..." class="w-full px-4 py-3 border border-gray-200 rounded-lg">
            </div>

            <!-- Articles -->
            <div>
              <div class="flex items-center justify-between mb-3">
                <label class="block text-sm font-medium text-gray-700">Articles *</label>
                <button type="button" @click="addArticleLine" class="px-3 py-2 text-sm text-white bg-indigo-600 rounded-lg hover:bg-indigo-700">
                  + Ajouter un article
                </button>
              </div>
              <div class="overflow-x-auto border border-gray-200 rounded-lg">
                <table class="min-w-full">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase">Article</th>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase w-32">Quantité</th>
                      <th v-if="formData.typeMouvement === 'ENTREE'" class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase w-40">Prix unitaire</th>
                      <th class="px-4 py-3 w-16"></th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(line, index) in formData.articles" :key="index">
                      <td class="px-4 py-3">
                        <select v-model="line.articleId" required class="w-full px-3 py-2 border border-gray-200 rounded-lg">
                          <option value="">Sélectionner</option>
                          <option v-for="art in articles" :key="art.id" :value="art.id">{{ art.reference }} - {{ art.designation }}</option>
                        </select>
                      </td>
                      <td class="px-4 py-3">
                        <input v-model.number="line.quantite" type="number" min="0.01" step="0.01" required class="w-full px-3 py-2 border border-gray-200 rounded-lg">
                      </td>
                      <td v-if="formData.typeMouvement === 'ENTREE'" class="px-4 py-3">
                        <input v-model.number="line.prixUnitaire" type="number" min="0" step="0.01" required class="w-full px-3 py-2 border border-gray-200 rounded-lg">
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

            <div class="flex justify-end space-x-3 pt-4 border-t">
              <button type="button" @click="closeModal" class="px-6 py-3 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200">
                Annuler
              </button>
              <button type="submit" class="px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700">
                Enregistrer
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { stockApi } from '@/services/api'

const mouvementsList = ref([])
const articles = ref([])
const depots = ref([])
const filterType = ref('')
const filterDepot = ref('')
const filterDateDebut = ref('')
const filterDateFin = ref('')
const showCreateModal = ref(false)
const formData = reactive({ 
  typeMouvement: '', 
  depotId: '', 
  dateMouvement: new Date().toISOString().split('T')[0],
  motif: '', 
  articles: [{ articleId: '', quantite: 1, prixUnitaire: 0 }] 
})

const filteredMouvements = computed(() => {
  let list = mouvementsList.value
  if (filterType.value) list = list.filter(m => m.typeMouvement === filterType.value)
  if (filterDepot.value) list = list.filter(m => m.depot?.id === parseInt(filterDepot.value))
  if (filterDateDebut.value) list = list.filter(m => new Date(m.dateMouvement) >= new Date(filterDateDebut.value))
  if (filterDateFin.value) list = list.filter(m => new Date(m.dateMouvement) <= new Date(filterDateFin.value))
  return list.sort((a, b) => new Date(b.dateMouvement) - new Date(a.dateMouvement))
})

const formatDateTime = (d) => d ? new Date(d).toLocaleString('fr-FR') : ''
const getTypeClass = (type) => ({ 'ENTREE': 'bg-green-100 text-green-800', 'SORTIE': 'bg-red-100 text-red-800' }[type] || 'bg-gray-100 text-gray-800')

const addArticleLine = () => {
  formData.articles.push({ articleId: '', quantite: 1, prixUnitaire: 0 })
}

const removeArticleLine = (index) => {
  formData.articles.splice(index, 1)
}

const resetForm = () => { 
  formData.typeMouvement = ''
  formData.depotId = ''
  formData.dateMouvement = new Date().toISOString().split('T')[0]
  formData.motif = ''
  formData.articles = [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
}

const closeModal = () => {
  showCreateModal.value = false
  resetForm()
}

const submitMouvement = async () => {
  try {
    const data = {
      typeMouvement: formData.typeMouvement,
      depotId: formData.depotId,
      dateMouvement: formData.dateMouvement ? formData.dateMouvement + 'T' + new Date().toTimeString().split(' ')[0] : null,
      motif: formData.motif,
      details: formData.articles.filter(a => a.articleId).map(a => ({
        articleId: a.articleId,
        quantite: a.quantite,
        prixUnitaire: formData.typeMouvement === 'ENTREE' ? a.prixUnitaire : null
      }))
    }
    
    await stockApi.createMouvement(data)
    alert('Mouvement enregistré avec succès')
    closeModal()
    loadMouvements()
  } catch (e) { 
    console.error('Erreur création mouvement:', e)
    alert('Erreur: ' + (e.response?.data?.message || e.message))
  }
}

const loadMouvements = async () => {
  try { 
    mouvementsList.value = (await stockApi.getMouvements()).data || [] 
  } catch (e) {
    console.error('Erreur chargement mouvements:', e)
    mouvementsList.value = []
  }
}

const loadArticles = async () => {
  try { 
    articles.value = (await stockApi.getArticles()).data || [] 
  } catch (e) { 
    console.error('Erreur chargement articles:', e)
    articles.value = []
  }
}

const loadDepots = async () => {
  try { 
    depots.value = (await stockApi.getDepots()).data || [] 
  } catch (e) { 
    console.error('Erreur chargement dépôts:', e)
    depots.value = []
  }
}

onMounted(() => { 
  loadMouvements()
  loadArticles()
  loadDepots()
})
</script>

<style scoped>
.btn-primary { 
  @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; 
}
</style>
