<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Devis / Proformas vente</h1>
        <p class="mt-1 text-sm text-gray-500">Création et gestion des devis clients</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau devis
      </button>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Devis</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Validité</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="devis in devisList" :key="devis.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ devis.numeroDevis }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(devis.dateDevis) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ devis.entrepriseClient?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(devis.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(devis.dateValidite) }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(devis.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ devis.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(devis)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </button>
                  <button v-if="devis.statut?.code === 'BROUILLON'" @click="validerDevis(devis)" class="text-green-600 hover:text-green-900" title="Valider">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </button>
                  <button v-if="devis.statut?.code === 'VALIDE'" @click="creerBonCommande(devis)" class="text-blue-600 hover:text-blue-900" title="Créer BC">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="devisList.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucun devis</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6 max-h-[90vh] overflow-y-auto">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Nouveau devis</h3>
          <form @submit.prevent="submitDevis" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Client</label>
                <select v-model="newDevis.clientId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="c in clients" :key="c.id" :value="c.id">{{ c.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Dépôt source</label>
                <select v-model="newDevis.depotId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="d in depots" :key="d.id" :value="d.id">{{ d.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Date devis</label>
                <input v-model="newDevis.dateDevis" type="date" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Date validité</label>
                <input v-model="newDevis.dateValidite" type="date" required class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            
            <div>
              <div class="flex justify-between mb-2">
                <label class="text-sm font-medium text-gray-700">Articles (prix CMUP automatique)</label>
                <button type="button" @click="addLine" class="text-sm text-indigo-600">+ Ajouter</button>
              </div>
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-2 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                    <th class="px-2 py-2 text-left text-xs font-medium text-gray-500 w-20">Qté</th>
                    <th class="px-2 py-2 text-left text-xs font-medium text-gray-500 w-24">Stock dispo</th>
                    <th class="px-2 py-2 text-left text-xs font-medium text-gray-500 w-28">Prix unit.</th>
                    <th class="w-8"></th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="(line, idx) in newDevis.details" :key="idx">
                    <td class="px-2 py-2">
                      <select v-model="line.articleId" @change="onArticleChange(line)" required class="w-full border-gray-300 rounded-lg text-sm">
                        <option value="">Sélectionner</option>
                        <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.reference }} - {{ a.designation }}</option>
                      </select>
                    </td>
                    <td class="px-2 py-2">
                      <input v-model.number="line.quantite" type="number" min="1" class="w-full border-gray-300 rounded-lg text-sm">
                    </td>
                    <td class="px-2 py-2 text-sm" :class="line.stockDispo < line.quantite ? 'text-red-600 font-medium' : 'text-green-600'">
                      {{ line.stockDispo || 0 }}
                    </td>
                    <td class="px-2 py-2 text-sm font-medium">
                      {{ formatMontant(line.prixUnitaire) }}
                    </td>
                    <td class="px-2 py-2">
                      <button type="button" @click="removeLine(idx)" class="text-red-500">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-if="stockError" class="p-3 bg-red-50 text-red-700 rounded-lg text-sm">
              ⚠️ {{ stockError }}
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary" :disabled="!!stockError">Créer le devis</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Devis {{ selectedDevis?.numeroDevis }}</h3>
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Client</p><p class="font-medium">{{ selectedDevis?.entrepriseClient?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Date</p><p class="font-medium">{{ formatDate(selectedDevis?.dateDevis) }}</p></div>
            <div><p class="text-sm text-gray-500">Montant HT</p><p class="font-medium">{{ formatMontant(selectedDevis?.montantHt) }} MGA</p></div>
            <div><p class="text-sm text-gray-500">Montant TTC</p><p class="font-medium">{{ formatMontant(selectedDevis?.montantTtc) }} MGA</p></div>
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
              <tr v-for="d in selectedDevis?.details" :key="d.id">
                <td class="px-4 py-2 text-sm">{{ d.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ d.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(d.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(d.montantHt) }}</td>
              </tr>
            </tbody>
          </table>
          <div class="flex justify-end"><button @click="showDetailsModal = false" class="btn-secondary">Fermer</button></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { venteApi, referenceApi, stockApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const devisList = ref([])
const clients = ref([])
const depots = ref([])
const articles = ref([])
const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const selectedDevis = ref(null)

const newDevis = reactive({
  clientId: '', depotId: '', dateDevis: new Date().toISOString().split('T')[0], dateValidite: '', details: [{ articleId: '', quantite: 1, prixUnitaire: 0, stockDispo: 0 }]
})

const stockError = computed(() => {
  const line = newDevis.details.find(l => l.articleId && l.quantite > l.stockDispo)
  return line ? `Stock insuffisant pour un ou plusieurs articles` : ''
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'
const getStatutClass = (code) => ({ 'BROUILLON': 'bg-gray-100 text-gray-800', 'VALIDE': 'bg-green-100 text-green-800', 'BC_CREE': 'bg-blue-100 text-blue-800' }[code] || 'bg-gray-100 text-gray-800')

const addLine = () => newDevis.details.push({ articleId: '', quantite: 1, prixUnitaire: 0, stockDispo: 0 })
const removeLine = (idx) => { if (newDevis.details.length > 1) newDevis.details.splice(idx, 1) }

const onArticleChange = async (line) => {
  if (!line.articleId || !newDevis.depotId) return
  try {
    // Récupérer le stock et le prix CMUP
    const stockResp = await stockApi.getStock(line.articleId, newDevis.depotId)
    const stock = stockResp.data
    line.stockDispo = stock?.quantiteTotale - stock?.quantiteReservee || 0
    line.prixUnitaire = stock?.cmup || 0
  } catch (e) {
    line.stockDispo = 100; line.prixUnitaire = 15000 // Démo
  }
}

const viewDetails = (devis) => { selectedDevis.value = devis; showDetailsModal.value = true }

const validerDevis = async (devis) => {
  if (confirm('Valider ce devis ?')) {
    try { await venteApi.validerDevis(devis.id); loadDevis() } catch (e) { console.error(e) }
  }
}

const creerBonCommande = async (devis) => {
  if (confirm('Créer un bon de commande à partir de ce devis ?')) {
    try { await venteApi.creerBonCommande(devis.id, authStore.user?.id || 1); loadDevis() } catch (e) { console.error(e) }
  }
}

const submitDevis = async () => {
  if (stockError.value) return
  try {
    await venteApi.creerDevis({
      clientId: newDevis.clientId, depotId: newDevis.depotId, dateDevis: newDevis.dateDevis, dateValidite: newDevis.dateValidite,
      entrepriseFilialeId: authStore.user?.entreprise?.id || 1, personnelId: authStore.user?.id || 1,
      details: newDevis.details.filter(d => d.articleId)
    })
    showCreateModal.value = false; loadDevis()
  } catch (e) { console.error(e) }
}

const loadDevis = async () => {
  try { devisList.value = (await venteApi.getDevis()).data || [] }
  catch (e) { devisList.value = [{ id: 1, numeroDevis: 'DEV-2024-001', dateDevis: '2024-01-20', entrepriseClient: { nom: 'Client Alpha' }, montantTtc: 2400000, dateValidite: '2024-02-20', statut: { code: 'BROUILLON', libelle: 'Brouillon' } }] }
}

const loadClients = async () => {
  try { clients.value = (await referenceApi.getClients()).data || [] }
  catch (e) { clients.value = [{ id: 5, nom: 'Client Société Alpha' }] }
}

const loadDepots = async () => {
  try { depots.value = (await stockApi.getDepots()).data || [] }
  catch (e) { depots.value = [{ id: 1, nom: 'Dépôt Central' }] }
}

const loadArticles = async () => {
  try { articles.value = (await stockApi.getArticles()).data || [] }
  catch (e) { 
    console.error('Erreur chargement articles:', e)
    articles.value = []
  }
}

watch(() => newDevis.depotId, () => {
  newDevis.details.forEach(line => { if (line.articleId) onArticleChange(line) })
})

onMounted(() => { loadDevis(); loadClients(); loadDepots(); loadArticles() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors disabled:opacity-50; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
