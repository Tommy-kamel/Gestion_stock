<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Bons de livraison clients</h1>
        <p class="mt-1 text-sm text-gray-500">Suivi des expéditions et livraisons</p>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterStatut" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les statuts</option>
          <option value="BROUILLON">Brouillon</option>
          <option value="EXPEDIE">Expédié</option>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BL</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Livreur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="bl in filteredBls" :key="bl.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ bl.numeroBl }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(bl.dateBl) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bl.bonCommandeVente?.devisVente?.entrepriseClient?.nom || bl.client }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bl.bonCommandeVente?.numeroBc }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bl.livreur?.nom || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(bl.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ bl.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(bl)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                  </button>
                  <button v-if="bl.statut?.code === 'BROUILLON'" @click="expedierBl(bl)" class="text-orange-600 hover:text-orange-900" title="Expédier">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
                    </svg>
                  </button>
                  <button v-if="bl.statut?.code === 'EXPEDIE'" @click="confirmerLivraison(bl)" class="text-green-600 hover:text-green-900" title="Confirmer livraison">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                    </svg>
                  </button>
                  <button v-if="bl.statut?.code === 'LIVRE'" @click="creerFacture(bl)" class="text-blue-600 hover:text-blue-900" title="Créer facture">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                    </svg>
                  </button>
                  <button @click="printBl(bl)" class="text-gray-600 hover:text-gray-900" title="Imprimer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredBls.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucun bon de livraison</td>
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
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Bon de livraison {{ selectedBl?.numeroBl }}</h3>
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Client</p><p class="font-medium">{{ selectedBl?.bonCommandeVente?.devisVente?.entrepriseClient?.nom || selectedBl?.client }}</p></div>
            <div><p class="text-sm text-gray-500">Date BL</p><p class="font-medium">{{ formatDate(selectedBl?.dateBl) }}</p></div>
            <div><p class="text-sm text-gray-500">BC origine</p><p class="font-medium">{{ selectedBl?.bonCommandeVente?.numeroBc }}</p></div>
            <div><p class="text-sm text-gray-500">Livreur</p><p class="font-medium">{{ selectedBl?.livreur?.nom || '-' }}</p></div>
            <div><p class="text-sm text-gray-500">Date expédition</p><p class="font-medium">{{ formatDate(selectedBl?.dateExpedition) || '-' }}</p></div>
            <div><p class="text-sm text-gray-500">Date livraison</p><p class="font-medium">{{ formatDate(selectedBl?.dateLivraison) || '-' }}</p></div>
          </div>
          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté commandée</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté livrée</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="d in selectedBl?.details" :key="d.id">
                <td class="px-4 py-2 text-sm">{{ d.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ d.quantiteCommandee }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ d.quantiteLivree }}</td>
              </tr>
            </tbody>
          </table>
          <div class="flex justify-end"><button @click="showDetailsModal = false" class="btn-secondary">Fermer</button></div>
        </div>
      </div>
    </div>

    <!-- Modal expédition -->
    <div v-if="showExpeditionModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showExpeditionModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Expédier le BL</h3>
          <form @submit.prevent="submitExpedition" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Livreur</label>
              <select v-model="expeditionData.livreurId" required class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option v-for="p in personnel" :key="p.id" :value="p.id">{{ p.nom }} {{ p.prenom }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date expédition</label>
              <input v-model="expeditionData.dateExpedition" type="date" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showExpeditionModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Expédier</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { venteApi, referenceApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const blsList = ref([])
const personnel = ref([])
const filterStatut = ref('')
const searchQuery = ref('')
const showDetailsModal = ref(false)
const showExpeditionModal = ref(false)
const selectedBl = ref(null)
const expeditionData = reactive({ blId: null, livreurId: '', dateExpedition: new Date().toISOString().split('T')[0] })

const filteredBls = computed(() => {
  let list = blsList.value
  if (filterStatut.value) list = list.filter(bl => bl.statut?.code === filterStatut.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(bl => bl.numeroBl?.toLowerCase().includes(q) || bl.client?.toLowerCase().includes(q))
  }
  return list
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const getStatutClass = (code) => ({ 'BROUILLON': 'bg-gray-100 text-gray-800', 'EXPEDIE': 'bg-orange-100 text-orange-800', 'LIVRE': 'bg-green-100 text-green-800' }[code] || 'bg-gray-100 text-gray-800')

const viewDetails = (bl) => { selectedBl.value = bl; showDetailsModal.value = true }
const printBl = (bl) => window.print()

const expedierBl = (bl) => {
  expeditionData.blId = bl.id
  showExpeditionModal.value = true
}

const submitExpedition = async () => {
  try {
    await venteApi.expedierLivraison(expeditionData.blId, expeditionData.livreurId, expeditionData.dateExpedition)
    showExpeditionModal.value = false
    loadBls()
  } catch (e) { console.error(e) }
}

const confirmerLivraison = async (bl) => {
  if (confirm('Confirmer la livraison au client ?')) {
    try {
      await venteApi.confirmerLivraison(bl.id, new Date().toISOString().split('T')[0])
      loadBls()
    } catch (e) { console.error(e) }
  }
}

const creerFacture = async (bl) => {
  if (confirm('Créer une facture pour ce bon de livraison ?')) {
    try {
      await venteApi.creerFacture(bl.id, authStore.user?.id || 1)
      loadBls()
      alert('Facture créée avec succès')
    } catch (e) { console.error(e) }
  }
}

const loadBls = async () => {
  try { blsList.value = (await venteApi.getBonsLivraison()).data || [] }
  catch (e) {
    blsList.value = [
      { id: 1, numeroBl: 'BLV-2024-001', dateBl: '2024-01-24', client: 'Client Alpha', bonCommandeVente: { numeroBc: 'BCV-2024-001', devisVente: { entrepriseClient: { nom: 'Client Alpha' } } }, statut: { code: 'BROUILLON', libelle: 'Brouillon' } },
      { id: 2, numeroBl: 'BLV-2024-002', dateBl: '2024-01-25', client: 'Client Beta', bonCommandeVente: { numeroBc: 'BCV-2024-002', devisVente: { entrepriseClient: { nom: 'Client Beta' } } }, livreur: { nom: 'Rakoto' }, dateExpedition: '2024-01-25', statut: { code: 'EXPEDIE', libelle: 'Expédié' } }
    ]
  }
}

const loadPersonnel = async () => {
  try { personnel.value = (await referenceApi.getPersonnel()).data || [] }
  catch (e) { personnel.value = [{ id: 1, nom: 'Rakoto', prenom: 'Jean' }, { id: 2, nom: 'Rasoa', prenom: 'Marie' }] }
}

onMounted(() => { loadBls(); loadPersonnel() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
