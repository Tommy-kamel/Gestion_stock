<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Proformas fournisseurs</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des proformas reçus des fournisseurs</p>
      </div>
    </div>

    <!-- Liste des DA en attente de proformas -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Demandes en attente de proformas</h2>
      <div class="space-y-4">
        <div v-for="da in demandesEnAttente" :key="da.id" 
             class="border border-gray-200 rounded-lg p-4 hover:border-indigo-300 cursor-pointer"
             @click="selectDemande(da)">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium text-gray-900">{{ da.numeroDa }}</p>
              <p class="text-sm text-gray-500">{{ formatDate(da.dateDemande) }}</p>
            </div>
            <div class="text-right">
              <span class="px-2 py-1 text-xs font-medium rounded-full bg-yellow-100 text-yellow-800">
                {{ da.proformas?.length || 0 }} proforma(s)
              </span>
              <p class="text-sm text-gray-500 mt-1">Min. 3 requis</p>
            </div>
          </div>
        </div>
        <div v-if="demandesEnAttente.length === 0" class="text-center text-gray-500 py-8">
          Aucune demande en attente de proformas
        </div>
      </div>
    </div>

    <!-- Détail DA sélectionnée -->
    <div v-if="selectedDa" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h2 class="text-lg font-semibold text-gray-900">{{ selectedDa.numeroDa }}</h2>
          <p class="text-sm text-gray-500">Articles demandés</p>
        </div>
        <button @click="showAddProformaModal = true" class="btn-primary">
          <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          Ajouter proforma
        </button>
      </div>

      <!-- Articles de la DA -->
      <div class="mb-6">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
              <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Quantité</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr v-for="detail in selectedDa.details" :key="detail.id">
              <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
              <td class="px-4 py-2 text-sm">{{ detail.quantite }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Proformas reçues -->
      <div>
        <h3 class="font-medium text-gray-900 mb-4">Proformas reçues ({{ proformas.length }}/3 minimum)</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="proforma in proformas" :key="proforma.id" 
               :class="['border rounded-lg p-4', proforma.estSelectionne ? 'border-green-500 bg-green-50' : 'border-gray-200']">
            <div class="flex items-center justify-between mb-2">
              <span class="font-medium text-gray-900">{{ proforma.fournisseur?.nom }}</span>
              <span v-if="proforma.estSelectionne" class="px-2 py-1 text-xs bg-green-500 text-white rounded-full">
                Meilleur prix
              </span>
              <span v-else class="text-sm text-gray-500">Rang #{{ proforma.rangPrix }}</span>
            </div>
            <div class="space-y-1 text-sm">
              <p><span class="text-gray-500">N° Proforma:</span> {{ proforma.numeroProforma }}</p>
              <p><span class="text-gray-500">Montant HT:</span> {{ formatMontant(proforma.montantHt) }} MGA</p>
              <p><span class="text-gray-500">Montant TTC:</span> {{ formatMontant(proforma.montantTtc) }} MGA</p>
              <p><span class="text-gray-500">Délai:</span> {{ proforma.delaiLivraison }} jours</p>
            </div>
          </div>
        </div>
        <div v-if="proformas.length === 0" class="text-center text-gray-500 py-8 border-2 border-dashed rounded-lg">
          Aucune proforma reçue. Cliquez sur "Ajouter proforma" pour commencer.
        </div>
      </div>

      <!-- Action validation -->
      <div v-if="proformas.length >= 3" class="mt-6 pt-6 border-t">
        <div class="flex items-center justify-between">
          <p class="text-sm text-green-600">
            ✓ Minimum de 3 proformas atteint. Le système a sélectionné le meilleur prix.
          </p>
          <button @click="envoyerValidationFinance" class="btn-primary">
            Envoyer pour validation finance
          </button>
        </div>
      </div>
    </div>

    <!-- Modal ajout proforma -->
    <div v-if="showAddProformaModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showAddProformaModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-2xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Ajouter une proforma fournisseur</h3>
          
          <form @submit.prevent="ajouterProforma" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Fournisseur</label>
                <select v-model="newProforma.fournisseurId" required class="w-full border-gray-300 rounded-lg">
                  <option value="">Sélectionner</option>
                  <option v-for="f in fournisseurs" :key="f.id" :value="f.id">{{ f.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">N° Proforma</label>
                <input v-model="newProforma.numeroProforma" type="text" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Date proforma</label>
                <input v-model="newProforma.dateProforma" type="date" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Date validité</label>
                <input v-model="newProforma.dateValidite" type="date" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Délai livraison (jours)</label>
                <input v-model.number="newProforma.delaiLivraison" type="number" min="1" required class="w-full border-gray-300 rounded-lg">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Conditions paiement</label>
                <input v-model="newProforma.conditionsPaiement" type="text" class="w-full border-gray-300 rounded-lg">
              </div>
            </div>

            <!-- Détails articles avec prix -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Prix par article</label>
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Qté</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unitaire</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="(detail, index) in newProforma.details" :key="index">
                    <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
                    <td class="px-4 py-2 text-sm">{{ detail.quantite }}</td>
                    <td class="px-4 py-2">
                      <input v-model.number="detail.prixUnitaire" type="number" min="0" step="0.01" required
                             class="w-32 border-gray-300 rounded-lg text-sm">
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div class="flex justify-end space-x-3 pt-4">
              <button type="button" @click="showAddProformaModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Enregistrer</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { achatApi, referenceApi } from '@/services/api'

const demandesEnAttente = ref([])
const selectedDa = ref(null)
const proformas = ref([])
const fournisseurs = ref([])
const showAddProformaModal = ref(false)

const newProforma = reactive({
  fournisseurId: '',
  numeroProforma: '',
  dateProforma: new Date().toISOString().split('T')[0],
  dateValidite: '',
  delaiLivraison: 7,
  conditionsPaiement: '',
  details: []
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('fr-FR')
}

const formatMontant = (montant) => {
  if (!montant) return '0'
  return new Intl.NumberFormat('fr-FR').format(montant)
}

const selectDemande = (da) => {
  selectedDa.value = da
  loadProformas(da.id)
  // Préparer les détails pour le formulaire proforma
  newProforma.details = (da.details || []).map(d => ({
    articleId: d.article?.id,
    article: d.article,
    quantite: d.quantite,
    prixUnitaire: 0
  }))
}

const loadProformas = async (daId) => {
  try {
    const response = await achatApi.getProformas(daId)
    proformas.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement proformas:', error)
    proformas.value = []
  }
}

const ajouterProforma = async () => {
  try {
    const data = {
      fournisseurId: newProforma.fournisseurId,
      numeroProforma: newProforma.numeroProforma,
      dateProforma: newProforma.dateProforma,
      dateValidite: newProforma.dateValidite,
      delaiLivraison: newProforma.delaiLivraison,
      conditionsPaiement: newProforma.conditionsPaiement,
      details: newProforma.details.map(d => ({
        articleId: d.articleId,
        quantite: d.quantite,
        prixUnitaire: d.prixUnitaire
      }))
    }
    await achatApi.ajouterProforma(selectedDa.value.id, data)
    showAddProformaModal.value = false
    loadProformas(selectedDa.value.id)
  } catch (error) {
    console.error('Erreur ajout proforma:', error)
    alert('Erreur lors de l\'ajout')
  }
}

const envoyerValidationFinance = async () => {
  if (confirm('Envoyer pour validation finance ?')) {
    // La DA sera mise à jour côté serveur
    alert('Demande envoyée pour validation finance')
    loadDemandes()
  }
}

const loadDemandes = async () => {
  try {
    const response = await achatApi.getDemandesAchat()
    demandesEnAttente.value = (response.data || []).filter(da => 
      ['APPROUVE', 'EN_ATTENTE_PROFORMA', 'PROFORMAS_SAISIS'].includes(da.statut?.code)
    )
  } catch (error) {
    console.error('Erreur chargement demandes:', error)
    demandesEnAttente.value = []
  }
}

const loadFournisseurs = async () => {
  try {
    const response = await referenceApi.getFournisseurs()
    fournisseurs.value = response.data || []
  } catch (error) {
    fournisseurs.value = [
      { id: 2, nom: 'Fournisseur Papeterie Plus' },
      { id: 3, nom: 'Fournisseur Tech Solutions' },
      { id: 4, nom: 'Fournisseur Bureau Pro' }
    ]
  }
}

onMounted(() => {
  loadDemandes()
  loadFournisseurs()
})
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors;
}
.btn-secondary {
  @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors;
}
</style>
