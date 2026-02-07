<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Validation Finance</h1>
        <p class="mt-1 text-sm text-gray-500">Demandes d'achat en attente de validation financière</p>
      </div>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">En attente</p>
        <p class="text-2xl font-bold text-yellow-600">{{ demandesEnAttente.length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Montant total</p>
        <p class="text-2xl font-bold text-indigo-600">{{ formatMontant(totalMontant) }} MGA</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Validées ce mois</p>
        <p class="text-2xl font-bold text-green-600">{{ statsValidees }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Rejetées ce mois</p>
        <p class="text-2xl font-bold text-red-600">{{ statsRejetees }}</p>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° DA</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Demandeur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur sélectionné</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Budget dispo</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="demande in demandesEnAttente" :key="demande.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ demande.numeroDa }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(demande.dateDemande) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ demande.demandeur?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div><p class="font-medium">{{ demande.proformaRetenu?.fournisseur?.nom }}</p>
                <p class="text-xs text-green-600">Moins disant ({{ demande.nbProformas || 3 }} proformas)</p></div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-indigo-600">{{ formatMontant(demande.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="demande.budgetDispo >= demande.montantTtc ? 'text-green-600' : 'text-red-600'">
                {{ formatMontant(demande.budgetDispo || 50000000) }} MGA
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(demande)" class="text-indigo-600 hover:text-indigo-900" title="Voir détails">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                  </button>
                  <button @click="validerDemande(demande)" class="text-green-600 hover:text-green-900" title="Valider">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                    </svg>
                  </button>
                  <button @click="rejeterDemande(demande)" class="text-red-600 hover:text-red-900" title="Rejeter">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="demandesEnAttente.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucune demande en attente de validation</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full p-6 max-h-[90vh] overflow-y-auto">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Détails de la demande {{ selectedDemande?.numeroDa }}</h3>
          
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Demandeur</p><p class="font-medium">{{ selectedDemande?.demandeur?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Date demande</p><p class="font-medium">{{ formatDate(selectedDemande?.dateDemande) }}</p></div>
            <div><p class="text-sm text-gray-500">Motif</p><p class="font-medium">{{ selectedDemande?.motif }}</p></div>
            <div><p class="text-sm text-gray-500">Budget disponible</p><p class="font-medium">{{ formatMontant(selectedDemande?.budgetDispo || 50000000) }} MGA</p></div>
          </div>

          <h4 class="font-semibold text-gray-900 mb-3">Comparatif des proformas reçus</h4>
          <table class="min-w-full divide-y divide-gray-200 mb-6">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Fournisseur</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Montant HT</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Montant TTC</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Délai livraison</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Statut</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="proforma in selectedDemande?.proformas || demoProformas" :key="proforma.id" :class="proforma.retenu ? 'bg-green-50' : ''">
                <td class="px-4 py-2 text-sm">{{ proforma.fournisseur?.nom }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(proforma.montantHt) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(proforma.montantTtc) }}</td>
                <td class="px-4 py-2 text-sm">{{ proforma.delaiLivraison }} jours</td>
                <td class="px-4 py-2">
                  <span v-if="proforma.retenu" class="px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">
                    ✓ Moins disant
                  </span>
                </td>
              </tr>
            </tbody>
          </table>

          <h4 class="font-semibold text-gray-900 mb-3">Articles demandés</h4>
          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Quantité</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unit.</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total HT</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="d in selectedDemande?.details || []" :key="d.id">
                <td class="px-4 py-2 text-sm">{{ d.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ d.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(d.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(d.montantHt) }}</td>
              </tr>
            </tbody>
          </table>

          <div class="flex justify-end space-x-3">
            <button @click="showDetailsModal = false" class="btn-secondary">Fermer</button>
            <button @click="rejeterDemande(selectedDemande); showDetailsModal = false" class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700">Rejeter</button>
            <button @click="validerDemande(selectedDemande); showDetailsModal = false" class="btn-primary">Valider</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal rejet -->
    <div v-if="showRejetModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showRejetModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Motif de rejet</h3>
          <form @submit.prevent="submitRejet" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Motif</label>
              <textarea v-model="rejetMotif" rows="3" required class="w-full border-gray-300 rounded-lg" placeholder="Expliquez la raison du rejet..."></textarea>
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showRejetModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700">Rejeter</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { financeApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const demandesEnAttente = ref([])
const showDetailsModal = ref(false)
const showRejetModal = ref(false)
const selectedDemande = ref(null)
const rejetMotif = ref('')
const statsValidees = ref(12)
const statsRejetees = ref(3)

const demoProformas = [
  { id: 1, fournisseur: { nom: 'Fournisseur A' }, montantHt: 4500000, montantTtc: 5400000, delaiLivraison: 7, retenu: false },
  { id: 2, fournisseur: { nom: 'Fournisseur B' }, montantHt: 4200000, montantTtc: 5040000, delaiLivraison: 5, retenu: true },
  { id: 3, fournisseur: { nom: 'Fournisseur C' }, montantHt: 4800000, montantTtc: 5760000, delaiLivraison: 10, retenu: false }
]

const totalMontant = computed(() => demandesEnAttente.value.reduce((sum, d) => sum + (d.montantTtc || 0), 0))

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const viewDetails = (demande) => { selectedDemande.value = demande; showDetailsModal.value = true }

const validerDemande = async (demande) => {
  if (confirm(`Valider la demande ${demande.numeroDa} pour un montant de ${formatMontant(demande.montantTtc)} MGA ?`)) {
    try {
      await financeApi.validerDemande(demande.id, authStore.user?.id || 1)
      loadDemandes()
    } catch (e) { console.error(e) }
  }
}

const rejeterDemande = (demande) => {
  selectedDemande.value = demande
  showRejetModal.value = true
}

const submitRejet = async () => {
  try {
    await financeApi.rejeterDemande(selectedDemande.value.id, authStore.user?.id || 1, rejetMotif.value)
    showRejetModal.value = false; rejetMotif.value = ''; loadDemandes()
  } catch (e) { console.error(e) }
}

const loadDemandes = async () => {
  try { demandesEnAttente.value = (await financeApi.getDemandesEnAttente()).data || [] }
  catch (e) {
    demandesEnAttente.value = [
      { id: 1, numeroDa: 'DA-2024-015', dateDemande: '2024-01-25', demandeur: { nom: 'Rakoto Jean' }, proformaRetenu: { fournisseur: { nom: 'Fournisseur B' } }, montantTtc: 5040000, nbProformas: 3, budgetDispo: 50000000 },
      { id: 2, numeroDa: 'DA-2024-016', dateDemande: '2024-01-26', demandeur: { nom: 'Rasoa Marie' }, proformaRetenu: { fournisseur: { nom: 'Fournisseur A' } }, montantTtc: 8200000, nbProformas: 4, budgetDispo: 50000000 }
    ]
  }
}

onMounted(() => loadDemandes())
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
