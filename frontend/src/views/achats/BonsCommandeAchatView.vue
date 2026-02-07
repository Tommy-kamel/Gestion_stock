<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Bons de commande achat</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des bons de commande fournisseurs</p>
      </div>
    </div>

    <!-- Tableau des BC -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="bc in bonsCommande" :key="bc.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ bc.numeroBc }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(bc.dateCommande) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bc.fournisseur?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">{{ formatMontant(bc.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(bc.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ bc.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(bc)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  </button>
                  <button v-if="bc.statut?.code === 'BC_CREE'" @click="signerBc(bc)" class="text-green-600 hover:text-green-900" title="Signer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                  </button>
                  <button @click="imprimerBc(bc)" class="text-gray-600 hover:text-gray-900" title="Imprimer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="bonsCommande.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                Aucun bon de commande
              </td>
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
            <div><p class="text-sm text-gray-500">Date commande</p><p class="font-medium">{{ formatDate(selectedBc?.dateCommande) }}</p></div>
            <div><p class="text-sm text-gray-500">Fournisseur</p><p class="font-medium">{{ selectedBc?.fournisseur?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Montant HT</p><p class="font-medium">{{ formatMontant(selectedBc?.montantHt) }} MGA</p></div>
            <div><p class="text-sm text-gray-500">Montant TTC</p><p class="font-medium">{{ formatMontant(selectedBc?.montantTtc) }} MGA</p></div>
          </div>

          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Quantité</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unitaire</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="detail in selectedBc?.details" :key="detail.id">
                <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ detail.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(detail.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(detail.quantite * detail.prixUnitaire) }}</td>
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
const bonsCommande = ref([])
const selectedBc = ref(null)
const showDetailsModal = ref(false)

const formatDate = (date) => date ? new Date(date).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const getStatutClass = (code) => {
  const classes = {
    'BC_CREE': 'bg-blue-100 text-blue-800',
    'BC_SIGNE': 'bg-green-100 text-green-800',
    'EN_LIVRAISON': 'bg-yellow-100 text-yellow-800',
    'LIVRE': 'bg-green-100 text-green-800'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const viewDetails = (bc) => { selectedBc.value = bc; showDetailsModal.value = true }

const signerBc = async (bc) => {
  if (confirm('Signer ce bon de commande ?')) {
    try {
      await achatApi.signerBonCommande(bc.id, authStore.user?.id || 1)
      loadBonsCommande()
    } catch (e) { console.error(e) }
  }
}

const imprimerBc = (bc) => { alert('Impression du BC ' + bc.numeroBc) }

const loadBonsCommande = async () => {
  try {
    const response = await achatApi.getBonsCommande()
    bonsCommande.value = response.data || []
  } catch (e) {
    bonsCommande.value = [
      { id: 1, numeroBc: 'BC-2024-001', dateCommande: '2024-01-20', fournisseur: { nom: 'Papeterie Plus' }, montantHt: 1500000, montantTtc: 1800000, statut: { code: 'BC_CREE', libelle: 'Créé' } }
    ]
  }
}

onMounted(loadBonsCommande)
</script>

<style scoped>
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
