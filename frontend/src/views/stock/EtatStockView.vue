<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">État du stock</h1>
        <p class="mt-1 text-sm text-gray-500">Situation des stocks par article et dépôt</p>
      </div>
      <div class="flex items-center space-x-3">
        <select v-model="selectedMethode" class="border-gray-300 rounded-lg text-sm">
          <option value="CMUP">Valorisation CMUP</option>
          <option value="FIFO">Valorisation FIFO</option>
          <option value="LIFO">Valorisation LIFO</option>
        </select>
        <button @click="exportStock" class="btn-secondary">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          Exporter
        </button>
      </div>
    </div>

    <!-- Alertes stock -->
    <div v-if="alertes.length > 0" class="bg-red-50 border border-red-200 rounded-xl p-4">
      <div class="flex items-center mb-2">
        <svg class="w-5 h-5 text-red-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <h3 class="font-semibold text-red-800">Alertes stock ({{ alertes.length }})</h3>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-2">
        <div v-for="alerte in alertes.slice(0,6)" :key="alerte.id" class="bg-white p-2 rounded-lg text-sm">
          <span class="font-medium">{{ alerte.article?.designation }}</span>
          <span class="text-red-600 ml-2">Stock: {{ alerte.quantiteTotale - alerte.quantiteReservee }} / Min: {{ alerte.quantiteMinimum }}</span>
        </div>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterDepot" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les dépôts</option>
          <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
        </select>
        <select v-model="filterCategorie" class="border-gray-300 rounded-lg text-sm">
          <option value="">Toutes les catégories</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.libelle }}</option>
        </select>
        <input v-model="searchQuery" type="text" placeholder="Rechercher article..." class="border-gray-300 rounded-lg text-sm flex-1">
      </div>
    </div>

    <!-- Résumé valorisation -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Valeur totale stock</p>
        <p class="text-2xl font-bold text-indigo-600">{{ formatMontant(totalValeur) }} MGA</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Nombre d'articles</p>
        <p class="text-2xl font-bold text-gray-900">{{ filteredStock.length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Articles en alerte</p>
        <p class="text-2xl font-bold text-red-600">{{ alertes.length }}</p>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Article</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dépôt</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qté totale</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qté réservée</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qté disponible</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">CMUP</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Valeur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="stock in filteredStock" :key="stock.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div><p class="font-medium text-gray-900">{{ stock.article?.reference }}</p><p class="text-sm text-gray-500">{{ stock.article?.designation }}</p></div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ stock.depot?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ stock.quantiteTotale }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-orange-600">{{ stock.quantiteReservee }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ stock.quantiteTotale - stock.quantiteReservee }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ formatMontant(stock.cmup) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600">{{ formatMontant(stock.quantiteTotale * stock.cmup) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStockStatusClass(stock)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ getStockStatusLabel(stock) }}
                </span>
              </td>
            </tr>
            <tr v-if="filteredStock.length === 0">
              <td colspan="8" class="px-6 py-12 text-center text-gray-500">Aucun stock trouvé</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { stockApi, referenceApi } from '@/services/api'

const stockList = ref([])
const depots = ref([])
const categories = ref([])
const filterDepot = ref('')
const filterCategorie = ref('')
const searchQuery = ref('')
const selectedMethode = ref('CMUP')

const alertes = computed(() => stockList.value.filter(s => (s.quantiteTotale - s.quantiteReservee) <= s.quantiteMinimum))

const filteredStock = computed(() => {
  let list = stockList.value
  if (filterDepot.value) list = list.filter(s => s.depot?.id === filterDepot.value)
  if (filterCategorie.value) list = list.filter(s => s.article?.categorie?.id === filterCategorie.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(s => s.article?.reference?.toLowerCase().includes(q) || s.article?.designation?.toLowerCase().includes(q))
  }
  return list
})

const totalValeur = computed(() => filteredStock.value.reduce((sum, s) => sum + (s.quantiteTotale * (s.cmup || 0)), 0))

const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const getStockStatusClass = (stock) => {
  const dispo = stock.quantiteTotale - stock.quantiteReservee
  if (dispo <= 0) return 'bg-red-100 text-red-800'
  if (dispo <= stock.quantiteMinimum) return 'bg-yellow-100 text-yellow-800'
  return 'bg-green-100 text-green-800'
}

const getStockStatusLabel = (stock) => {
  const dispo = stock.quantiteTotale - stock.quantiteReservee
  if (dispo <= 0) return 'Rupture'
  if (dispo <= stock.quantiteMinimum) return 'Stock bas'
  return 'Normal'
}

const exportStock = () => {
  // Export CSV simple
  const csv = [['Article', 'Dépôt', 'Qté', 'CMUP', 'Valeur'].join(',')]
  filteredStock.value.forEach(s => csv.push([s.article?.designation, s.depot?.nom, s.quantiteTotale, s.cmup, s.quantiteTotale * s.cmup].join(',')))
  const blob = new Blob([csv.join('\n')], { type: 'text/csv' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = 'etat_stock.csv'
  link.click()
}

const loadStock = async () => {
  try { 
    stockList.value = (await stockApi.getAllStocks()).data || []
  }
  catch (e) {
    console.error('Erreur chargement stock:', e)
    stockList.value = []
  }
}

const loadDepots = async () => {
  try { depots.value = (await stockApi.getDepots()).data || [] }
  catch (e) { 
    console.error('Erreur chargement dépôts:', e)
    depots.value = []
  }
}

const loadCategories = async () => {
  try { categories.value = (await referenceApi.getCategories()).data || [] }
  catch (e) { 
    console.error('Erreur chargement catégories:', e)
    categories.value = []
  }
}

onMounted(() => { loadStock(); loadDepots(); loadCategories() })
</script>

<style scoped>
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
