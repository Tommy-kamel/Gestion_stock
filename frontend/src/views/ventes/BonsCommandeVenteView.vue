<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Bons de commande clients</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des commandes clients</p>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterStatut" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les statuts</option>
          <option value="EN_ATTENTE">En attente</option>
          <option value="CONFIRME">Confirmé</option>
          <option value="LIVRE">Livré</option>
        </select>
        <input v-model="searchQuery" type="text" placeholder="Rechercher..." class="border-gray-300 rounded-lg text-sm">
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Devis</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="bc in filteredBcs" :key="bc.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ bc.numeroBc }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(bc.dateBc) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bc.devisVente?.entrepriseClient?.nom || bc.client }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bc.devisVente?.numeroDevis }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(bc.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(bc.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ bc.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(bc)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                  </button>
                  <button v-if="bc.statut?.code === 'EN_ATTENTE'" @click="confirmerBc(bc)" class="text-green-600 hover:text-green-900" title="Confirmer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                    </svg>
                  </button>
                  <button v-if="bc.statut?.code === 'CONFIRME'" @click="creerBonLivraison(bc)" class="text-blue-600 hover:text-blue-900" title="Créer BL">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7v8a2 2 0 002 2h6M8 7V5a2 2 0 012-2h4.586a1 1 0 01.707.293l4.414 4.414a1 1 0 01.293.707V15a2 2 0 01-2 2h-2M8 7H6a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2v-2"/>
                    </svg>
                  </button>
                  <button @click="printBc(bc)" class="text-gray-600 hover:text-gray-900" title="Imprimer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredBcs.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucun bon de commande</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Bon de commande {{ selectedBc?.numeroBc }}</h3>
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Client</p><p class="font-medium">{{ selectedBc?.devisVente?.entrepriseClient?.nom || selectedBc?.client }}</p></div>
            <div><p class="text-sm text-gray-500">Date</p><p class="font-medium">{{ formatDate(selectedBc?.dateBc) }}</p></div>
            <div><p class="text-sm text-gray-500">Devis origine</p><p class="font-medium">{{ selectedBc?.devisVente?.numeroDevis }}</p></div>
            <div><p class="text-sm text-gray-500">Statut</p><p class="font-medium">{{ selectedBc?.statut?.libelle }}</p></div>
          </div>
          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unit.</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total HT</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="d in selectedBc?.details" :key="d.id">
                <td class="px-4 py-2 text-sm">{{ d.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ d.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(d.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(d.montantHt) }}</td>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { venteApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const bcsList = ref([])
const filterStatut = ref('')
const searchQuery = ref('')
const showDetailsModal = ref(false)
const selectedBc = ref(null)

const filteredBcs = computed(() => {
  let list = bcsList.value
  if (filterStatut.value) list = list.filter(bc => bc.statut?.code === filterStatut.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(bc => bc.numeroBc?.toLowerCase().includes(q) || bc.devisVente?.entrepriseClient?.nom?.toLowerCase().includes(q))
  }
  return list
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'
const getStatutClass = (code) => ({ 'EN_ATTENTE': 'bg-yellow-100 text-yellow-800', 'CONFIRME': 'bg-green-100 text-green-800', 'LIVRE': 'bg-blue-100 text-blue-800' }[code] || 'bg-gray-100 text-gray-800')

const viewDetails = (bc) => { selectedBc.value = bc; showDetailsModal.value = true }
const printBc = (bc) => window.print()

const confirmerBc = async (bc) => {
  if (confirm('Confirmer ce bon de commande ?')) {
    try {
      // API pour confirmer le BC
      await venteApi.confirmerBonCommande(bc.id)
      loadBcs()
    } catch (e) { console.error(e) }
  }
}

const creerBonLivraison = async (bc) => {
  if (confirm('Créer un bon de livraison pour cette commande ?')) {
    try {
      await venteApi.creerBonLivraison(bc.id, authStore.user?.id || 1)
      loadBcs()
      alert('Bon de livraison créé avec succès')
    } catch (e) { console.error(e) }
  }
}

const loadBcs = async () => {
  try { bcsList.value = (await venteApi.getBonsCommande()).data || [] }
  catch (e) {
    bcsList.value = [
      { id: 1, numeroBc: 'BCV-2024-001', dateBc: '2024-01-22', client: 'Client Alpha', devisVente: { numeroDevis: 'DEV-2024-001', entrepriseClient: { nom: 'Client Alpha' } }, montantTtc: 2400000, statut: { code: 'EN_ATTENTE', libelle: 'En attente' } },
      { id: 2, numeroBc: 'BCV-2024-002', dateBc: '2024-01-23', client: 'Client Beta', devisVente: { numeroDevis: 'DEV-2024-002', entrepriseClient: { nom: 'Client Beta' } }, montantTtc: 1800000, statut: { code: 'CONFIRME', libelle: 'Confirmé' } }
    ]
  }
}

onMounted(() => loadBcs())
</script>

<style scoped>
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
