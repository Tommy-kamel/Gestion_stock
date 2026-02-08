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
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex flex-wrap items-center gap-4">
        <select v-model="filterType" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les types</option>
          <option value="ENTREE">Entrées</option>
          <option value="SORTIE">Sorties</option>
        </select>
        <select v-model="filterArticle" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les articles</option>
          <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.reference }}</option>
        </select>
        <input v-model="filterDateDebut" type="date" class="border-gray-300 rounded-lg text-sm">
        <input v-model="filterDateFin" type="date" class="border-gray-300 rounded-lg text-sm">
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Article</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dépôt</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantité</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Document</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Motif</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="mvt in filteredMouvements" :key="mvt.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDateTime(mvt.dateMouvement) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getTypeClass(mvt.typeMouvement)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ mvt.typeMouvement }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ mvt.article?.designation }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ mvt.depot?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium" :class="mvt.typeMouvement === 'SORTIE' ? 'text-red-600' : 'text-green-600'">
                {{ mvt.typeMouvement === 'SORTIE' ? '-' : '+' }}{{ mvt.quantite }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ mvt.numeroDocument || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ mvt.motif || '-' }}</td>
            </tr>
            <tr v-if="filteredMouvements.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucun mouvement</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Nouveau mouvement</h3>
          <form @submit.prevent="submitMouvement" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Type</label>
              <select v-model="formData.typeMouvement" required class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option value="ENTREE">Entrée</option>
                <option value="SORTIE">Sortie</option>
                <option value="AJUSTEMENT">Ajustement</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Article</label>
              <select v-model="formData.articleId" required class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.reference }} - {{ a.designation }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Dépôt</label>
              <select v-model="formData.depotId" required class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Quantité</label>
              <input v-model.number="formData.quantite" type="number" min="1" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div v-if="formData.typeMouvement === 'ENTREE'">
              <label class="block text-sm font-medium text-gray-700 mb-1">Prix unitaire</label>
              <input v-model.number="formData.prixUnitaire" type="number" min="0" class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Motif</label>
              <input v-model="formData.motif" type="text" class="w-full border-gray-300 rounded-lg">
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Enregistrer</button>
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
const filterArticle = ref('')
const filterDateDebut = ref('')
const filterDateFin = ref('')
const showCreateModal = ref(false)
const formData = reactive({ typeMouvement: '', articleId: '', depotId: '', quantite: 1, prixUnitaire: 0, motif: '' })

const filteredMouvements = computed(() => {
  let list = mouvementsList.value
  if (filterType.value) list = list.filter(m => m.typeMouvement === filterType.value)
  if (filterArticle.value) list = list.filter(m => m.article?.id === filterArticle.value)
  if (filterDateDebut.value) list = list.filter(m => new Date(m.dateMouvement) >= new Date(filterDateDebut.value))
  if (filterDateFin.value) list = list.filter(m => new Date(m.dateMouvement) <= new Date(filterDateFin.value))
  return list.sort((a, b) => new Date(b.dateMouvement) - new Date(a.dateMouvement))
})

const formatDateTime = (d) => d ? new Date(d).toLocaleString('fr-FR') : ''
const getTypeClass = (type) => ({ 'ENTREE': 'bg-green-100 text-green-800', 'SORTIE': 'bg-red-100 text-red-800', 'TRANSFERT': 'bg-blue-100 text-blue-800', 'AJUSTEMENT': 'bg-yellow-100 text-yellow-800' }[type] || 'bg-gray-100 text-gray-800')

const resetForm = () => { formData.typeMouvement = ''; formData.articleId = ''; formData.depotId = ''; formData.quantite = 1; formData.prixUnitaire = 0; formData.motif = '' }

const submitMouvement = async () => {
  try {
    await stockApi.createMouvement(formData)
    showCreateModal.value = false; resetForm(); loadMouvements()
  } catch (e) { console.error(e) }
}

const loadMouvements = async () => {
  try { mouvementsList.value = (await stockApi.getMouvements()).data || [] }
  catch (e) {
    console.error('Erreur chargement mouvements:', e)
    mouvementsList.value = []
  }
}

const loadArticles = async () => {
  try { articles.value = (await stockApi.getArticles()).data || [] }
  catch (e) { 
    console.error('Erreur chargement articles:', e)
    articles.value = []
  }
}

const loadDepots = async () => {
  try { depots.value = (await stockApi.getDepots()).data || [] }
  catch (e) { 
    console.error('Erreur chargement dépôts:', e)
    depots.value = []
  }
}

onMounted(() => { loadMouvements(); loadArticles(); loadDepots() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
