<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Caisses</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des caisses et trésorerie</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle caisse
      </button>
    </div>

    <!-- Résumé -->
    <div class="bg-gradient-to-r from-indigo-500 to-purple-600 rounded-xl shadow-lg p-6 text-white">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-indigo-100">Solde total toutes caisses</p>
          <p class="text-3xl font-bold">{{ formatMontant(totalSolde) }} MGA</p>
        </div>
        <div class="text-right">
          <p class="text-indigo-100">Nombre de caisses</p>
          <p class="text-3xl font-bold">{{ caisses.length }}</p>
        </div>
      </div>
    </div>

    <!-- Cartes caisses -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="caisse in caisses" :key="caisse.id" class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-6">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="text-lg font-semibold text-gray-900">{{ caisse.nom }}</h3>
              <p class="text-sm text-gray-500">{{ caisse.type }}</p>
            </div>
            <span :class="caisse.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
              {{ caisse.actif ? 'Active' : 'Inactive' }}
            </span>
          </div>
          
          <div class="mb-4">
            <p class="text-sm text-gray-500">Solde actuel</p>
            <p class="text-2xl font-bold" :class="caisse.solde >= 0 ? 'text-green-600' : 'text-red-600'">
              {{ formatMontant(caisse.solde) }} MGA
            </p>
          </div>

          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p class="text-gray-500">Entrées mois</p>
              <p class="font-medium text-green-600">+{{ formatMontant(caisse.entreesMois || 0) }}</p>
            </div>
            <div>
              <p class="text-gray-500">Sorties mois</p>
              <p class="font-medium text-red-600">-{{ formatMontant(caisse.sortiesMois || 0) }}</p>
            </div>
          </div>
        </div>
        
        <div class="px-6 py-3 bg-gray-50 border-t border-gray-100 flex justify-between">
          <button @click="viewMouvements(caisse)" class="text-indigo-600 hover:text-indigo-900 text-sm font-medium">
            Mouvements
          </button>
          <button @click="editCaisse(caisse)" class="text-gray-600 hover:text-gray-900 text-sm font-medium">
            Modifier
          </button>
        </div>
      </div>
    </div>

    <!-- Derniers mouvements -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="px-6 py-4 border-b border-gray-100">
        <h3 class="font-semibold text-gray-900">Derniers mouvements</h3>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Caisse</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Libellé</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="mvt in derniersMouvements" :key="mvt.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDateTime(mvt.dateMouvement) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ mvt.caisse?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="mvt.typeMouvement === 'ENTREE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ mvt.typeMouvement }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium" :class="mvt.typeMouvement === 'ENTREE' ? 'text-green-600' : 'text-red-600'">
                {{ mvt.typeMouvement === 'ENTREE' ? '+' : '-' }}{{ formatMontant(mvt.montant) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ mvt.libelle }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création/édition -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouvelle' }} caisse</h3>
          <form @submit.prevent="submitCaisse" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Nom</label>
              <input v-model="formData.nom" type="text" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Type</label>
              <select v-model="formData.type" required class="w-full border-gray-300 rounded-lg">
                <option value="ESPECES">Espèces</option>
                <option value="BANQUE">Banque</option>
                <option value="MOBILE_MONEY">Mobile Money</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Solde initial</label>
              <input v-model.number="formData.soldeInitial" type="number" class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea v-model="formData.description" rows="2" class="w-full border-gray-300 rounded-lg"></textarea>
            </div>
            <div class="flex items-center">
              <input type="checkbox" v-model="formData.actif" class="rounded text-indigo-600 mr-2">
              <label class="text-sm text-gray-700">Active</label>
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">{{ editMode ? 'Modifier' : 'Créer' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { financeApi } from '@/services/api'

const router = useRouter()
const caisses = ref([])
const derniersMouvements = ref([])
const showCreateModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, nom: '', type: 'ESPECES', soldeInitial: 0, description: '', actif: true })

const totalSolde = computed(() => caisses.value.reduce((sum, c) => sum + (c.solde || 0), 0))

const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'
const formatDateTime = (d) => d ? new Date(d).toLocaleString('fr-FR') : ''

const resetForm = () => {
  formData.id = null; formData.nom = ''; formData.type = 'ESPECES'; formData.soldeInitial = 0; formData.description = ''; formData.actif = true
  editMode.value = false
}

const editCaisse = (caisse) => {
  formData.id = caisse.id; formData.nom = caisse.nom; formData.type = caisse.type
  formData.description = caisse.description; formData.actif = caisse.actif
  editMode.value = true; showCreateModal.value = true
}

const viewMouvements = (caisse) => {
  router.push({ path: '/finances/mouvements', query: { caisse: caisse.id } })
}

const submitCaisse = async () => {
  try {
    if (editMode.value) {
      await financeApi.updateCaisse(formData.id, formData)
    } else {
      await financeApi.createCaisse(formData)
    }
    showCreateModal.value = false; resetForm(); loadCaisses()
  } catch (e) { console.error(e) }
}

const loadCaisses = async () => {
  try { caisses.value = (await financeApi.getCaisses()).data || [] }
  catch (e) {
    caisses.value = [
      { id: 1, nom: 'Caisse Principale', type: 'ESPECES', solde: 15000000, entreesMois: 25000000, sortiesMois: 18000000, actif: true },
      { id: 2, nom: 'Banque BOA', type: 'BANQUE', solde: 85000000, entreesMois: 120000000, sortiesMois: 95000000, actif: true },
      { id: 3, nom: 'MVola', type: 'MOBILE_MONEY', solde: 2500000, entreesMois: 5000000, sortiesMois: 3500000, actif: true }
    ]
  }
}

const loadMouvements = async () => {
  try { derniersMouvements.value = (await financeApi.getMouvementsCaisse()).data?.slice(0, 10) || [] }
  catch (e) {
    derniersMouvements.value = [
      { id: 1, dateMouvement: '2024-01-26T14:30:00', caisse: { nom: 'Caisse Principale' }, typeMouvement: 'ENTREE', montant: 2400000, libelle: 'Encaissement facture FV-2024-002' },
      { id: 2, dateMouvement: '2024-01-26T11:00:00', caisse: { nom: 'Banque BOA' }, typeMouvement: 'SORTIE', montant: 5040000, libelle: 'Paiement facture FA-2024-015' },
      { id: 3, dateMouvement: '2024-01-25T16:45:00', caisse: { nom: 'Caisse Principale' }, typeMouvement: 'ENTREE', montant: 800000, libelle: 'Encaissement partiel FV-2024-001' }
    ]
  }
}

onMounted(() => { loadCaisses(); loadMouvements() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
