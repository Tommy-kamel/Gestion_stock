<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Proformas fournisseurs</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des proformas reçus des fournisseurs</p>
      </div>
      <button @click="openModal" class="btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouveau proforma
      </button>
    </div>

    <!-- Liste des proformas -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Tous les proformas</h2>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Proforma</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">DA</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date émission</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date validité</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="proforma in proformas" :key="proforma.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ proforma.numeroProforma }}</td>
              <td class="px-6 py-4 text-sm text-gray-700">{{ proforma.fournisseur?.nom }}</td>
              <td class="px-6 py-4 text-sm text-gray-700">{{ proforma.demandeAchat?.numeroDa }}</td>
              <td class="px-6 py-4 text-sm font-semibold text-gray-900">{{ formatCurrency(proforma.montantTtc) }}</td>
              <td class="px-6 py-4 text-sm text-gray-500">{{ formatDate(proforma.dateEmission) }}</td>
              <td class="px-6 py-4 text-sm text-gray-500">{{ formatDate(proforma.dateValidite) }}</td>
              <td class="px-6 py-4">
                <span v-if="proforma.status?.code === 'VALIDE'" class="px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">
                  {{ proforma.status?.libelle }}
                </span>
                <span v-else-if="proforma.status?.code === 'ANNULE'" class="px-2 py-1 text-xs font-medium rounded-full bg-red-100 text-red-800">
                  {{ proforma.status?.libelle }}
                </span>
                <span v-else class="px-2 py-1 text-xs font-medium rounded-full bg-yellow-100 text-yellow-800">
                  {{ proforma.status?.libelle || 'Brouillon' }}
                </span>
              </td>
            </tr>
            <tr v-if="proformas.length === 0">
              <td colspan="7" class="px-6 py-12 text-center text-gray-500">
                Aucun proforma
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal Nouveau Proforma -->
    <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div class="sticky top-0 bg-white border-b px-6 py-4 flex items-center justify-between">
          <h3 class="text-xl font-semibold text-gray-900">Nouveau proforma fournisseur</h3>
          <button @click="closeModal" class="text-gray-400 hover:text-gray-600">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form @submit.prevent="submitProforma" class="p-6 space-y-6">
          <!-- Informations générales -->
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Demande d'achat *</label>
              <select v-model="form.demandeAchatId" required 
                      class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                      @change="loadDemandeDetails">
                <option value="">Sélectionner une DA</option>
                <option v-for="da in demandesAchats" :key="da.id" :value="da.id">
                  {{ da.numeroDa }} - {{ formatDate(da.dateDemande) }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Fournisseur *</label>
              <select v-model="form.fournisseurId" required 
                      class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500">
                <option value="">Sélectionner un fournisseur</option>
                <option v-for="f in fournisseurs" :key="f.id" :value="f.id">
                  {{ f.nom }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">N° Proforma *</label>
              <input v-model="form.numeroProforma" type="text" required
                     class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                     placeholder="PF2024-001" />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Date d'émission *</label>
              <input v-model="form.dateEmission" type="date" required
                     class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500" />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Durée de validité (jours) *</label>
              <input v-model.number="form.dureeValidite" type="number" required min="1"
                     class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                     placeholder="30" />
            </div>
          </div>

          <!-- Articles et prix -->
          <div v-if="selectedDemande" class="border-t pt-6">
            <h4 class="text-lg font-semibold text-gray-900 mb-4">Articles et prix unitaires</h4>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Article</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Quantité</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Prix unitaire *</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="(detail, index) in form.details" :key="index">
                    <td class="px-4 py-3 text-sm">{{ detail.articleDesignation }}</td>
                    <td class="px-4 py-3 text-sm">{{ detail.quantite }}</td>
                    <td class="px-4 py-3">
                      <input v-model.number="detail.prixUnitaire" type="number" step="0.01" required min="0"
                             @input="calculateLineTotal(index)"
                             class="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
                             placeholder="0.00" />
                    </td>
                    <td class="px-4 py-3 text-sm font-semibold">
                      {{ formatCurrency(detail.prixUnitaire * detail.quantite) }}
                    </td>
                  </tr>
                </tbody>
                <tfoot class="bg-gray-50">
                  <tr>
                    <td colspan="3" class="px-4 py-3 text-right font-semibold text-gray-900">Total:</td>
                    <td class="px-4 py-3 text-lg font-bold text-indigo-600">{{ formatCurrency(totalMontant) }}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center justify-end gap-3 pt-4 border-t">
            <button type="button" @click="closeModal" 
                    class="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50">
              Annuler
            </button>
            <button type="submit" 
                    class="px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 font-medium">
              Créer le proforma
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { achatApi, referenceApi } from '@/services/api';

const proformas = ref([]);
const demandesAchats = ref([]);
const fournisseurs = ref([]);
const showModal = ref(false);
const selectedDemande = ref(null);

const form = ref({
  demandeAchatId: '',
  fournisseurId: '',
  numeroProforma: '',
  dateEmission: new Date().toISOString().split('T')[0],
  dureeValidite: 30,
  details: []
});

const totalMontant = computed(() => {
  return form.value.details.reduce((sum, detail) => {
    return sum + (detail.prixUnitaire * detail.quantite || 0);
  }, 0);
});

onMounted(async () => {
  await loadData();
});

async function loadData() {
  try {
    const [proformasRes, demandesRes, fournisseursRes] = await Promise.all([
      achatApi.getAllProformas(),
      achatApi.getDemandesAchat(),
      referenceApi.getFournisseurs()
    ]);
    
    // Afficher tous les proformas (pas de filtre)
    proformas.value = proformasRes.data;
    
    // Filtrer les DAs avec statut VALIDE (validées, en attente de proformas)
    demandesAchats.value = demandesRes.data.filter(da => da.status?.code === 'VALIDE');
    fournisseurs.value = fournisseursRes.data;
  } catch (error) {
    console.error('Erreur lors du chargement des données:', error);
    alert('Erreur lors du chargement des données');
  }
}

function openModal() {
  showModal.value = true;
  form.value = {
    demandeAchatId: '',
    fournisseurId: '',
    numeroProforma: '',
    dateEmission: new Date().toISOString().split('T')[0],
    dureeValidite: 5,
    details: []
  };
  selectedDemande.value = null;
}

function closeModal() {
  showModal.value = false;
  selectedDemande.value = null;
}

async function loadDemandeDetails() {
  if (!form.value.demandeAchatId) {
    selectedDemande.value = null;
    form.value.details = [];
    return;
  }

  try {
    const response = await achatApi.getDemandeAchat(form.value.demandeAchatId);
    selectedDemande.value = response.data;
    
    // Initialiser les détails du proforma avec les articles de la DA
    form.value.details = selectedDemande.value.details.map(detail => ({
      articleId: detail.article.id,
      articleDesignation: detail.article.designation,
      quantite: detail.quantiteDemandee || detail.quantite || 0,
      prixUnitaire: 0
    }));
  } catch (error) {
    console.error('Erreur lors du chargement de la demande:', error);
    alert('Erreur lors du chargement des détails de la demande');
  }
}

function calculateLineTotal(index) {
  // Trigger reactivity for computed total
  form.value.details[index] = { ...form.value.details[index] };
}

async function submitProforma() {
  try {
    // Validation
    if (!form.value.demandeAchatId || !form.value.fournisseurId) {
      alert('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const hasInvalidPrices = form.value.details.some(d => !d.prixUnitaire || d.prixUnitaire <= 0);
    if (hasInvalidPrices) {
      alert('Veuillez saisir tous les prix unitaires');
      return;
    }

    // Préparer les données pour l'API
    const payload = {
      demandeAchatId: parseInt(form.value.demandeAchatId),
      fournisseurId: parseInt(form.value.fournisseurId),
      numeroProforma: form.value.numeroProforma,
      dateEmission: form.value.dateEmission,
      dureeValidite: form.value.dureeValidite,
      details: form.value.details.map(d => ({
        articleId: d.articleId,
        quantite: parseFloat(d.quantite) || 0,
        prixUnitaire: parseFloat(d.prixUnitaire) || 0
      }))
    };

    await achatApi.creerProforma(payload);
    alert('Proforma créé avec succès');
    closeModal();
    await loadData();
  } catch (error) {
    console.error('Erreur lors de la création du proforma:', error);
    alert('Erreur lors de la création du proforma: ' + (error.response?.data?.message || error.message));
  }
}

function formatDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('fr-FR');
}

function formatCurrency(amount) {
  if (!amount && amount !== 0) return '0 Ar';
  return new Intl.NumberFormat('fr-FR', {
    style: 'decimal',
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(amount) + ' Ar';
}
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 transition-colors;
}
</style>
