<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Articles</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion du catalogue articles</p>
      </div>
      <button @click="showCreateModal = true" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvel article
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="flex items-center space-x-4">
        <select v-model="filterCategorie" class="border-gray-300 rounded-lg text-sm">
          <option value="">Toutes les catégories</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.libelle }}</option>
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
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Référence</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Désignation</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Catégorie</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Unité</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="article in filteredArticles" :key="article.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ article.reference }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ article.designation }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ article.categorie?.libelle }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ article.unite?.libelle }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="article.actif ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ article.actif ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="editArticle(article)" class="text-indigo-600 hover:text-indigo-900" title="Modifier">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                    </svg>
                  </button>
                  <button @click="toggleArticleStatus(article)" :class="article.actif ? 'text-red-600 hover:text-red-900' : 'text-green-600 hover:text-green-900'" :title="article.actif ? 'Désactiver' : 'Activer'">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredArticles.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">Aucun article</td>
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
          <h3 class="text-lg font-semibold text-gray-900 mb-4">{{ editMode ? 'Modifier' : 'Nouvel' }} article</h3>
          <form @submit.prevent="submitArticle" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Référence</label>
              <input v-model="formData.reference" type="text" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Désignation</label>
              <input v-model="formData.designation" type="text" required class="w-full border-gray-300 rounded-lg">
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Catégorie</label>
              <select v-model="formData.categorieId" required class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.libelle }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Unité</label>
              <select v-model="formData.uniteId" required class="w-full border-gray-300 rounded-lg">
                <option value="">Sélectionner</option>
                <option v-for="u in unites" :key="u.id" :value="u.id">{{ u.libelle }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea v-model="formData.description" rows="2" class="w-full border-gray-300 rounded-lg"></textarea>
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
import { stockApi, referenceApi } from '@/services/api'

const articlesList = ref([])
const categories = ref([])
const unites = ref([])
const filterCategorie = ref('')
const searchQuery = ref('')
const showCreateModal = ref(false)
const editMode = ref(false)
const formData = reactive({ id: null, reference: '', designation: '', categorieId: '', uniteId: '', description: '' })

const filteredArticles = computed(() => {
  let list = articlesList.value
  if (filterCategorie.value) list = list.filter(a => a.categorie?.id === filterCategorie.value)
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(a => a.reference?.toLowerCase().includes(q) || a.designation?.toLowerCase().includes(q))
  }
  return list
})

const resetForm = () => {
  formData.id = null; formData.reference = ''; formData.designation = ''; formData.categorieId = ''; formData.uniteId = ''; formData.description = ''
  editMode.value = false
}

const editArticle = (article) => {
  formData.id = article.id; formData.reference = article.reference; formData.designation = article.designation
  formData.categorieId = article.categorie?.id; formData.uniteId = article.unite?.id; formData.description = article.description
  editMode.value = true; showCreateModal.value = true
}

const toggleArticleStatus = async (article) => {
  if (confirm(`${article.actif ? 'Désactiver' : 'Activer'} cet article ?`)) {
    try {
      await stockApi.toggleArticleStatus(article.id, !article.actif)
      loadArticles()
    } catch (e) { article.actif = !article.actif }
  }
}

const submitArticle = async () => {
  try {
    if (editMode.value) {
      await stockApi.updateArticle(formData.id, formData)
    } else {
      await stockApi.createArticle(formData)
    }
    showCreateModal.value = false; resetForm(); loadArticles()
  } catch (e) { console.error(e) }
}

const loadArticles = async () => {
  try { articlesList.value = (await stockApi.getArticles()).data || [] }
  catch (e) {
    console.error('Erreur chargement articles:', e)
    articlesList.value = []
  }
}

const loadCategories = async () => {
  try { categories.value = (await referenceApi.getCategories()).data || [] }
  catch (e) { categories.value = [{ id: 1, libelle: 'Fournitures bureau' }, { id: 2, libelle: 'Informatique' }] }
}

const loadUnites = async () => {
  try { unites.value = (await referenceApi.getUnites()).data || [] }
  catch (e) { unites.value = [{ id: 1, libelle: 'Pièce' }, { id: 2, libelle: 'Carton' }, { id: 3, libelle: 'Boîte' }] }
}

onMounted(() => { loadArticles(); loadCategories(); loadUnites() })
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
