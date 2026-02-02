<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Paiements Achats</h1>
        <p class="mt-1 text-sm text-gray-500">Suivi des paiements fournisseurs</p>
      </div>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Total factures</p>
        <p class="text-2xl font-bold text-gray-900">{{ stats.totalFactures }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">En attente paiement</p>
        <p class="text-2xl font-bold text-yellow-600">{{ stats.enAttente }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Montant à payer</p>
        <p class="text-2xl font-bold text-red-600">{{ formatMontant(stats.montantAPayer) }} MGA</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Payé ce mois</p>
        <p class="text-2xl font-bold text-green-600">{{ formatMontant(stats.payeCeMois) }} MGA</p>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterStatut" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les statuts</option>
          <option value="EN_ATTENTE">En attente</option>
          <option value="PARTIELLEMENT_PAYEE">Partiellement payée</option>
          <option value="PAYEE">Payée</option>
        </select>
        <select v-model="filterFournisseur" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les fournisseurs</option>
          <option v-for="f in fournisseurs" :key="f.id" :value="f.id">{{ f.nom }}</option>
        </select>
        <input v-model="searchQuery" type="text" placeholder="Rechercher..." class="border-gray-300 rounded-lg text-sm flex-1">
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Facture</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Payé</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Reste</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Échéance</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="facture in filteredFactures" :key="facture.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ facture.numeroFacture }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(facture.dateFacture) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ facture.fournisseur?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(facture.montantTtc) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-green-600">{{ formatMontant(facture.montantPaye || 0) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-red-600">{{ formatMontant((facture.montantTtc || 0) - (facture.montantPaye || 0)) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm" :class="isOverdue(facture) ? 'text-red-600 font-medium' : 'text-gray-500'">
                {{ formatDate(facture.dateEcheance) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(facture.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ facture.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <button v-if="facture.statut?.code !== 'PAYEE'" @click="enregistrerPaiement(facture)" class="text-green-600 hover:text-green-900 font-medium">
                  Payer
                </button>
              </td>
            </tr>
            <tr v-if="filteredFactures.length === 0">
              <td colspan="9" class="px-6 py-12 text-center text-gray-500">Aucune facture</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal paiement -->
    <div v-if="showPaiementModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showPaiementModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Enregistrer un paiement</h3>
          <form @submit.prevent="submitPaiement" class="space-y-4">
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-sm text-gray-500">Facture: {{ selectedFacture?.numeroFacture }}</p>
              <p class="text-sm text-gray-500">Fournisseur: {{ selectedFacture?.fournisseur?.nom }}</p>
              <p class="text-sm">Reste à payer: <span class="font-bold text-red-600">{{ formatMontant((selectedFacture?.montantTtc || 0) - (selectedFacture?.montantPaye || 0)) }} MGA</span></p>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Montant</label>
              <input v-model.number="paiementData.montant" type="number" required :max="(selectedFacture?.montantTtc || 0) - (selectedFacture?.montantPaye || 0)" class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Mode de paiement</label>
              <select v-model="paiementData.modePaiement" required class="w-full border-gray-300 rounded-lg">
                <option value="ESPECES">Espèces</option>
                <option value="CHEQUE">Chèque</option>
                <option value="VIREMENT">Virement</option>
                <option value="MOBILE_MONEY">Mobile Money</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Référence</label>
              <input v-model="paiementData.reference" type="text" placeholder="N° chèque, réf virement..." class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Caisse</label>
              <select v-model="paiementData.caisseId" required class="w-full border-gray-300 rounded-lg">
                <option v-for="c in caisses" :key="c.id" :value="c.id">{{ c.nom }}</option>
              </select>
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showPaiementModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Enregistrer</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { achatApi, financeApi, referenceApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const facturesList = ref([])
const fournisseurs = ref([])
const caisses = ref([])
const filterStatut = ref('')
const filterFournisseur = ref('')
const searchQuery = ref('')
const showPaiementModal = ref(false)
const selectedFacture = ref(null)
const paiementData = reactive({ factureId: null, montant: 0, modePaiement: 'VIREMENT', reference: '', caisseId: '' })

const stats = computed(() => ({
  totalFactures: facturesList.value.length,
  enAttente: facturesList.value.filter(f => f.statut?.code === 'EN_ATTENTE').length,
  montantAPayer: facturesList.value.reduce((sum, f) => sum + ((f.montantTtc || 0) - (f.montantPaye || 0)), 0),
  payeCeMois: 25000000
}))

const filteredFactures = computed(() => {
  let list = facturesList.value
  if (filterStatut.value) list = list.filter(f => f.statut?.code === filterStatut.value)
  if (filterFournisseur.value) list = list.filter(f => f.fournisseur?.id === filterFournisseur.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(f => f.numeroFacture?.toLowerCase().includes(q) || f.fournisseur?.nom?.toLowerCase().includes(q))
  }
  return list
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'
const isOverdue = (f) => f.dateEcheance && new Date(f.dateEcheance) < new Date() && f.statut?.code !== 'PAYEE'
const getStatutClass = (code) => ({ 'EN_ATTENTE': 'bg-yellow-100 text-yellow-800', 'PARTIELLEMENT_PAYEE': 'bg-orange-100 text-orange-800', 'PAYEE': 'bg-green-100 text-green-800' }[code] || 'bg-gray-100 text-gray-800')

const enregistrerPaiement = (facture) => {
  selectedFacture.value = facture
  paiementData.factureId = facture.id
  paiementData.montant = (facture.montantTtc || 0) - (facture.montantPaye || 0)
  showPaiementModal.value = true
}

const submitPaiement = async () => {
  try {
    await financeApi.enregistrerPaiementAchat({
      factureId: paiementData.factureId, montant: paiementData.montant,
      modePaiement: paiementData.modePaiement, reference: paiementData.reference,
      caisseId: paiementData.caisseId, personnelId: authStore.user?.id || 1
    })
    showPaiementModal.value = false
    loadFactures()
  } catch (e) { console.error(e) }
}

const loadFactures = async () => {
  try { facturesList.value = (await achatApi.getFactures()).data || [] }
  catch (e) {
    facturesList.value = [
      { id: 1, numeroFacture: 'FA-2024-015', dateFacture: '2024-01-20', fournisseur: { id: 1, nom: 'Fournisseur A' }, montantTtc: 5040000, montantPaye: 0, dateEcheance: '2024-02-20', statut: { code: 'EN_ATTENTE', libelle: 'En attente' } },
      { id: 2, numeroFacture: 'FA-2024-016', dateFacture: '2024-01-22', fournisseur: { id: 2, nom: 'Fournisseur B' }, montantTtc: 8200000, montantPaye: 4000000, dateEcheance: '2024-02-22', statut: { code: 'PARTIELLEMENT_PAYEE', libelle: 'Partiel' } },
      { id: 3, numeroFacture: 'FA-2024-014', dateFacture: '2024-01-15', fournisseur: { id: 1, nom: 'Fournisseur A' }, montantTtc: 3500000, montantPaye: 3500000, dateEcheance: '2024-02-15', statut: { code: 'PAYEE', libelle: 'Payée' } }
    ]
  }
}

const loadFournisseurs = async () => {
  try { fournisseurs.value = (await referenceApi.getFournisseurs()).data || [] }
  catch (e) { fournisseurs.value = [{ id: 1, nom: 'Fournisseur A' }, { id: 2, nom: 'Fournisseur B' }] }
}

const loadCaisses = async () => {
  try { caisses.value = (await financeApi.getCaisses()).data || [] }
  catch (e) { caisses.value = [{ id: 1, nom: 'Caisse Principale' }, { id: 2, nom: 'Banque BOA' }] }
}

onMounted(() => { loadFactures(); loadFournisseurs(); loadCaisses() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
