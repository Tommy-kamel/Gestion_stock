<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Lots de stock</h1>
        <p class="mt-1 text-sm text-gray-500">Suivi des lots par article (FIFO/LIFO)</p>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterArticle" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les articles</option>
          <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.reference }} - {{ a.designation }}</option>
        </select>
        <select v-model="filterDepot" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les dépôts</option>
          <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
        </select>
        <label class="flex items-center space-x-2 text-sm">
          <input type="checkbox" v-model="showEpuises" class="rounded text-indigo-600">
          <span>Afficher lots épuisés</span>
        </label>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Lot</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Article</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dépôt</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date entrée</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qté initiale</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qté restante</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Prix unitaire</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Valeur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Expiration</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="lot in filteredLots" :key="lot.id" class="hover:bg-gray-50" :class="lot.quantiteRestante === 0 ? 'opacity-50' : ''">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ lot.numeroLot }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div><p class="font-medium">{{ lot.article?.reference }}</p><p class="text-gray-500">{{ lot.article?.designation }}</p></div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ lot.depot?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(lot.dateEntree) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ lot.quantiteInitiale }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium" :class="lot.quantiteRestante === 0 ? 'text-red-600' : 'text-green-600'">
                {{ lot.quantiteRestante }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ formatMontant(lot.prixUnitaire) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600">{{ formatMontant(lot.quantiteRestante * lot.prixUnitaire) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="isExpiringSoon(lot) ? 'text-orange-600 font-medium' : isExpired(lot) ? 'text-red-600 font-medium' : 'text-gray-500'">
                {{ formatDate(lot.dateExpiration) || '-' }}
                <span v-if="isExpired(lot)" class="ml-1">(Expiré)</span>
              </td>
            </tr>
            <tr v-if="filteredLots.length === 0">
              <td colspan="9" class="px-6 py-12 text-center text-gray-500">Aucun lot</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Résumé -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Total lots actifs</p>
        <p class="text-2xl font-bold text-gray-900">{{ filteredLots.filter(l => l.quantiteRestante > 0).length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Valeur totale</p>
        <p class="text-2xl font-bold text-indigo-600">{{ formatMontant(totalValeur) }} MGA</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Lots expirant bientôt</p>
        <p class="text-2xl font-bold text-orange-600">{{ lotsExpirant.length }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { stockApi } from '@/services/api'

const lotsList = ref([])
const articles = ref([])
const depots = ref([])
const filterArticle = ref('')
const filterDepot = ref('')
const showEpuises = ref(false)

const filteredLots = computed(() => {
  let list = lotsList.value
  if (filterArticle.value) list = list.filter(l => l.article?.id === filterArticle.value)
  if (filterDepot.value) list = list.filter(l => l.depot?.id === filterDepot.value)
  if (!showEpuises.value) list = list.filter(l => l.quantiteRestante > 0)
  return list.sort((a, b) => new Date(a.dateEntree) - new Date(b.dateEntree))
})

const totalValeur = computed(() => filteredLots.value.reduce((sum, l) => sum + (l.quantiteRestante * l.prixUnitaire), 0))
const lotsExpirant = computed(() => lotsList.value.filter(l => isExpiringSoon(l) && !isExpired(l)))

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const isExpired = (lot) => lot.dateExpiration && new Date(lot.dateExpiration) < new Date()
const isExpiringSoon = (lot) => {
  if (!lot.dateExpiration) return false
  const exp = new Date(lot.dateExpiration)
  const today = new Date()
  const diff = (exp - today) / (1000 * 60 * 60 * 24)
  return diff > 0 && diff <= 30
}

const loadLots = async () => {
  try { 
    lotsList.value = (await stockApi.getLots()).data || []
  }
  catch (e) {
    console.error('Erreur chargement lots:', e)
    lotsList.value = []
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

onMounted(() => { loadLots(); loadArticles(); loadDepots() })
</script>
