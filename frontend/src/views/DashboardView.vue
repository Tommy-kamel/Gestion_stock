<template>
  <div class="space-y-6">
    <!-- En-tête -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Tableau de bord</h1>
        <p class="mt-1 text-sm text-gray-500">Vue d'ensemble de l'activité</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <span class="text-sm text-gray-500">{{ currentDate }}</span>
      </div>
    </div>

    <!-- Cartes statistiques -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- Demandes d'achat -->
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Demandes d'achat</p>
            <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.demandesAchat || 0 }}</p>
            <p class="text-sm text-blue-600 mt-1">{{ stats.daEnAttente || 0 }} en attente</p>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Devis vente -->
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Devis vente</p>
            <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.devisVente || 0 }}</p>
            <p class="text-sm text-green-600 mt-1">{{ stats.devisEnAttente || 0 }} en attente</p>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Total Articles -->
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Total Articles</p>
            <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.alertesStock || 0 }}</p>
            <p class="text-sm text-orange-600 mt-1">Articles en catalogue</p>
          </div>
          <div class="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center">
            <svg class="w-6 h-6 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
          </div>
        </div>
      </div>

      <!-- Total Mouvements -->
      <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Total Mouvements</p>
            <p class="text-3xl font-bold text-gray-900 mt-2">{{ stats.soldeCaisses || 0 }}</p>
            <p class="text-sm text-purple-600 mt-1">Entrées/Sorties stock</p>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- Section principale -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Dernières demandes d'achat -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100">
        <div class="p-6 border-b border-gray-100">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">Demandes d'achat récentes</h2>
            <router-link to="/achats/demandes" class="text-sm text-indigo-600 hover:text-indigo-700">
              Voir tout →
            </router-link>
          </div>
        </div>
        <div class="divide-y divide-gray-100">
          <div v-for="da in recentDA" :key="da.id" class="p-4 hover:bg-gray-50">
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium text-gray-900">{{ da.numeroDa }}</p>
                <p class="text-sm text-gray-500">{{ da.dateDemande }}</p>
              </div>
              <span :class="getStatutClass(da.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                {{ da.statut?.libelle || 'N/A' }}
              </span>
            </div>
          </div>
          <div v-if="recentDA.length === 0" class="p-6 text-center text-gray-500">
            Aucune demande d'achat
          </div>
        </div>
      </div>

      <!-- Derniers devis -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100">
        <div class="p-6 border-b border-gray-100">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">Devis vente récents</h2>
            <!-- <router-link to="/ventes/devis" class="text-sm text-indigo-600 hover:text-indigo-700">
              Voir tout →
            </router-link> -->
          </div>
        </div>
        <div class="divide-y divide-gray-100">
          <div v-for="devis in recentDevis" :key="devis.id" class="p-4 hover:bg-gray-50">
            <div class="flex items-center justify-between">
              <div>
                <p class="font-medium text-gray-900">{{ devis.numeroDevis }}</p>
                <p class="text-sm text-gray-500">{{ devis.client }} - {{ formatMontant(devis.montantTtc) }} MGA</p>
              </div>
              <span :class="getStatutClass(devis.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                {{ devis.statut?.libelle || 'N/A' }}
              </span>
            </div>
          </div>
          <div v-if="recentDevis.length === 0" class="p-6 text-center text-gray-500">
            Aucun devis
          </div>
        </div>
      </div>
    </div>

    <!-- Résumé finances -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-gradient-to-br from-indigo-500 to-indigo-600 rounded-xl shadow-sm p-6 text-white">
        <p class="text-indigo-100 text-sm font-medium">Valeur stock</p>
        <p class="text-3xl font-bold mt-2">{{ formatMontant(stats.valeurStock) }}</p>
        <p class="text-indigo-100 text-sm mt-1">MGA en stock</p>
      </div>
      <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-sm p-6 text-white">
        <p class="text-green-100 text-sm font-medium">Commandes clients</p>
        <p class="text-3xl font-bold mt-2">{{ stats.creancesClients }}</p>
        <p class="text-green-100 text-sm mt-1">Bons de commande vente</p>
      </div>
      <div class="bg-gradient-to-br from-red-500 to-red-600 rounded-xl shadow-sm p-6 text-white">
        <p class="text-red-100 text-sm font-medium">Commandes fournisseurs</p>
        <p class="text-3xl font-bold mt-2">{{ stats.dettesFournisseurs }}</p>
        <p class="text-red-100 text-sm mt-1">Bons de commande achat</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const stats = ref({
  demandesAchat: 0,
  daEnAttente: 0,
  devisVente: 0,
  devisEnAttente: 0,
  alertesStock: 0,
  soldeCaisses: 0,
  creancesClients: 0,
  dettesFournisseurs: 0,
  valeurStock: 0
})

