<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Synthèse Direction</h1>
      <p class="text-gray-500">Vue d'ensemble de l'activité de l'entreprise</p>
    </div>

    <!-- KPIs principaux -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <!-- Achats en cours -->
      <div class="bg-white rounded-xl shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">DA en attente</p>
            <p class="text-2xl font-bold text-blue-600">{{ kpis.daEnAttente }}</p>
          </div>
          <div class="bg-blue-100 p-3 rounded-full">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
        <p class="text-xs text-gray-400 mt-2">{{ kpis.daTotal }} total ce mois</p>
      </div>

      <!-- Commandes ventes -->
      <div class="bg-white rounded-xl shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Commandes à livrer</p>
            <p class="text-2xl font-bold text-green-600">{{ kpis.commandesALivrer }}</p>
          </div>
          <div class="bg-green-100 p-3 rounded-full">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
        </div>
        <p class="text-xs text-gray-400 mt-2">{{ formatMontant(kpis.caCommandes) }} en attente</p>
      </div>

      <!-- Stock -->
      <div class="bg-white rounded-xl shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Valorisation stock</p>
            <p class="text-2xl font-bold text-purple-600">{{ formatMontant(kpis.valorisationStock) }}</p>
          </div>
          <div class="bg-purple-100 p-3 rounded-full">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
          </div>
        </div>
        <p class="text-xs text-gray-400 mt-2">{{ kpis.lotsExpires }} lots expirés</p>
      </div>

      <!-- Finance -->
      <div class="bg-white rounded-xl shadow p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Factures impayées</p>
            <p class="text-2xl font-bold text-red-600">{{ formatMontant(kpis.facturesImpayees) }}</p>
          </div>
          <div class="bg-red-100 p-3 rounded-full">
            <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
        <p class="text-xs text-gray-400 mt-2">{{ kpis.nbFacturesImpayees }} factures</p>
      </div>
    </div>

    <!-- Validations en attente -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
      <div class="bg-white rounded-xl shadow p-6">
        <h2 class="text-lg font-semibold mb-4">Demandes d'achat à valider</h2>
        <div v-if="daAValider.length === 0" class="text-center text-gray-500 py-4">
          Aucune demande en attente
        </div>
        <div v-else class="space-y-3">
          <div v-for="da in daAValider.slice(0, 5)" :key="da.id" 
               class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
            <div>
              <p class="font-medium">{{ da.numero }}</p>
              <p class="text-sm text-gray-500">{{ formatDate(da.dateCreation) }}</p>
            </div>
            <div class="flex space-x-2">
              <button @click="validerDA(da)" 
                      class="bg-green-600 text-white px-3 py-1 rounded text-sm hover:bg-green-700">
                Valider
              </button>
              <button @click="refuserDA(da)"
                      class="bg-red-600 text-white px-3 py-1 rounded text-sm hover:bg-red-700">
                Refuser
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow p-6">
        <h2 class="text-lg font-semibold mb-4">Alertes Stock</h2>
        <div v-if="alertesStock.length === 0" class="text-center text-gray-500 py-4">
          Aucune alerte
        </div>
        <div v-else class="space-y-3">
          <div v-for="alerte in alertesStock.slice(0, 5)" :key="alerte.id"
               class="flex items-center p-3 rounded-lg"
               :class="alerte.type === 'expire' ? 'bg-red-50' : 'bg-yellow-50'">
            <div :class="alerte.type === 'expire' ? 'text-red-600' : 'text-yellow-600'" class="mr-3">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                      d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
            <div>
              <p class="font-medium">{{ alerte.article }}</p>
              <p class="text-sm text-gray-500">{{ alerte.message }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Graphiques -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="bg-white rounded-xl shadow p-6">
        <h2 class="text-lg font-semibold mb-4">Évolution des achats (6 derniers mois)</h2>
        <div class="h-64 flex items-end justify-between space-x-2">
          <div v-for="(mois, idx) in graphAchats" :key="idx" class="flex-1 flex flex-col items-center">
            <div class="w-full bg-blue-500 rounded-t" :style="{ height: mois.height + '%' }"></div>
            <p class="text-xs text-gray-500 mt-2">{{ mois.label }}</p>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow p-6">
        <h2 class="text-lg font-semibold mb-4">Évolution des ventes (6 derniers mois)</h2>
        <div class="h-64 flex items-end justify-between space-x-2">
          <div v-for="(mois, idx) in graphVentes" :key="idx" class="flex-1 flex flex-col items-center">
            <div class="w-full bg-green-500 rounded-t" :style="{ height: mois.height + '%' }"></div>
            <p class="text-xs text-gray-500 mt-2">{{ mois.label }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

const kpis = ref({
  daEnAttente: 0,
  daTotal: 0,
  commandesALivrer: 0,
  caCommandes: 0,
  valorisationStock: 0,
  lotsExpires: 0,
  facturesImpayees: 0,
  nbFacturesImpayees: 0
})

const daAValider = ref([])
const alertesStock = ref([])
const graphAchats = ref([])
const graphVentes = ref([])

function formatMontant(montant) {
  if (!montant) return '0 MGA'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(montant)
}

function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

async function loadData() {
  try {
    // Charger les DA
    const daRes = await axios.get(`${API_URL}/achats/da`)
    const das = daRes.data || []
    daAValider.value = das.filter(da => da.statut === 'EN_ATTENTE')
    kpis.value.daEnAttente = daAValider.value.length
    kpis.value.daTotal = das.length

    // Charger les commandes ventes
    const cmdRes = await axios.get(`${API_URL}/ventes/commandes`)
    const cmds = cmdRes.data || []
    const cmdALivrer = cmds.filter(c => c.statut === 'VALIDEE' || c.statut === 'EN_PREPARATION')
    kpis.value.commandesALivrer = cmdALivrer.length
    kpis.value.caCommandes = cmdALivrer.reduce((sum, c) => sum + (c.montantTotal || 0), 0)

    // Charger les KPIs stock
    try {
      const stockRes = await axios.get(`${API_URL}/stock/kpis`)
      if (stockRes.data) {
        kpis.value.valorisationStock = stockRes.data.valorisationTotale || 0
        kpis.value.lotsExpires = stockRes.data.nombreLotsExpires || 0
      }
    } catch (e) {
      console.log('KPIs stock non disponibles')
    }

    // Alertes stock (lots proches expiration)
    try {
      const lotsRes = await axios.get(`${API_URL}/stock/lots/proches-expiration?joursAvant=30`)
      alertesStock.value = (lotsRes.data || []).map(lot => ({
        id: lot.id,
        article: lot.article?.designation || 'Article',
        message: `Expire le ${formatDate(lot.dateExpiration)}`,
        type: lot.dateExpiration && new Date(lot.dateExpiration) < new Date() ? 'expire' : 'warning'
      }))
    } catch (e) {
      console.log('Alertes stock non disponibles')
    }

    // Graphiques (données simulées pour l'instant)
    const mois = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin']
    graphAchats.value = mois.map((label, i) => ({
      label,
      height: Math.floor(Math.random() * 60) + 20
    }))
    graphVentes.value = mois.map((label, i) => ({
      label,
      height: Math.floor(Math.random() * 70) + 30
    }))

  } catch (error) {
    console.error('Erreur chargement données:', error)
  }
}

async function validerDA(da) {
  try {
    await axios.patch(`${API_URL}/achats/da/${da.id}/valider`)
    await loadData()
  } catch (error) {
    console.error('Erreur validation:', error)
    alert(error.response?.data?.error || 'Erreur lors de la validation')
  }
}

async function refuserDA(da) {
  const motif = prompt('Motif du refus:')
  if (!motif) return
  
  try {
    await axios.patch(`${API_URL}/achats/da/${da.id}/refuser`, { motif })
    await loadData()
  } catch (error) {
    console.error('Erreur refus:', error)
    alert(error.response?.data?.error || 'Erreur lors du refus')
  }
}

onMounted(loadData)
</script>
