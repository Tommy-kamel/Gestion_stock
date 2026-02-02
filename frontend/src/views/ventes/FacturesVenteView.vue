<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Factures clients</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des factures de vente</p>
      </div>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Total factures</p>
        <p class="text-2xl font-bold text-gray-900">{{ stats.total }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">En attente</p>
        <p class="text-2xl font-bold text-yellow-600">{{ stats.enAttente }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Payées</p>
        <p class="text-2xl font-bold text-green-600">{{ stats.payees }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Montant à encaisser</p>
        <p class="text-2xl font-bold text-indigo-600">{{ formatMontant(stats.montantRestant) }} MGA</p>
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
        <input v-model="searchQuery" type="text" placeholder="Rechercher..." class="border-gray-300 rounded-lg text-sm">
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant payé</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Échéance</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="facture in filteredFactures" :key="facture.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ facture.numeroFacture }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(facture.dateFacture) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ facture.bonLivraisonVente?.bonCommandeVente?.devisVente?.entrepriseClient?.nom || facture.client }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(facture.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <span :class="facture.montantPaye >= facture.montantTtc ? 'text-green-600' : 'text-orange-600'">
                  {{ formatMontant(facture.montantPaye || 0) }} MGA
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500" :class="isOverdue(facture) ? 'text-red-600 font-medium' : ''">
                {{ formatDate(facture.dateEcheance) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(facture.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ facture.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(facture)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                  </button>
                  <button v-if="facture.statut?.code !== 'PAYEE'" @click="enregistrerPaiement(facture)" class="text-green-600 hover:text-green-900" title="Encaisser">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z"/>
                    </svg>
                  </button>
                  <button @click="printFacture(facture)" class="text-gray-600 hover:text-gray-900" title="Imprimer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredFactures.length === 0">
              <td colspan="8" class="px-6 py-12 text-center text-gray-500">Aucune facture</td>
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
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Facture {{ selectedFacture?.numeroFacture }}</h3>
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Client</p><p class="font-medium">{{ selectedFacture?.bonLivraisonVente?.bonCommandeVente?.devisVente?.entrepriseClient?.nom || selectedFacture?.client }}</p></div>
            <div><p class="text-sm text-gray-500">Date</p><p class="font-medium">{{ formatDate(selectedFacture?.dateFacture) }}</p></div>
            <div><p class="text-sm text-gray-500">Échéance</p><p class="font-medium">{{ formatDate(selectedFacture?.dateEcheance) }}</p></div>
            <div><p class="text-sm text-gray-500">BL origine</p><p class="font-medium">{{ selectedFacture?.bonLivraisonVente?.numeroBl }}</p></div>
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
              <tr v-for="d in selectedFacture?.details" :key="d.id">
                <td class="px-4 py-2 text-sm">{{ d.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ d.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(d.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(d.montantHt) }}</td>
              </tr>
            </tbody>
            <tfoot class="bg-gray-50">
              <tr><td colspan="3" class="px-4 py-2 text-right text-sm">Total HT</td><td class="px-4 py-2 text-sm">{{ formatMontant(selectedFacture?.montantHt) }} MGA</td></tr>
              <tr><td colspan="3" class="px-4 py-2 text-right text-sm">TVA (20%)</td><td class="px-4 py-2 text-sm">{{ formatMontant(selectedFacture?.montantTva) }} MGA</td></tr>
              <tr><td colspan="3" class="px-4 py-2 text-right text-sm font-bold">Total TTC</td><td class="px-4 py-2 text-sm font-bold">{{ formatMontant(selectedFacture?.montantTtc) }} MGA</td></tr>
              <tr><td colspan="3" class="px-4 py-2 text-right text-sm">Montant payé</td><td class="px-4 py-2 text-sm text-green-600">{{ formatMontant(selectedFacture?.montantPaye || 0) }} MGA</td></tr>
              <tr><td colspan="3" class="px-4 py-2 text-right text-sm font-medium">Reste à payer</td><td class="px-4 py-2 text-sm font-medium text-red-600">{{ formatMontant((selectedFacture?.montantTtc || 0) - (selectedFacture?.montantPaye || 0)) }} MGA</td></tr>
            </tfoot>
          </table>
          <div class="flex justify-end"><button @click="showDetailsModal = false" class="btn-secondary">Fermer</button></div>
        </div>
      </div>
    </div>

    <!-- Modal paiement -->
    <div v-if="showPaiementModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showPaiementModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Enregistrer un encaissement</h3>
          <form @submit.prevent="submitPaiement" class="space-y-4">
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-sm text-gray-500">Facture: {{ selectedFacture?.numeroFacture }}</p>
              <p class="text-sm">Reste à encaisser: <span class="font-bold">{{ formatMontant((selectedFacture?.montantTtc || 0) - (selectedFacture?.montantPaye || 0)) }} MGA</span></p>
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
              <button type="submit" class="btn-primary">Encaisser</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { venteApi, financeApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const facturesList = ref([])
const caisses = ref([])
const filterStatut = ref('')
const searchQuery = ref('')
const showDetailsModal = ref(false)
const showPaiementModal = ref(false)
const selectedFacture = ref(null)
const paiementData = reactive({ factureId: null, montant: 0, modePaiement: 'ESPECES', reference: '', caisseId: '' })

const stats = computed(() => {
  const total = facturesList.value.length
  const enAttente = facturesList.value.filter(f => f.statut?.code === 'EN_ATTENTE').length
  const payees = facturesList.value.filter(f => f.statut?.code === 'PAYEE').length
  const montantRestant = facturesList.value.reduce((sum, f) => sum + ((f.montantTtc || 0) - (f.montantPaye || 0)), 0)
  return { total, enAttente, payees, montantRestant }
})

const filteredFactures = computed(() => {
  let list = facturesList.value
  if (filterStatut.value) list = list.filter(f => f.statut?.code === filterStatut.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(f => f.numeroFacture?.toLowerCase().includes(q) || f.client?.toLowerCase().includes(q))
  }
  return list
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'
const isOverdue = (f) => f.dateEcheance && new Date(f.dateEcheance) < new Date() && f.statut?.code !== 'PAYEE'
const getStatutClass = (code) => ({ 'EN_ATTENTE': 'bg-yellow-100 text-yellow-800', 'PARTIELLEMENT_PAYEE': 'bg-orange-100 text-orange-800', 'PAYEE': 'bg-green-100 text-green-800' }[code] || 'bg-gray-100 text-gray-800')

const viewDetails = (facture) => { selectedFacture.value = facture; showDetailsModal.value = true }
const printFacture = (facture) => window.print()

const enregistrerPaiement = (facture) => {
  selectedFacture.value = facture
  paiementData.factureId = facture.id
  paiementData.montant = (facture.montantTtc || 0) - (facture.montantPaye || 0)
  showPaiementModal.value = true
}

const submitPaiement = async () => {
  try {
    await financeApi.enregistrerPaiementVente({
      factureId: paiementData.factureId, montant: paiementData.montant,
      modePaiement: paiementData.modePaiement, reference: paiementData.reference,
      caisseId: paiementData.caisseId, personnelId: authStore.user?.id || 1
    })
    showPaiementModal.value = false
    loadFactures()
  } catch (e) { console.error(e) }
}

const loadFactures = async () => {
  try { facturesList.value = (await venteApi.getFactures()).data || [] }
  catch (e) {
    facturesList.value = [
      { id: 1, numeroFacture: 'FV-2024-001', dateFacture: '2024-01-26', client: 'Client Alpha', montantTtc: 2400000, montantPaye: 0, dateEcheance: '2024-02-26', statut: { code: 'EN_ATTENTE', libelle: 'En attente' } },
      { id: 2, numeroFacture: 'FV-2024-002', dateFacture: '2024-01-20', client: 'Client Beta', montantTtc: 1800000, montantPaye: 1800000, dateEcheance: '2024-02-20', statut: { code: 'PAYEE', libelle: 'Payée' } }
    ]
  }
}

const loadCaisses = async () => {
  try { caisses.value = (await financeApi.getCaisses()).data || [] }
  catch (e) { caisses.value = [{ id: 1, nom: 'Caisse Principale' }, { id: 2, nom: 'Caisse Annexe' }] }
}

onMounted(() => { loadFactures(); loadCaisses() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