const recentDA = ref([])
const recentDevis = ref([])

const currentDate = computed(() => {
  return new Date().toLocaleDateString('fr-FR', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  })
})

const formatMontant = (montant) => {
  if (!montant) return '0'
  return new Intl.NumberFormat('fr-FR').format(montant)
}

const getStatutClass = (code) => {
  const classes = {
    'BROUILLON': 'bg-gray-100 text-gray-800',
    'SOUMIS': 'bg-blue-100 text-blue-800',
    'APPROUVE': 'bg-green-100 text-green-800',
    'EN_ATTENTE_PROFORMA': 'bg-yellow-100 text-yellow-800',
    'EN_ATTENTE_FINANCE': 'bg-purple-100 text-purple-800',
    'VALIDE_FINANCE': 'bg-green-100 text-green-800',
    'REJETE_FINANCE': 'bg-red-100 text-red-800',
    'BC_CREE': 'bg-blue-100 text-blue-800',
    'VALIDE': 'bg-green-100 text-green-800'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const loadDashboard = async () => {
  try {
    const token = localStorage.getItem('token')
    const headers = { Authorization: `Bearer ${token}` }
    
    // Charger les données des 3 dashboards
    const [achatsRes, ventesRes, stockRes, caissesRes] = await Promise.all([
      axios.get('http://localhost:8080/api/dashboard/achats', { headers }),
      axios.get('http://localhost:8080/api/dashboard/ventes', { headers }),
      axios.get('http://localhost:8080/api/dashboard/stock', { headers }),
      axios.get('http://localhost:8080/api/finances/caisses', { headers }).catch(() => ({ data: [] }))
    ])
    
    const achats = achatsRes.data
    const ventes = ventesRes.data
    const stock = stockRes.data
    const caisses = caissesRes.data
    
    stats.value = {
      demandesAchat: achats.totalDemandesAchat || 0,
      daEnAttente: Object.entries(achats.demandesParStatut || {})
        .filter(([statut]) => statut.includes('ATTENTE') || statut.includes('SOUMIS'))
        .reduce((sum, [, count]) => sum + count, 0),
      devisVente: ventes.totalProformas || 0,
      devisEnAttente: Object.entries(ventes.proformasParStatut || {})
        .filter(([statut]) => statut.includes('ATTENTE') || statut.includes('SOUMIS'))
        .reduce((sum, [, count]) => sum + count, 0),
      alertesStock: stock.totalArticles || 0, // Total articles
      soldeCaisses: stock.totalMouvements || 0, // Total mouvements stock
      creancesClients: ventes.totalBonCommande || 0, // Nombre de commandes clients
      dettesFournisseurs: achats.totalBonCommande || 0, // Nombre de commandes fournisseurs
      valeurStock: stock.valeurTotaleStock || 0
    }
    
    // Dernières demandes d'achat
    recentDA.value = (achats.dernieresDemandesAchat || []).slice(0, 5).map(da => ({
      id: da.id,
      numeroDa: `DA-${da.id}`,
      dateDemande: new Date(da.dateDemande).toLocaleDateString('fr-FR'),
      statut: {
        code: da.status?.code || 'BROUILLON',
        libelle: da.status?.libelle || 'Brouillon'
      }
    }))
    
    // Derniers devis (proformas vente)
    recentDevis.value = (ventes.dernieresDemandesClient || []).slice(0, 5).map(dc => ({
      id: dc.id,
      numeroDevis: `PV-${dc.id}`,
      client: dc.client?.nom || 'N/A',
      montantTtc: 0, // À calculer si disponible
      statut: {
        code: dc.statutValidation || 'EN_ATTENTE',
        libelle: dc.statutValidation || 'En attente'
      }
    }))
  } catch (error) {
    console.error('Erreur chargement dashboard:', error)
    stats.value = {
      demandesAchat: 0,
      daEnAttente: 0,
      devisVente: 0,
      devisEnAttente: 0,
      alertesStock: 0,
      soldeCaisses: 0,
      creancesClients: 0,
      dettesFournisseurs: 0,
      valeurStock: 0
    }
  }
}

onMounted(() => {
  loadDashboard()
})
</script>
