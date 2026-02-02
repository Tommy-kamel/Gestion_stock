<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Fournisseurs</h1>
        <p class="mt-1 text-sm text-gray-500">Liste des entreprises fournisseurs</p>
      </div>
      <button @click="openCreate" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau fournisseur
      </button>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Total fournisseurs</p>
        <p class="text-2xl font-bold text-gray-900">{{ fournisseurs.length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Actifs</p>
        <p class="text-2xl font-bold text-green-600">{{ fournisseurs.filter(f => f.actif).length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
        <p class="text-sm text-gray-500">Commandes en cours</p>
        <p class="text-2xl font-bold text-indigo-600">{{ commandesEnCours }}</p>
      </div>
    </div>

    <!-- Recherche -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <input v-model="searchQuery" type="text" placeholder="Rechercher un fournisseur..." class="w-full border-gray-300 rounded-lg">
    </div>

    <!-- Cartes fournisseurs -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="fournisseur in filteredFournisseurs" :key="fournisseur.id" class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
        <div class="p-6">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="text-lg font-semibold text-gray-900">{{ fournisseur.nom }}</h3>
              <p class="text-sm text-gray-500">{{ fournisseur.adresse }}</p>
            </div>
            <span :class="fournisseur.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
              {{ fournisseur.actif ? 'Actif' : 'Inactif' }}
            </span>
          </div>
          
          <div class="space-y-2 text-sm">
            <div class="flex items-center text-gray-500">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/></svg>
              {{ fournisseur.telephone || '-' }}
            </div>
            <div class="flex items-center text-gray-500">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
              {{ fournisseur.email || '-' }}
            </div>
            <div class="flex items-center text-gray-500">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
              {{ fournisseur.contact || '-' }}
            </div>
          </div>

          <div v-if="fournisseur.categories?.length" class="mt-4 flex flex-wrap gap-1">
            <span v-for="cat in fournisseur.categories" :key="cat" class="px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
              {{ cat }}
            </span>
          </div>
        </div>
        
        <div class="px-6 py-3 bg-gray-50 border-t border-gray-100 flex justify-between">
          <button @click="viewHistorique(fournisseur)" class="text-indigo-600 hover:text-indigo-900 text-sm font-medium">
            Historique
          </button>
          <button @click="editFournisseur(fournisseur)" class="text-gray-600 hover:text-gray-900 text-sm font-medium">
            Modifier
          </button>
        </div>
      </div>

      <div v-if="filteredFournisseurs.length === 0" class="col-span-full text-center py-12 text-gray-500">
        Aucun fournisseur trouvé
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-lg w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouveau' }} fournisseur</h3>
          <form @submit.prevent="submitFournisseur" class="space-y-4">
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

const fournisseurs = ref([])
const searchQuery = ref('')
const commandesEnCours = ref(8)
const showModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, nom: '', nif: '', stat: '', adresse: '', telephone: '', email: '', contact: '', actif: true })

const filteredFournisseurs = computed(() => {
  if (!searchQuery.value) return fournisseurs.value
  const q = searchQuery.value.toLowerCase()
  return fournisseurs.value.filter(f => f.nom?.toLowerCase().includes(q) || f.contact?.toLowerCase().includes(q))
})

const resetForm = () => {
  formData.id = null; formData.nom = ''; formData.nif = ''; formData.stat = ''; formData.adresse = ''
  formData.telephone = ''; formData.email = ''; formData.contact = ''; formData.actif = true
  editMode.value = false
}

const openCreate = () => { resetForm(); showModal.value = true }

const editFournisseur = (f) => {
  Object.assign(formData, f)
  editMode.value = true; showModal.value = true
}

const viewHistorique = (f) => {
  alert(`Historique commandes pour ${f.nom}`)
}

const submitFournisseur = async () => {
  try {
    const data = { ...formData, estFournisseur: true }
    if (editMode.value) {
      await referenceApi.updateEntreprise(formData.id, data)
    } else {
      await referenceApi.createEntreprise(data)
    }
    showModal.value = false; loadFournisseurs()
  } catch (e) { console.error(e) }
}

const loadFournisseurs = async () => {
  try { fournisseurs.value = (await referenceApi.getFournisseurs()).data || [] }
  catch (e) {
    fournisseurs.value = [
      { id: 1, nom: 'Papeterie Centrale', nif: '12345678', adresse: 'Antsirabe', telephone: '+261 20 44 000 00', email: 'contact@papeterie.mg', contact: 'M. Rakoto', actif: true, categories: ['Fournitures', 'Papeterie'] },
      { id: 2, nom: 'Informatique Plus', nif: '98765432', adresse: 'Antananarivo', telephone: '+261 20 22 111 11', email: 'info@infoplus.mg', contact: 'Mme Rasoa', actif: true, categories: ['Informatique', 'Bureautique'] },
      { id: 3, nom: 'Import Export SARL', nif: '45678912', adresse: 'Toamasina', telephone: '+261 20 53 000 00', email: 'import@export.mg', contact: 'M. Randria', actif: false }
    ]
  }
}

onMounted(() => loadFournisseurs())
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
