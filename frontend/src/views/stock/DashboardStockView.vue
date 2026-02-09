<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto">
      <!-- En-tête -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">📦 Dashboard Stock</h1>
        <p class="text-gray-600 mt-2">Vue d'ensemble de l'état du stock</p>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>

      <!-- Statistiques -->
      <div v-else>
        <!-- Cartes KPI -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Total Articles</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalArticles }}</p>
              </div>
              <div class="bg-cyan-100 rounded-full p-3">
                <svg class="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"></path>
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Dépôts</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalDepots }}</p>
              </div>
              <div class="bg-orange-100 rounded-full p-3">
                <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Mouvements</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalMouvements }}</p>
              </div>
              <div class="bg-violet-100 rounded-full p-3">
                <svg class="w-8 h-8 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"></path>
                </svg>
              </div>
            </div>
          </div>

          <!-- <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Valeur Totale</p>
                <p class="text-2xl font-bold text-gray-900">{{ formatMoney(stats.valeurTotaleStock) }}</p>
              </div>
              <div class="bg-lime-100 rounded-full p-3">
                <svg class="w-8 h-8 text-lime-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
              </div>
            </div>
          </div> -->
        </div>

        <!-- Alerte rupture -->
        <div v-if="stats.articlesEnRupture > 0" class="bg-red-50 p-4 mb-6 rounded-lg">
          <div class="flex items-center">
            <svg class="w-6 h-6 text-red-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
            </svg>
            <p class="text-red-800 font-semibold">⚠️ Alerte : {{ stats.articlesEnRupture }} article(s) en rupture de stock</p>
          </div>
        </div>

        <!-- Graphiques et tableaux -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <!-- Mouvements par type -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">📊 Mouvements par Type</h3>
            <div class="space-y-3">
              <div v-for="(count, type) in stats.mouvementsParType" :key="type" class="flex items-center justify-between p-3 bg-gray-50 rounded">
                <div class="flex items-center space-x-2">
                  <span v-if="type === 'ENTREE'" class="text-green-600">↗️</span>
                  <span v-else class="text-red-600">↘️</span>
                  <span class="font-medium text-gray-700">{{ type }}</span>
                </div>
                <span class="px-3 py-1 bg-violet-100 text-violet-800 rounded-full text-sm font-semibold">{{ count }}</span>
              </div>
            </div>
          </div>

          <!-- Top Articles valorisés -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">💎 Top 5 Articles Valorisés</h3>
            <div class="space-y-3">
              <div v-for="(article, index) in stats.topArticles" :key="index" class="p-3 bg-gray-50 rounded">
                <div class="flex items-center justify-between mb-1">
                  <div class="flex items-center space-x-2">
                    <span class="text-xl">{{ getMedal(index) }}</span>
                    <span class="font-medium text-gray-700">{{ article.nom }}</span>
                  </div>
                  <span class="text-sm font-semibold text-lime-700">{{ formatMoney(article.valeur) }}</span>
                </div>
                <div class="text-xs text-gray-500">Quantité: {{ article.quantite }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Derniers mouvements -->
        <div class="bg-white rounded-lg shadow-md p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">🕒 Derniers Mouvements</h3>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Article</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dépôt</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantité</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="mvt in stats.derniersMouvements" :key="mvt.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(mvt.dateMouvement) }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ mvt.article?.nom || '-' }}</td>
                  <td class="px-6 py-4 text-sm text-gray-600">{{ mvt.depot?.nom || '-' }}</td>
                  <td class="px-6 py-4">
                    <span :class="mvt.typeMouvement === 'ENTREE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs rounded-full">
                      {{ mvt.typeMouvement === 'ENTREE' ? '↗️ ENTRÉE' : '↘️ SORTIE' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-sm font-semibold" :class="mvt.typeMouvement === 'ENTREE' ? 'text-green-600' : 'text-red-600'">
                    {{ mvt.typeMouvement === 'ENTREE' ? mvt.quantiteEntree : mvt.quantiteSortie }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const stats = ref({
  totalArticles: 0,
  totalDepots: 0,
  totalMouvements: 0,
  valeurTotaleStock: 0,
  articlesEnRupture: 0,
  topArticles: [],
  mouvementsParType: {},
  derniersMouvements: []
})
const loading = ref(true)

const fetchStats = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('http://localhost:8080/api/dashboard/stock', {
      headers: { Authorization: `Bearer ${token}` }
    })
    stats.value = response.data
  } catch (error) {
    console.error('Erreur chargement stats stock:', error)
  } finally {
    loading.value = false
  }
}

const formatMoney = (value) => {
  return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(value || 0)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const getMedal = (index) => {
  const medals = ['🥇', '🥈', '🥉', '4️⃣', '5️⃣']
  return medals[index] || '•'
}

onMounted(() => {
  fetchStats()
})
</script>
