<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <div class="max-w-7xl mx-auto">
      <!-- En-tête -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">💰 Dashboard Ventes</h1>
        <p class="text-gray-600 mt-2">Vue d'ensemble des opérations de vente</p>
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
                <p class="text-sm text-gray-600 mb-1">Demandes Clients</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalDemandesClient }}</p>
              </div>
              <div class="bg-indigo-100 rounded-full p-3">
                <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Proformas Vente</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalProformas }}</p>
              </div>
              <div class="bg-pink-100 rounded-full p-3">
                <svg class="w-8 h-8 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Bons de Commande</p>
                <p class="text-3xl font-bold text-gray-900">{{ stats.totalBonCommande }}</p>
              </div>
              <div class="bg-teal-100 rounded-full p-3">
                <svg class="w-8 h-8 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-gray-600 mb-1">Chiffre d'Affaires</p>
                <p class="text-2xl font-bold text-gray-900">{{ formatMoney(stats.chiffreAffaires) }}</p>
              </div>
              <div class="bg-emerald-100 rounded-full p-3">
                <svg class="w-8 h-8 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- Graphiques et tableaux -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <!-- Proformas par statut -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">📊 Proformas par Statut</h3>
            <div class="space-y-3">
              <div v-for="(count, statut) in stats.proformasParStatut" :key="statut" class="flex items-center justify-between p-3 bg-gray-50 rounded">
                <span class="font-medium text-gray-700">{{ statut }}</span>
                <span class="px-3 py-1 bg-pink-100 text-pink-800 rounded-full text-sm font-semibold">{{ count }}</span>
              </div>
            </div>
          </div>

          <!-- Top Clients -->
          <div class="bg-white rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">👥 Top 5 Clients</h3>
            <div class="space-y-3">
              <div v-for="(client, index) in stats.topClients" :key="index" class="flex items-center justify-between p-3 bg-gray-50 rounded">
                <div class="flex items-center space-x-3">
                  <span class="text-2xl">{{ getMedal(index) }}</span>
                  <span class="font-medium text-gray-700">{{ client.nom }}</span>
                </div>
                <span class="px-3 py-1 bg-emerald-100 text-emerald-800 rounded-full text-sm font-semibold">{{ client.count }} proformas</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Dernières demandes -->
        <div class="bg-white rounded-lg shadow-md p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">🕒 Dernières Demandes Clients</h3>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="demande in stats.dernieresDemandesClient" :key="demande.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 text-sm text-gray-900">#{{ demande.id }}</td>
                  <td class="px-6 py-4 text-sm text-gray-600">{{ formatDate(demande.dateDemande) }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ demande.client?.nom || '-' }}</td>
                  <td class="px-6 py-4">
                    <span :class="getStatutClass(demande.statutValidation)" class="px-2 py-1 text-xs rounded-full">
                      {{ demande.statutValidation }}
                    </span>
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
  totalDemandesClient: 0,
  totalProformas: 0,
  totalBonCommande: 0,
  chiffreAffaires: 0,
  proformasParStatut: {},
  topClients: [],
  dernieresDemandesClient: []
})
const loading = ref(true)

const fetchStats = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get('http://localhost:8080/api/dashboard/ventes', {
      headers: { Authorization: `Bearer ${token}` }
    })
    stats.value = response.data
  } catch (error) {
    console.error('Erreur chargement stats ventes:', error)
  } finally {
    loading.value = false
  }
}

const formatMoney = (value) => {
  return new Intl.NumberFormat('fr-MG', { style: 'currency', currency: 'MGA' }).format(value || 0)
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('fr-FR')
}

const getMedal = (index) => {
  const medals = ['🥇', '🥈', '🥉', '4️⃣', '5️⃣']
  return medals[index] || '•'
}

const getStatutClass = (statut) => {
  const classes = {
    'EN_ATTENTE': 'bg-yellow-100 text-yellow-800',
    'VALIDE': 'bg-green-100 text-green-800',
    'REFUSE': 'bg-red-100 text-red-800'
  }
  return classes[statut] || 'bg-gray-100 text-gray-800'
}

onMounted(() => {
  fetchStats()
})
</script>
