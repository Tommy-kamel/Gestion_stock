<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Bons de commande achat</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des bons de commande fournisseurs</p>
      </div>
      <button @click="openCreateModal" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau BC
      </button>
    </div>

    <!-- Tableau des BC -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° BC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="bc in bonsCommande" :key="bc.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ bc.numeroBc }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(bc.dateCommande) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ bc.proformaFournisseur?.fournisseur?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">{{ formatMontant(bc.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(bc.status?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ bc.status?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(bc)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  </button>
                  <button v-if="bc.status?.code === 'BC_CREE'" @click="signerBc(bc)" class="text-green-600 hover:text-green-900" title="Signer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                  </button>
                  <!-- <button @click="imprimerBc(bc)" class="text-gray-600 hover:text-gray-900" title="Imprimer">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
                    </svg>
                  </button> -->
                </div>
              </td>
            </tr>
            <tr v-if="bonsCommande.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                Aucun bon de commande
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Bon de commande {{ selectedBc?.numeroBc }}</h3>
          
          <div class="grid grid-cols-2 gap-4 mb-6">
            <div><p class="text-sm text-gray-500">Date commande</p><p class="font-medium">{{ formatDate(selectedBc?.dateCommande) }}</p></div>
            <div><p class="text-sm text-gray-500">Fournisseur</p><p class="font-medium">{{ selectedBc?.fournisseur?.nom }}</p></div>
            <div><p class="text-sm text-gray-500">Montant TTC</p><p class="font-medium">{{ formatMontant(selectedBc?.montantTtc) }} MGA</p></div>
          </div>

          <table class="min-w-full divide-y divide-gray-200 mb-4">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Article</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Quantité</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Prix unitaire</th>
                <th class="px-4 py-2 text-left text-xs font-medium text-gray-500">Total</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr v-for="detail in selectedBc?.details" :key="detail.id">
                <td class="px-4 py-2 text-sm">{{ detail.article?.designation }}</td>
                <td class="px-4 py-2 text-sm">{{ detail.quantite }}</td>
                <td class="px-4 py-2 text-sm">{{ formatMontant(detail.prixUnitaire) }}</td>
                <td class="px-4 py-2 text-sm font-medium">{{ formatMontant(detail.quantite * detail.prixUnitaire) }}</td>
              </tr>
            </tbody>
          </table>

          <div class="flex justify-end">
            <button @click="showDetailsModal = false" class="btn-secondary mr-2">Fermer</button>
            <button v-if="selectedBc?.status?.code !== 'VALIDE'" @click="livrerEtPayer" class="btn-primary">
              Livrer et Payer
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Création BC -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-3xl w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Créer un bon de commande</h3>
          
          <form @submit.prevent="creerBc" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Proforma fournisseur *</label>
              <select v-model="newBc.proformaFournisseurId" required class="w-full px-4 py-2 border rounded-lg">
                <option value="">-- Sélectionner un proforma --</option>
                <option v-for="proforma in proformasValides" :key="proforma.id" :value="proforma.id">
                  {{ proforma.numeroProforma }} - {{ proforma.fournisseur?.nom }} - {{ formatMontant(proforma.montantTtc) }} Ar
                </option>
              </select>
            </div>

            <div class="flex justify-end space-x-3">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Créer</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal Livrer et Payer -->
    <div v-if="showLivrerModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showLivrerModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-md w-full p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Livrer et payer le BC</h3>
          
          <form @submit.prevent="confirmLivrerEtPayer" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Caisse *</label>
              <select v-model="livraison.caisseId" required class="w-full px-4 py-2 border rounded-lg">
                <option value="">-- Sélectionner une caisse --</option>
                <option v-for="caisse in caisses" :key="caisse.id" :value="caisse.id">
                  {{ caisse.libelle }} ({{ formatMontant(caisse.soldeActuel) }} Ar)
                </option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Numéro facture fournisseur *</label>
              <input v-model="livraison.numeroFactureFournisseur" required type="text" 
                     class="w-full px-4 py-2 border rounded-lg" placeholder="FAC-2024-001" />
            </div>

            <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
              <p class="text-sm text-yellow-800">
                Cette action va créer la facture, mettre à jour le stock, enregistrer le paiement et déduire 
                <strong>{{ formatMontant(selectedBc?.montantTtc) }} Ar</strong> de la caisse.
              </p>
            </div>

            <div class="flex justify-end space-x-3">
              <button type="button" @click="showLivrerModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Confirmer</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { achatApi, referenceApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const bonsCommande = ref([])
const proformasValides = ref([])
const caisses = ref([])
const selectedBc = ref(null)
const showDetailsModal = ref(false)
const showCreateModal = ref(false)
const showLivrerModal = ref(false)

const newBc = ref({
  proformaFournisseurId: ''
})

const livraison = ref({
  caisseId: '',
  numeroFactureFournisseur: ''
})

const formatDate = (date) => date ? new Date(date).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const getStatutClass = (code) => {
  const classes = {
    'BROUILLON': 'bg-yellow-100 text-yellow-800',
    'VALIDE': 'bg-green-100 text-green-800',
    'EN_COURS': 'bg-blue-100 text-blue-800'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const viewDetails = async (bc) => { 
  try {
    const response = await achatApi.getBonCommande(bc.id)
    selectedBc.value = response.data
    showDetailsModal.value = true
  } catch (e) {
    console.error(e)
    alert('Erreur lors du chargement des détails')
  }
}

const openCreateModal = async () => {
  try {
    const response = await achatApi.getAllProformas()
    proformasValides.value = response.data.filter(p => p.status?.code === 'VALIDE')
    showCreateModal.value = true
  } catch (e) {
    console.error(e)
    alert('Erreur lors du chargement des proformas')
  }
}

const creerBc = async () => {
  try {
    await achatApi.creerBonCommande({ proformaFournisseurId: parseInt(newBc.value.proformaFournisseurId) })
    alert('Bon de commande créé avec succès')
    showCreateModal.value = false
    newBc.value = { proformaFournisseurId: '' }
    loadBonsCommande()
  } catch (e) {
    console.error(e)
    alert('Erreur lors de la création du BC')
  }
}

const livrerEtPayer = async () => {
  try {
    const response = await referenceApi.getEntreprises()
    // Charger les caisses (simplification - normalement on filtre par entreprise)
    caisses.value = [
      { id: 1, libelle: 'Caisse Entreprise A', soldeActuel: 10000000 },
      { id: 2, libelle: 'Caisse Entreprise B', soldeActuel: 5000000 }
    ]
    showLivrerModal.value = true
  } catch (e) {
    console.error(e)
  }
}

const confirmLivrerEtPayer = async () => {
  if (confirm('Confirmer la livraison et le paiement ? Cette action est irréversible.')) {
    try {
      await achatApi.livrerEtPayerBonCommande(selectedBc.value.id, {
        caisseId: parseInt(livraison.value.caisseId),
        numeroFactureFournisseur: livraison.value.numeroFactureFournisseur
      })
      alert('Livraison et paiement effectués avec succès')
      showLivrerModal.value = false
      showDetailsModal.value = false
      livraison.value = { caisseId: '', numeroFactureFournisseur: '' }
      loadBonsCommande()
    } catch (e) {
      console.error(e)
      alert('Erreur: ' + (e.response?.data || e.message))
    }
  }
}

const loadBonsCommande = async () => {
  try {
    const response = await achatApi.getBonsCommande()
    bonsCommande.value = response.data || []
  } catch (e) {
    console.error(e)
    bonsCommande.value = []
  }
}

onMounted(loadBonsCommande)
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors; }
.btn-secondary { @apply inline-flex items-center px-4 py-2 bg-gray-100 text-gray-700 font-medium rounded-lg hover:bg-gray-200 transition-colors; }
</style>
