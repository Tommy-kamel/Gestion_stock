<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Validations en attente</h1>
      <p class="text-gray-500">Documents nécessitant votre approbation</p>
    </div>

    <!-- Tabs -->
    <div class="border-b mb-6">
      <nav class="flex space-x-8">
        <button @click="activeTab = 'da'"
                :class="['pb-4 px-1 border-b-2 font-medium text-sm', 
                         activeTab === 'da' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500']">
          Demandes d'achat
          <span v-if="daEnAttente.length" class="ml-2 bg-blue-100 text-blue-600 px-2 py-1 rounded-full text-xs">
            {{ daEnAttente.length }}
          </span>
        </button>
        <button @click="activeTab = 'bc'"
                :class="['pb-4 px-1 border-b-2 font-medium text-sm', 
                         activeTab === 'bc' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500']">
          Bons de commande
          <span v-if="bcEnAttente.length" class="ml-2 bg-blue-100 text-blue-600 px-2 py-1 rounded-full text-xs">
            {{ bcEnAttente.length }}
          </span>
        </button>
        <button @click="activeTab = 'cmd'"
                :class="['pb-4 px-1 border-b-2 font-medium text-sm', 
                         activeTab === 'cmd' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500']">
          Commandes ventes
          <span v-if="cmdEnAttente.length" class="ml-2 bg-blue-100 text-blue-600 px-2 py-1 rounded-full text-xs">
            {{ cmdEnAttente.length }}
          </span>
        </button>
      </nav>
    </div>

    <!-- Demandes d'achat -->
    <div v-if="activeTab === 'da'" class="bg-white rounded-lg shadow">
      <table class="min-w-full">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° DA</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Créé par</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Motif</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nb articles</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="da in daEnAttente" :key="da.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 font-medium">{{ da.numero }}</td>
            <td class="px-6 py-4">{{ formatDate(da.dateCreation) }}</td>
            <td class="px-6 py-4">{{ da.creePar || '-' }}</td>
            <td class="px-6 py-4">{{ da.motif || '-' }}</td>
            <td class="px-6 py-4">{{ da.lignes?.length || 0 }}</td>
            <td class="px-6 py-4">
              <div class="flex space-x-2">
                <button @click="viewDA(da)" class="text-blue-600 hover:text-blue-800">Voir</button>
                <button @click="validerDA(da)" class="text-green-600 hover:text-green-800">Valider</button>
                <button @click="refuserDA(da)" class="text-red-600 hover:text-red-800">Refuser</button>
              </div>
            </td>
          </tr>
          <tr v-if="daEnAttente.length === 0">
            <td colspan="6" class="px-6 py-8 text-center text-gray-500">
              Aucune demande d'achat en attente de validation
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Bons de commande -->
    <div v-if="activeTab === 'bc'" class="bg-white rounded-lg shadow">
      <table class="min-w-full">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="bc in bcEnAttente" :key="bc.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 font-medium">{{ bc.numero }}</td>
            <td class="px-6 py-4">{{ formatDate(bc.dateCommande) }}</td>
            <td class="px-6 py-4">{{ bc.fournisseur?.nom || '-' }}</td>
            <td class="px-6 py-4">{{ formatMontant(bc.montantTotal) }}</td>
            <td class="px-6 py-4">
              <div class="flex space-x-2">
                <button @click="viewBC(bc)" class="text-blue-600 hover:text-blue-800">Voir</button>
                <button @click="validerBC(bc)" class="text-green-600 hover:text-green-800">Valider</button>
                <button @click="refuserBC(bc)" class="text-red-600 hover:text-red-800">Refuser</button>
              </div>
            </td>
          </tr>
          <tr v-if="bcEnAttente.length === 0">
            <td colspan="5" class="px-6 py-8 text-center text-gray-500">
              Aucun bon de commande en attente de validation
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Commandes ventes -->
    <div v-if="activeTab === 'cmd'" class="bg-white rounded-lg shadow">
      <table class="min-w-full">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Commande</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="cmd in cmdEnAttente" :key="cmd.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 font-medium">{{ cmd.numero }}</td>
            <td class="px-6 py-4">{{ formatDate(cmd.dateCommande) }}</td>
            <td class="px-6 py-4">{{ cmd.client?.nom || '-' }}</td>
            <td class="px-6 py-4">{{ formatMontant(cmd.montantTotal) }}</td>
            <td class="px-6 py-4">
              <div class="flex space-x-2">
                <button @click="viewCmd(cmd)" class="text-blue-600 hover:text-blue-800">Voir</button>
                <button @click="validerCmd(cmd)" class="text-green-600 hover:text-green-800">Valider</button>
                <button @click="refuserCmd(cmd)" class="text-red-600 hover:text-red-800">Refuser</button>
              </div>
            </td>
          </tr>
          <tr v-if="cmdEnAttente.length === 0">
            <td colspan="5" class="px-6 py-8 text-center text-gray-500">
              Aucune commande en attente de validation
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal détail DA -->
    <div v-if="selectedDA" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <div class="p-6 border-b flex justify-between items-center">
          <h2 class="text-xl font-bold">Demande d'achat {{ selectedDA.numero }}</h2>
          <button @click="selectedDA = null" class="text-gray-500 hover:text-gray-700">✕</button>
        </div>
        <div class="p-6">
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div>
              <p class="text-sm text-gray-500">Date création</p>
              <p class="font-medium">{{ formatDate(selectedDA.dateCreation) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Date besoin</p>
              <p class="font-medium">{{ formatDate(selectedDA.dateBesoin) }}</p>
            </div>
            <div class="col-span-2">
              <p class="text-sm text-gray-500">Motif</p>
              <p class="font-medium">{{ selectedDA.motif || '-' }}</p>
            </div>
          </div>
          
          <h3 class="font-semibold mb-2">Articles demandés</h3>
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-gray-500 border-b">
                <th class="pb-2">Article</th>
                <th class="pb-2 text-right">Quantité</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="ligne in selectedDA.lignes" :key="ligne.id" class="border-b">
                <td class="py-2">{{ ligne.article?.designation }}</td>
                <td class="py-2 text-right">{{ ligne.quantite }}</td>
              </tr>
            </tbody>
          </table>

          <div class="flex justify-end space-x-3 mt-6">
            <button @click="selectedDA = null" class="px-4 py-2 border rounded-lg">Fermer</button>
            <button @click="refuserDA(selectedDA); selectedDA = null" 
                    class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700">Refuser</button>
            <button @click="validerDA(selectedDA); selectedDA = null" 
                    class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700">Valider</button>
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

const activeTab = ref('da')
const daEnAttente = ref([])
const bcEnAttente = ref([])
const cmdEnAttente = ref([])
const selectedDA = ref(null)

function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('fr-FR')
}

function formatMontant(montant) {
  if (!montant) return '0 MGA'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(montant)
}

async function loadData() {
  try {
    // DA en attente
    const daRes = await axios.get(`${API_URL}/achats/da`)
    daEnAttente.value = (daRes.data || []).filter(da => da.statut === 'EN_ATTENTE')

    // BC en attente
    const bcRes = await axios.get(`${API_URL}/achats/bc`)
    bcEnAttente.value = (bcRes.data || []).filter(bc => bc.statut === 'EN_ATTENTE')

    // Commandes en attente
    const cmdRes = await axios.get(`${API_URL}/ventes/commandes`)
    cmdEnAttente.value = (cmdRes.data || []).filter(cmd => cmd.statut === 'DEVIS' || cmd.statut === 'EN_ATTENTE')
  } catch (error) {
    console.error('Erreur chargement:', error)
  }
}

function viewDA(da) {
  selectedDA.value = da
}

function viewBC(bc) {
  alert('Détail BC: ' + bc.numero)
}

function viewCmd(cmd) {
  alert('Détail commande: ' + cmd.numero)
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

async function validerBC(bc) {
  try {
    await axios.patch(`${API_URL}/achats/bc/${bc.id}/valider`)
    await loadData()
  } catch (error) {
    alert(error.response?.data?.error || 'Erreur lors de la validation')
  }
}

async function refuserBC(bc) {
  try {
    await axios.patch(`${API_URL}/achats/bc/${bc.id}/annuler`)
    await loadData()
  } catch (error) {
    alert(error.response?.data?.error || 'Erreur lors de l\'annulation')
  }
}

async function validerCmd(cmd) {
  try {
    await axios.patch(`${API_URL}/ventes/commandes/${cmd.id}/valider`)
    await loadData()
  } catch (error) {
    alert(error.response?.data?.error || 'Erreur lors de la validation')
  }
}

async function refuserCmd(cmd) {
  try {
    await axios.patch(`${API_URL}/ventes/commandes/${cmd.id}/annuler`)
    await loadData()
  } catch (error) {
    alert(error.response?.data?.error || 'Erreur lors de l\'annulation')
  }
}

onMounted(loadData)
</script>
