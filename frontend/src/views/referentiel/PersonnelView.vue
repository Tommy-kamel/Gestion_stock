<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Personnel</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des employés</p>
      </div>
      <button @click="openCreate" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvel employé
      </button>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Total employés</p>
        <p class="text-2xl font-bold text-gray-900">{{ personnel.length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Actifs</p>
        <p class="text-2xl font-bold text-green-600">{{ personnel.filter(p => p.actif).length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Départements</p>
        <p class="text-2xl font-bold text-indigo-600">{{ new Set(personnel.map(p => p.departement)).size }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Responsables</p>
        <p class="text-2xl font-bold text-purple-600">{{ personnel.filter(p => p.estResponsable).length }}</p>
      </div>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterDepartement" class="border-gray-300 rounded-lg text-sm">
          <option value="">Tous les départements</option>
          <option v-for="dep in departements" :key="dep" :value="dep">{{ dep }}</option>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Matricule</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nom & Prénom</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Département</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fonction</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Téléphone</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="emp in filteredPersonnel" :key="emp.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ emp.matricule }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center mr-3">
                    <span class="text-sm font-medium text-indigo-600">{{ emp.nom?.charAt(0) }}{{ emp.prenom?.charAt(0) }}</span>
                  </div>
                  <div><p class="font-medium text-gray-900">{{ emp.nom }} {{ emp.prenom }}</p></div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ emp.departement || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ emp.fonction || '-' }}
                <span v-if="emp.estResponsable" class="ml-1 px-1.5 py-0.5 text-xs bg-purple-100 text-purple-800 rounded">Chef</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ emp.email || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ emp.telephone || '-' }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="emp.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ emp.actif ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <button @click="editEmploye(emp)" class="text-indigo-600 hover:text-indigo-900" title="Modifier">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                  </svg>
                </button>
              </td>
            </tr>
            <tr v-if="filteredPersonnel.length === 0">
              <td colspan="8" class="px-6 py-12 text-center text-gray-500">Aucun employé trouvé</td>
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
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouvel' }} employé</h3>
          <form @submit.prevent="submitEmploye" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Matricule</label>
                <input v-model="formData.matricule" type="text" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">CIN</label>
                <input v-model="formData.cin" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Nom</label>
                <input v-model="formData.nom" type="text" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Prénom</label>
                <input v-model="formData.prenom" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Département</label>
                <select v-model="formData.departement" class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="dep in departements" :key="dep" :value="dep">{{ dep }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Fonction</label>
                <input v-model="formData.fonction" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input v-model="formData.email" type="email" class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Téléphone</label>
                <input v-model="formData.telephone" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Adresse</label>
              <input v-model="formData.adresse" type="text" class="w-full border-gray-300 rounded-lg">
            </div>
            <div class="flex items-center space-x-4">
              <label class="flex items-center">
                <input type="checkbox" v-model="formData.estResponsable" class="rounded text-indigo-600 mr-2">
                <span class="text-sm text-gray-700">Responsable</span>
              </label>
              <label class="flex items-center">
                <input type="checkbox" v-model="formData.actif" class="rounded text-indigo-600 mr-2">
                <span class="text-sm text-gray-700">Actif</span>
              </label>
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

const personnel = ref([])
const departements = ['Direction', 'Achats', 'Ventes', 'Stock', 'Finance', 'Comptabilité', 'RH', 'IT']
const filterDepartement = ref('')
const searchQuery = ref('')
const showModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, matricule: '', cin: '', nom: '', prenom: '', departement: '', fonction: '', email: '', telephone: '', adresse: '', estResponsable: false, actif: true })

const filteredPersonnel = computed(() => {
  let list = personnel.value
  if (filterDepartement.value) list = list.filter(p => p.departement === filterDepartement.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(p => p.nom?.toLowerCase().includes(q) || p.prenom?.toLowerCase().includes(q) || p.matricule?.toLowerCase().includes(q))
  }
  return list
})

const resetForm = () => {
  formData.id = null; formData.matricule = ''; formData.cin = ''; formData.nom = ''; formData.prenom = ''
  formData.departement = ''; formData.fonction = ''; formData.email = ''; formData.telephone = ''
  formData.adresse = ''; formData.estResponsable = false; formData.actif = true
  editMode.value = false
}

const openCreate = () => { resetForm(); showModal.value = true }

const editEmploye = (emp) => {
  Object.assign(formData, emp)
  editMode.value = true; showModal.value = true
}

const submitEmploye = async () => {
  try {
    if (editMode.value) {
      await referenceApi.updatePersonnel(formData.id, formData)
    } else {
      await referenceApi.createPersonnel(formData)
    }
    showModal.value = false; loadPersonnel()
  } catch (e) { console.error(e) }
}

const loadPersonnel = async () => {
  try { personnel.value = (await referenceApi.getPersonnel()).data || [] }
  catch (e) {
    personnel.value = [
      { id: 1, matricule: 'EMP001', nom: 'Rakoto', prenom: 'Jean', departement: 'Direction', fonction: 'Directeur Général', email: 'rakoto@mad-distrib.mg', telephone: '+261 34 00 000 01', estResponsable: true, actif: true },
      { id: 2, matricule: 'EMP002', nom: 'Rasoa', prenom: 'Marie', departement: 'Finance', fonction: 'Responsable Finance', email: 'rasoa@mad-distrib.mg', telephone: '+261 34 00 000 02', estResponsable: true, actif: true },
      { id: 3, matricule: 'EMP003', nom: 'Rabe', prenom: 'Paul', departement: 'Achats', fonction: 'Acheteur', email: 'rabe@mad-distrib.mg', telephone: '+261 34 00 000 03', estResponsable: false, actif: true },
      { id: 4, matricule: 'EMP004', nom: 'Ravao', prenom: 'Lova', departement: 'Ventes', fonction: 'Commercial', email: 'ravao@mad-distrib.mg', telephone: '+261 34 00 000 04', estResponsable: false, actif: true },
      { id: 5, matricule: 'EMP005', nom: 'Randria', prenom: 'Solo', departement: 'Stock', fonction: 'Magasinier', email: 'randria@mad-distrib.mg', telephone: '+261 34 00 000 05', estResponsable: false, actif: true }
    ]
  }
}

onMounted(() => loadPersonnel())
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
