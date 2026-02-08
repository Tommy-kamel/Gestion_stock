<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">État du stock valorisé</h1>
        <p class="mt-1 text-sm text-gray-500">Valorisation des stocks selon les méthodes FIFO, LIFO, CMUP</p>
      </div>
      <div class="flex items-center space-x-3">
        <button @click="exportStock" class="btn-secondary">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          Exporter
        </button>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Entreprise</label>
          <select v-model="filterEntreprise" @change="onEntrepriseChange" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500">
            <option value="">Toutes les entreprises</option>
            <option v-for="e in entreprises" :key="e.id" :value="e.id">{{ e.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Site</label>
          <select v-model="filterSite" @change="onSiteChange" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500">
            <option value="">Tous les sites</option>
            <option v-for="s in filteredSites" :key="s.id" :value="s.id">{{ s.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Dépôt</label>
          <select v-model="filterDepot" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500">
            <option value="">Tous les dépôts</option>
            <option v-for="d in filteredDepots" :key="d.id" :value="d.id">{{ d.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Recherche article</label>
          <input v-model="searchQuery" type="text" placeholder="Référence ou désignation..." class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500">
        </div>
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
        <p class="text-sm text-gray-500">Quantité totale</p>
        <p class="text-2xl font-bold text-green-600">{{ totalQuantite }}</p>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Site</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Méthode</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantité</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Prix unitaire</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Valeur totale</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="stock in filteredStock" :key="stock.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div>
                  <p class="font-medium text-gray-900">{{ stock.article?.reference }}</p>
                  <p class="text-sm text-gray-500">{{ stock.article?.designation }}</p>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ stock.depot?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ stock.depot?.site?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getMethodeClass(stock.methodeValorisation?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ stock.methodeValorisation?.code || 'N/A' }}
                </span>
                <p class="text-xs text-gray-500 mt-1">{{ stock.methodeValorisation?.libelle }}</p>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatQuantite(stock.quantiteActuelle) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">{{ formatMontant(stock.prixUnitaire) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600">{{ formatMontant(stock.valeurTotale) }} MGA</td>
            </tr>
            <tr v-if="filteredStock.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucun stock trouvé</td>
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
const sites = ref([])
const entreprises = ref([])
const filterEntreprise = ref('')
const filterSite = ref('')
const filterDepot = ref('')
const searchQuery = ref('')

const filteredSites = computed(() => {
  if (!filterEntreprise.value) return sites.value
  return sites.value.filter(s => s.entreprise?.id === filterEntreprise.value)
})

const filteredDepots = computed(() => {
  if (!filterSite.value) return depots.value
  return depots.value.filter(d => d.site?.id === filterSite.value)
})

const filteredStock = computed(() => {
  let list = stockList.value
  if (filterDepot.value) list = list.filter(s => s.depot?.id === filterDepot.value)
  if (filterSite.value) list = list.filter(s => s.depot?.site?.id === filterSite.value)
  if (filterEntreprise.value) list = list.filter(s => s.depot?.site?.entreprise?.id === filterEntreprise.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(s => 
      s.article?.reference?.toLowerCase().includes(q) || 
      s.article?.designation?.toLowerCase().includes(q)
    )
  }
  return list
})

const totalValeur = computed(() => 
  filteredStock.value.reduce((sum, s) => sum + (s.valeurTotale || 0), 0)
)

const totalQuantite = computed(() => 
  filteredStock.value.reduce((sum, s) => sum + (parseFloat(s.quantiteActuelle) || 0), 0).toFixed(2)
)

const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(m) : '0.00'
const formatQuantite = (q) => q ? new Intl.NumberFormat('fr-FR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(q) : '0.00'

const getMethodeClass = (code) => {
  const classes = {
    'FIFO': 'bg-blue-100 text-blue-800',
    'LIFO': 'bg-purple-100 text-purple-800',
    'CMUP': 'bg-green-100 text-green-800'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const onEntrepriseChange = () => {
  filterSite.value = ''
  filterDepot.value = ''
}

const onSiteChange = () => {
  filterDepot.value = ''
}

const exportStock = () => {
  const csv = [['Article', 'Référence', 'Dépôt', 'Site', 'Méthode', 'Quantité', 'Prix unitaire', 'Valeur'].join(';')]
  filteredStock.value.forEach(s => csv.push([
    s.article?.designation || '',
    s.article?.reference || '',
    s.depot?.nom || '',
    s.depot?.site?.nom || '',
    s.methodeValorisation?.code || '',
    s.quantiteActuelle || 0,
    s.prixUnitaire || 0,
    s.valeurTotale || 0
  ].join(';')))
  const blob = new Blob([csv.join('\n')], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `valorisation_stock_${new Date().toISOString().split('T')[0]}.csv`
  link.click()
}

const loadStock = async () => {
  try { 
    const response = await stockApi.getStockValorise()
    stockList.value = response.data || []
  } catch (e) {
    console.error('Erreur chargement stock:', e)
    stockList.value = []
  }
}

const loadDepots = async () => {
  try { 
    const response = await stockApi.getDepots()
    depots.value = response.data || []
  } catch (e) { 
    console.error('Erreur chargement dépôts:', e)
    depots.value = []
  }
}

const loadSites = async () => {
  try { 
    const response = await referenceApi.getSites()
    sites.value = response.data || []
  } catch (e) { 
    console.error('Erreur chargement sites:', e)
    sites.value = []
  }
}

const loadEntreprises = async () => {
  try { 
    const response = await referenceApi.getEntreprises()
    entreprises.value = response.data || []
  } catch (e) { 
    console.error('Erreur chargement entreprises:', e)
    entreprises.value = []
  }
}

onMounted(() => { 
  loadStock()
  loadDepots()
  loadSites()
  loadEntreprises()
})
</script>

<style scoped>
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
