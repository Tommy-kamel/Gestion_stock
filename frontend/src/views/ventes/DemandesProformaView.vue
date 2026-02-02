<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Demandes de proforma</h1>
        <p class="mt-1 text-sm text-gray-500">Demandes reçues des clients</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle demande
      </button>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Demande</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="demande in demandes" :key="demande.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ demande.numeroDemande }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(demande.dateDemande) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ demande.client?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(demande.statut?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ demande.statut?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <button v-if="demande.statut?.code === 'BROUILLON'" @click="creerDevis(demande)" class="text-green-600 hover:text-green-900" title="Créer devis">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </button>
              </td>
            </tr>
            <tr v-if="demandes.length === 0">
              <td colspan="5" class="px-6 py-12 text-center text-gray-500">Aucune demande</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-2xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Nouvelle demande de proforma</h3>
          <form @submit.prevent="submitDemande" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Client</label>
                <select v-model="newDemande.clientId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="c in clients" :key="c.id" :value="c.id">{{ c.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Date</label>
                <input v-model="newDemande.dateDemande" type="date" required class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div>
              <div class="flex justify-between mb-2">
                <label class="text-sm font-medium text-gray-700">Articles</label>
                <button type="button" @click="addLine" class="text-sm text-indigo-600">+ Ajouter</button>
              </div>
              <div class="space-y-2">
                <div v-for="(line, idx) in newDemande.details" :key="idx" class="flex space-x-2">
                  <select v-model="line.articleId" required class="flex-1 border-gray-300 rounded-lg text-sm">
                    <option value="">Article</option>
                    <option v-for="a in articles" :key="a.id" :value="a.id">{{ a.reference }} - {{ a.designation }}</option>
                  </select>
                  <input v-model.number="line.quantite" type="number" min="1" placeholder="Qté" class="w-24 border-gray-300 rounded-lg text-sm">
                  <button type="button" @click="removeLine(idx)" class="text-red-500"><svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg></button>
                </div>
              </div>
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Créer</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { venteApi, referenceApi, stockApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const router = useRouter()
const authStore = useAuthStore()
const demandes = ref([])
const clients = ref([])
const articles = ref([])
const showCreateModal = ref(false)

const newDemande = reactive({
  clientId: '', dateDemande: new Date().toISOString().split('T')[0], details: [{ articleId: '', quantite: 1 }]
})

const formatDate = (d) => d ? new Date(d).toLocaleDateString('fr-FR') : ''
const getStatutClass = (code) => ({ 'BROUILLON': 'bg-gray-100 text-gray-800', 'VALIDE': 'bg-green-100 text-green-800' }[code] || 'bg-gray-100 text-gray-800')
const addLine = () => newDemande.details.push({ articleId: '', quantite: 1 })
const removeLine = (idx) => { if (newDemande.details.length > 1) newDemande.details.splice(idx, 1) }

const submitDemande = async () => {
  try {
    await venteApi.creerDemandeProforma({ ...newDemande, entrepriseId: authStore.user?.entreprise?.id || 1 })
    showCreateModal.value = false
    loadDemandes()
  } catch (e) { console.error(e) }
}

const creerDevis = (demande) => {
  router.push({ path: '/ventes/devis', query: { demandeId: demande.id } })
}

const loadDemandes = async () => {
  try { demandes.value = (await venteApi.getDemandesProforma()).data || [] }
  catch (e) { demandes.value = [{ id: 1, numeroDemande: 'DPV-2024-001', dateDemande: '2024-01-15', client: { nom: 'Client Alpha' }, statut: { code: 'BROUILLON', libelle: 'Brouillon' } }] }
}

const loadClients = async () => {
  try { clients.value = (await referenceApi.getClients()).data || [] }
  catch (e) { clients.value = [{ id: 5, nom: 'Client Société Alpha' }, { id: 6, nom: 'Client Entreprise Beta' }] }
}

const loadArticles = async () => {
  try { articles.value = (await stockApi.getArticles()).data || [] }
  catch (e) { 
    console.error('Erreur chargement articles:', e)
    articles.value = []
  }
}

onMounted(() => { loadDemandes(); loadClients(); loadArticles() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
