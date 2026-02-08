<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Bons de commande clients</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des commandes clients depuis les proformas validés</p>
      </div>
    </div>

    <!-- Section: Proformas validés -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Proformas validés</h2>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Proforma</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="(proforma, index) in proformasValides" :key="proforma.id || index" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ proforma.numeroProforma || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(proforma.dateProforma) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ proforma.demandeAchatClient?.client?.nom || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(proforma.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <button @click="creerBCDepuisProforma(proforma)" class="text-indigo-600 hover:text-indigo-900">
                  Créer BC
                </button>
              </td>
            </tr>
            <tr v-if="!proformasValides || proformasValides.length === 0">
              <td colspan="5" class="px-6 py-8 text-center text-gray-500">Aucun proforma validé</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Section: Bons de commande créés -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Bons de commande créés</h2>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="(bc, index) in bcsList" :key="bc.id || index" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ bc.numeroBc || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(bc.dateCommande) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bc.client?.nom || 'N/A' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(bc.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(bc.status?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ bc.status?.libelle || 'N/A' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(bc)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                    </svg>
                  </button>
                  <button v-if="bc.status?.code === 'EN_COURS'" @click="showLivrerModal(bc)" class="text-green-600 hover:text-green-900" title="Livrer et encaisser">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!bcsList || bcsList.length === 0">
              <td colspan="6" class="px-6 py-8 text-center text-gray-500">Aucun bon de commande</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal détails BC -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Bon de commande {{ selectedBc?.numeroBc }}</h3>
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Client</p><p class="font-medium">{{ selectedBc?.client?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Date</p><p class="font-medium">{{ formatDate(selectedBc?.dateCommande) }}</p></div>
            <div><p class="text-sm text-gray-500">Proforma origine</p><p class="font-medium">{{ selectedBc?.proformaVente?.numeroProforma }}</p></div>
            <div><p class="text-sm text-gray-500">Statut</p><p class="font-medium">{{ selectedBc?.status?.libelle }}</p></div>
          </div>
          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unit.</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="d in selectedBcDetails" :key="d.id">
                <td class="px-4 py-2 text-sm">{{ d.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ d.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(d.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(d.quantite * d.prixUnitaire) }}</td>
              </tr>
            </tbody>
            <tfoot class="bg-gray-50">
              <tr>
                <td colspan="3" class="px-4 py-2 text-right text-sm font-medium">Total TTC</td>
                <td class="px-4 py-2 text-sm font-bold">{{ formatMontant(selectedBc?.montantTtc) }} MGA</td>
              </tr>
            </tfoot>
          </table>
          <div class="flex justify-end"><button @click="showDetailsModal = false" class="btn-secondary">Fermer</button></div>
        </div>
      </div>
    </div>

    <!-- Modal Livrer et Encaisser -->
    <div v-if="showLivrerModalVisible" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showLivrerModalVisible = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Livrer et encaisser</h3>
          <p class="text-sm text-gray-600 mb-4">BC: {{ bcToLivrer?.numeroBc }}</p>
          
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Dépôt de sortie</label>
              <select v-model="livraison.depotId" class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner un dépôt</option>
                <option v-for="depot in depots" :key="depot.id" :value="depot.id">{{ depot.nom }}</option>
              </select>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Caisse d'encaissement</label>
              <select v-model="livraison.caisseId" class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner une caisse</option>
                <option v-for="caisse in caisses" :key="caisse.id" :value="caisse.id">{{ caisse.libelle }}</option>
              </select>
            </div>
          </div>

          <div class="flex justify-end space-x-2 mt-6">
            <button @click="showLivrerModalVisible = false" class="btn-secondary">Annuler</button>
            <button @click="executerLivraison" class="btn-primary">Confirmer</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { venteApi, stockApi, financeApi } from '@/services/api'

const proformasValides = ref([])
const bcsList = ref([])
const depots = ref([])
const caisses = ref([])
const showDetailsModal = ref(false)
const selectedBc = ref(null)
const selectedBcDetails = ref([])
const showLivrerModalVisible = ref(false)
const bcToLivrer = ref(null)
const livraison = ref({ depotId: '', caisseId: '' })

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'
const getStatutClass = (code) => {
  const classes = {
    'EN_COURS': 'bg-yellow-100 text-yellow-800',
    'VALIDE': 'bg-green-100 text-green-800',
    'LIVRE': 'bg-blue-100 text-blue-800'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const loadProformasValides = async () => {
  try {
    const response = await venteApi.getProformasValides()
    console.log('Proformas validés response:', response)
    console.log('Proformas validés data:', response.data)
    
    // Vérifier que response.data est un tableau
    if (Array.isArray(response.data)) {
      proformasValides.value = response.data
    } else {
      console.error('Les données ne sont pas un tableau:', response.data)
      proformasValides.value = []
    }
  } catch (e) {
    console.error('Erreur chargement proformas validés:', e)
    proformasValides.value = []
  }
}

const loadBonsCommande = async () => {
  try {
    const response = await venteApi.getBonsCommande()
    console.log('Bons de commande response:', response)
    console.log('Bons de commande data:', response.data)
    
    // Vérifier que response.data est un tableau
    if (Array.isArray(response.data)) {
      bcsList.value = response.data
    } else {
      console.error('Les données ne sont pas un tableau:', response.data)
      bcsList.value = []
    }
  } catch (e) {
    console.error('Erreur chargement BCs:', e)
    bcsList.value = []
  }
}

const loadDepots = async () => {
  try {
    depots.value = (await stockApi.getDepots()).data || []
  } catch (e) {
    console.error('Erreur chargement dépôts:', e)
  }
}

const loadCaisses = async () => {
  try {
    caisses.value = (await financeApi.getCaisses()).data || []
  } catch (e) {
    console.error('Erreur chargement caisses:', e)
  }
}

const creerBCDepuisProforma = async (proforma) => {
  if (!confirm(`Créer un bon de commande pour le proforma ${proforma.numeroProforma} ?`)) return
  
  try {
    await venteApi.creerBonCommandeDepuisProforma({
      proformaVenteId: proforma.id,
      dateCommande: new Date().toISOString().split('T')[0]
    })
    alert('Bon de commande créé avec succès !')
    loadProformasValides()
    loadBonsCommande()
  } catch (e) {
    console.error('Erreur création BC:', e)
    alert(e.response?.data || 'Erreur lors de la création du bon de commande')
  }
}

const viewDetails = async (bc) => {
  selectedBc.value = bc
  try {
    // Charger les détails si nécessaire
    selectedBcDetails.value = bc.details || []
    showDetailsModal.value = true
  } catch (e) {
    console.error('Erreur chargement détails:', e)
  }
}

const showLivrerModal = (bc) => {
  bcToLivrer.value = bc
  livraison.value = { depotId: '', caisseId: '' }
  showLivrerModalVisible.value = true
}

const executerLivraison = async () => {
  if (!livraison.value.depotId || !livraison.value.caisseId) {
    alert('Veuillez sélectionner un dépôt et une caisse')
    return
  }

  if (!confirm('Confirmer la livraison et l\'encaissement ? Cette action est irréversible.')) return

  try {
    await venteApi.livrerEtEncaisser(bcToLivrer.value.id, livraison.value)
    alert('Livraison et encaissement effectués avec succès !')
    showLivrerModalVisible.value = false
    loadBonsCommande()
  } catch (e) {
    console.error('Erreur livraison:', e)
    alert(e.response?.data || 'Erreur lors de la livraison et l\'encaissement')
  }
}

onMounted(() => {
  loadProformasValides()
  loadBonsCommande()
  loadDepots()
  loadCaisses()
})
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
