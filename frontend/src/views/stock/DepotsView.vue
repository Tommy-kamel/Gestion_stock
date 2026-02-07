<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Dépôts / Entrepôts</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des emplacements de stockage</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau dépôt
      </button>
    </div>

    <!-- Cartes dépôts -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="depot in depots" :key="depot.id" class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="p-6">
          <div class="flex items-start justify-between">
            <div>
              <h3 class="text-lg font-semibold text-gray-900">{{ depot.nom }}</h3>
              <p class="text-sm text-gray-500">{{ depot.adresse }}</p>
            </div>
            <span :class="depot.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
              {{ depot.actif ? 'Actif' : 'Inactif' }}
            </span>
          </div>
          
          <div class="mt-4 grid grid-cols-2 gap-4">
            <div class="bg-gray-50 rounded-lg p-3">
              <p class="text-xs text-gray-500">Articles en stock</p>
              <p class="text-xl font-bold text-gray-900">{{ depot.nombreArticles || 0 }}</p>
            </div>
            <div class="bg-gray-50 rounded-lg p-3">
              <p class="text-xs text-gray-500">Valeur stock</p>
              <p class="text-xl font-bold text-indigo-600">{{ formatMontant(depot.valeurStock || 0) }}</p>
            </div>
          </div>

          <div class="mt-4 text-sm text-gray-500">
            <p v-if="depot.responsable"><span class="font-medium">Responsable:</span> {{ depot.responsable?.nom }}</p>
            <p v-if="depot.telephone"><span class="font-medium">Tél:</span> {{ depot.telephone }}</p>
          </div>
        </div>
        
        <div class="px-6 py-3 bg-gray-50 border-t border-gray-100 flex justify-end space-x-2">
          <button @click="viewStock(depot)" class="text-indigo-600 hover:text-indigo-900 text-sm font-medium">
            Voir stock
          </button>
          <button @click="editDepot(depot)" class="text-gray-600 hover:text-gray-900 text-sm font-medium">
            Modifier
          </button>
        </div>
      </div>

      <div v-if="depots.length === 0" class="col-span-full text-center py-12 text-gray-500">
        Aucun dépôt configuré
      </div>
    </div>

    <!-- Modal création/édition -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouveau' }} dépôt</h3>
          <form @submit.prevent="submitDepot" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Nom</label>
              <input v-model="formData.nom" type="text" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Adresse</label>
              <textarea v-model="formData.adresse" rows="2" class="w-full border-gray-300 rounded-lg"></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Téléphone</label>
              <input v-model="formData.telephone" type="text" class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Responsable</label>
              <select v-model="formData.responsableId" class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option v-for="p in personnel" :key="p.id" :value="p.id">{{ p.nom }} {{ p.prenom }}</option>
              </select>
            </div>
            <div class="flex items-center">
              <input type="checkbox" v-model="formData.actif" class="rounded text-indigo-600 mr-2">
              <label class="text-sm text-gray-700">Actif</label>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { stockApi, referenceApi } from '@/services/api'

const router = useRouter()
const depots = ref([])
const personnel = ref([])
const showCreateModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, nom: '', adresse: '', telephone: '', responsableId: '', actif: true })

const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const resetForm = () => {
  formData.id = null; formData.nom = ''; formData.adresse = ''; formData.telephone = ''; formData.responsableId = ''; formData.actif = true
  editMode.value = false
}

const editDepot = (depot) => {
  formData.id = depot.id; formData.nom = depot.nom; formData.adresse = depot.adresse
  formData.telephone = depot.telephone; formData.responsableId = depot.responsable?.id; formData.actif = depot.actif
  editMode.value = true; showCreateModal.value = true
}

const viewStock = (depot) => {
  router.push({ path: '/stock/etat', query: { depot: depot.id } })
}

const submitDepot = async () => {
  try {
    if (editMode.value) {
      await stockApi.updateDepot(formData.id, formData)
    } else {
      await stockApi.createDepot(formData)
    }
    showCreateModal.value = false; resetForm(); loadDepots()
  } catch (e) { console.error(e) }
}

const loadDepots = async () => {
  try { depots.value = (await stockApi.getDepots()).data || [] }
  catch (e) {
    depots.value = [
      { id: 1, nom: 'Dépôt Central', adresse: 'Zone industrielle Ankorondrano', telephone: '+261 20 22 000 00', responsable: { nom: 'Rakoto' }, actif: true, nombreArticles: 45, valeurStock: 125000000 },
      { id: 2, nom: 'Dépôt Annexe Tamatave', adresse: 'Port de Toamasina', telephone: '+261 20 53 000 00', responsable: { nom: 'Rasoa' }, actif: true, nombreArticles: 28, valeurStock: 78000000 }
    ]
  }
}

const loadPersonnel = async () => {
  try { personnel.value = (await referenceApi.getPersonnel()).data || [] }
  catch (e) { personnel.value = [{ id: 1, nom: 'Rakoto', prenom: 'Jean' }, { id: 2, nom: 'Rasoa', prenom: 'Marie' }] }
}

onMounted(() => { loadDepots(); loadPersonnel() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
