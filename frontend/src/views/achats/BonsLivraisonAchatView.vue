<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Bons de livraison achat</h1>
        <p class="mt-1 text-sm text-gray-500">Réception des marchandises fournisseurs</p>
      </div>
    </div>

    <!-- Tableau des BL -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BL</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dépôt</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="bl in bonsLivraison" :key="bl.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ bl.numeroBl }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(bl.dateLivraison) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bl.fournisseur?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bl.bonCommandeAchat?.numeroBc }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bl.depotReception?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(bl.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ bl.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(bl)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </button>
                  <button v-if="bl.statut?.code === 'EN_LIVRAISON'" @click="validerReception(bl)" class="text-green-600 hover:text-green-900" title="Valider réception">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="bonsLivraison.length === 0">
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
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ selectedBl?.numeroBl }}</h3>
          
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Date livraison</p><p class="font-medium">{{ formatDate(selectedBl?.dateLivraison) }}</p></div>
            <div><p class="text-sm text-gray-500">Fournisseur</p><p class="font-medium">{{ selectedBl?.fournisseur?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Dépôt réception</p><p class="font-medium">{{ selectedBl?.depotReception?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Date réception</p><p class="font-medium">{{ formatDate(selectedBl?.dateReception) || 'Non reçu' }}</p></div>
          </div>

          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté livrée</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">N° Lot</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="detail in selectedBl?.details" :key="detail.id">
                <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ detail.quantiteLivree }}</td>
                <td class="px-4 py-2 text-sm">{{ detail.numeroLot || '-' }}</td>
              </tr>
            </tbody>
          </table>

          <div class="flex justify-end">
            <button @click="showDetailsModal = false" class="btn-secondary">Fermer</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { achatApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const bonsLivraison = ref([])
const selectedBl = ref(null)
const showDetailsModal = ref(false)

const formatDate = (date) => date ? new Date(date).toLocaleDateString('fr-FR') : ''

const getStatutClass = (code) => {
  const classes = { 'EN_LIVRAISON': 'bg-yellow-100 text-yellow-800', 'LIVRE': 'bg-green-100 text-green-800' }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const viewDetails = (bl) => { selectedBl.value = bl; showDetailsModal.value = true }

const validerReception = async (bl) => {
  if (confirm('Valider la réception et mettre à jour le stock ?')) {
    try {
      await achatApi.validerReception(bl.id, authStore.user?.id || 1)
      loadBonsLivraison()
    } catch (e) { console.error(e) }
  }
}

const loadBonsLivraison = async () => {
  try {
    const response = await achatApi.getBonsLivraison()
    bonsLivraison.value = response.data || []
  } catch (e) {
    bonsLivraison.value = [
      { id: 1, numeroBl: 'BL-2024-001', dateLivraison: '2024-01-22', fournisseur: { nom: 'Papeterie Plus' }, bonCommandeAchat: { numeroBc: 'BC-2024-001' }, depotReception: { nom: 'Dépôt Central' }, statut: { code: 'EN_LIVRAISON', libelle: 'En livraison' } }
    ]
  }
}

onMounted(loadBonsLivraison)
</script>

<style scoped>
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
