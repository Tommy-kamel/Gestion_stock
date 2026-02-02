<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Clients</h1>
        <p class="mt-1 text-sm text-gray-500">Liste des entreprises clientes</p>
      </div>
      <button @click="openCreate" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau client
      </button>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Total clients</p>
        <p class="text-2xl font-bold text-gray-900">{{ clients.length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Actifs</p>
        <p class="text-2xl font-bold text-green-600">{{ clients.filter(c => c.actif).length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Créances totales</p>
        <p class="text-2xl font-bold text-orange-600">{{ formatMontant(totalCreances) }} MGA</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">CA ce mois</p>
        <p class="text-2xl font-bold text-indigo-600">{{ formatMontant(caMois) }} MGA</p>
      </div>
    </div>

    <!-- Recherche -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <input v-model="searchQuery" type="text" placeholder="Rechercher un client..." class="w-full border-gray-300 rounded-lg">
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">NIF</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Contact</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Téléphone</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Créance</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="client in filteredClients" :key="client.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div><p class="font-medium text-gray-900">{{ client.nom }}</p><p class="text-sm text-gray-500">{{ client.adresse }}</p></div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ client.nif || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ client.contact || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ client.telephone || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium" :class="client.creance > 0 ? 'text-orange-600' : 'text-green-600'">
                {{ formatMontant(client.creance || 0) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="client.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ client.actif ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewHistorique(client)" class="text-indigo-600 hover:text-indigo-900" title="Historique">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                    </svg>
                  </button>
                  <button @click="editClient(client)" class="text-gray-600 hover:text-gray-900" title="Modifier">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredClients.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">Aucun client trouvé</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouveau' }} client</h3>
          <form @submit.prevent="submitClient" class="space-y-4">
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
              <label class="block text-sm font-medium text-gray-700 mb-1">Plafond crédit</label>
              <input v-model.number="formData.plafondCredit" type="number" class="w-full border-gray-300 rounded-lg">
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

const clients = ref([])
const searchQuery = ref('')
const showModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, nom: '', nif: '', stat: '', adresse: '', telephone: '', email: '', contact: '', plafondCredit: 0, actif: true })

const totalCreances = computed(() => clients.value.reduce((sum, c) => sum + (c.creance || 0), 0))
const caMois = ref(18500000)

const filteredClients = computed(() => {
  if (!searchQuery.value) return clients.value
  const q = searchQuery.value.toLowerCase()
  return clients.value.filter(c => c.nom?.toLowerCase().includes(q) || c.contact?.toLowerCase().includes(q))
})

const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const resetForm = () => {
  formData.id = null; formData.nom = ''; formData.nif = ''; formData.stat = ''; formData.adresse = ''
  formData.telephone = ''; formData.email = ''; formData.contact = ''; formData.plafondCredit = 0; formData.actif = true
  editMode.value = false
}

const openCreate = () => { resetForm(); showModal.value = true }

const editClient = (c) => {
  Object.assign(formData, c)
  editMode.value = true; showModal.value = true
}

const viewHistorique = (c) => {
  alert(`Historique ventes pour ${c.nom}`)
}

const submitClient = async () => {
  try {
    const data = { ...formData, estClient: true }
    if (editMode.value) {
      await referenceApi.updateEntreprise(formData.id, data)
    } else {
      await referenceApi.createEntreprise(data)
    }
    showModal.value = false; loadClients()
  } catch (e) { console.error(e) }
}

const loadClients = async () => {
  try { clients.value = (await referenceApi.getClients()).data || [] }
  catch (e) {
    clients.value = [
      { id: 1, nom: 'Société Alpha SARL', nif: '11111111', adresse: 'Antananarivo', telephone: '+261 20 22 100 00', email: 'contact@alpha.mg', contact: 'M. Andria', actif: true, creance: 1600000 },
      { id: 2, nom: 'Entreprise Beta', nif: '22222222', adresse: 'Antsirabe', telephone: '+261 20 44 200 00', email: 'info@beta.mg', contact: 'Mme Ravo', actif: true, creance: 0 },
      { id: 3, nom: 'Client Gamma', nif: '33333333', adresse: 'Toamasina', telephone: '+261 20 53 300 00', email: 'gamma@mail.mg', contact: 'M. Solo', actif: true, creance: 5600000 }
    ]
  }
}

onMounted(() => loadClients())
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
