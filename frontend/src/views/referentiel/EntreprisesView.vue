<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Entreprises</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion du référentiel entreprises (filiales, fournisseurs, clients)</p>
      </div>
      <button @click="openCreate" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle entreprise
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterType" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les types</option>
          <option value="interne">Filiales</option>
          <option value="fournisseur">Fournisseurs</option>
          <option value="client">Clients</option>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nom</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">NIF</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Contact</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Téléphone</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="ent in filteredEntreprises" :key="ent.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div><p class="font-medium text-gray-900">{{ ent.nom }}</p><p class="text-sm text-gray-500">{{ ent.adresse }}</p></div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ ent.nif || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex flex-wrap gap-1">
                  <span v-if="ent.estInterne" class="px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800">Filiale</span>
                  <span v-if="ent.estFournisseur" class="px-2 py-1 text-xs font-medium rounded-full bg-purple-100 text-purple-800">Fournisseur</span>
                  <span v-if="ent.estClient" class="px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">Client</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ ent.contact || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ ent.telephone || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="ent.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ ent.actif ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="editEntreprise(ent)" class="text-indigo-600 hover:text-indigo-900" title="Modifier">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredEntreprises.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucune entreprise</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création/édition -->
    <div v-if="showModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouvelle' }} entreprise</h3>
          <form @submit.prevent="submitEntreprise" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Nom</label>
              <input v-model="formData.nom" type="text" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">NIF</label>
                <input v-model="formData.nif" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">STAT</label>
                <input v-model="formData.stat" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Adresse</label>
              <textarea v-model="formData.adresse" rows="2" class="w-full border-gray-300 rounded-lg"></textarea>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Téléphone</label>
                <input v-model="formData.telephone" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input v-model="formData.email" type="email" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Contact</label>
              <input v-model="formData.contact" type="text" class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Type d'entreprise</label>
              <div class="flex items-center space-x-4">
                <label class="flex items-center"><input type="checkbox" v-model="formData.estInterne" class="rounded text-indigo-600 mr-2"><span class="text-sm">Filiale</span></label>
                <label class="flex items-center"><input type="checkbox" v-model="formData.estFournisseur" class="rounded text-indigo-600 mr-2"><span class="text-sm">Fournisseur</span></label>
                <label class="flex items-center"><input type="checkbox" v-model="formData.estClient" class="rounded text-indigo-600 mr-2"><span class="text-sm">Client</span></label>
              </div>
            </div>
            <div class="flex items-center">
              <input type="checkbox" v-model="formData.actif" class="rounded text-indigo-600 mr-2">
              <label class="text-sm text-gray-700">Actif</label>
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showModal = false" class="btn-secondary">Annuler</button>
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
import { referenceApi } from '@/services/api'

const entreprises = ref([])
const filterType = ref('')
const searchQuery = ref('')
const showModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, nom: '', nif: '', stat: '', adresse: '', telephone: '', email: '', contact: '', estInterne: false, estFournisseur: false, estClient: false, actif: true })

const filteredEntreprises = computed(() => {
  let list = entreprises.value
  if (filterType.value === 'interne') list = list.filter(e => e.estInterne)
  else if (filterType.value === 'fournisseur') list = list.filter(e => e.estFournisseur)
  else if (filterType.value === 'client') list = list.filter(e => e.estClient)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(e => e.nom?.toLowerCase().includes(q) || e.nif?.toLowerCase().includes(q))
  }
  return list
})

const resetForm = () => {
  formData.id = null; formData.nom = ''; formData.nif = ''; formData.stat = ''; formData.adresse = ''
  formData.telephone = ''; formData.email = ''; formData.contact = ''
  formData.estInterne = false; formData.estFournisseur = false; formData.estClient = false; formData.actif = true
  editMode.value = false
}

const openCreate = () => { resetForm(); showModal.value = true }

const editEntreprise = (ent) => {
  Object.assign(formData, ent)
  editMode.value = true; showModal.value = true
}

const submitEntreprise = async () => {
  try {
    if (editMode.value) {
      await referenceApi.updateEntreprise(formData.id, formData)
    } else {
      await referenceApi.createEntreprise(formData)
    }
    showModal.value = false; loadEntreprises()
  } catch (e) { console.error(e) }
}

const loadEntreprises = async () => {
  try { entreprises.value = (await referenceApi.getEntreprises()).data || [] }
  catch (e) {
    entreprises.value = [
      { id: 1, nom: 'Mad Distrib SARL', nif: '12345678901', adresse: 'Antananarivo', telephone: '+261 20 22 000 00', estInterne: true, estFournisseur: false, estClient: false, actif: true },
      { id: 2, nom: 'Fournisseur Papeterie', nif: '98765432101', adresse: 'Antsirabe', telephone: '+261 20 44 000 00', estInterne: false, estFournisseur: true, estClient: false, actif: true },
      { id: 3, nom: 'Client Société Alpha', nif: '45678901234', adresse: 'Toamasina', telephone: '+261 20 53 000 00', estInterne: false, estFournisseur: false, estClient: true, actif: true }
    ]
  }
}

onMounted(() => loadEntreprises())
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
