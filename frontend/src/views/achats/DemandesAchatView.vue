<template>
  <div class="space-y-6">
    <!-- En-tête -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Demandes d'achat</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des demandes d'achat internes</p>
      </div>
      <button @click="showCreateModal = true" class="mt-4 sm:mt-0 btn-primary">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Nouvelle demande
      </button>
    </div>

    <!-- Filtres -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Entreprise</label>
          <select v-model="filters.entreprise" @change="onFilterEntrepriseChange" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            <option value="">Toutes</option>
            <option v-for="entreprise in entreprises" :key="entreprise.id" :value="entreprise.id">{{ entreprise.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Site</label>
          <select v-model="filters.site" @change="onFilterSiteChange" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            <option value="">Tous</option>
            <option v-for="site in filteredSitesForFilters" :key="site.id" :value="site.id">{{ site.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Dépôt</label>
          <select v-model="filters.depot" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            <option value="">Tous</option>
            <option v-for="depot in filteredDepotsForFilters" :key="depot.id" :value="depot.id">{{ depot.nom }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Statut</label>
          <select v-model="filters.statut" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            <option value="">Tous</option>
            <option value="BROUILLON">Brouillon</option>
            <option value="SOUMIS">Soumis</option>
            <option value="APPROUVE">Approuvé</option>
            <option value="VALIDE">Validé</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Date</label>
          <input v-model="filters.dateDebut" type="date" class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
        </div>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">N° DA</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Dépôt cible</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="da in filteredDemandes" :key="da.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="font-medium text-gray-900">{{ da.numeroDa }}</span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(da.dateDemande) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ da.depot?.nom }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatutClass(da.status?.code)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ da.status?.libelle }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm">
                <div class="flex items-center space-x-2">
                  <button @click="viewDetails(da)" class="text-indigo-600 hover:text-indigo-900" title="Voir">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  </button>
                  <button v-if="da.status?.code !== 'VALIDE' && da.status?.code !== 'CLOTURE'" @click="valider(da)" 
                          class="text-green-600 hover:text-green-900" title="Valider">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </button>
                  <button v-if="da.status?.code === 'SOUMIS'" @click="refuser(da)" 
                          class="text-red-600 hover:text-red-900" title="Refuser">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </button>
                  <button v-if="da.status?.code === 'VALIDE'" @click="viewProformas(da)" 
                          class="text-purple-600 hover:text-purple-900" title="Voir les proformas">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </button>
                  <button v-if="da.status?.code === 'VALIDE'" @click="selectionnerMeilleurProforma(da)" 
                          class="text-blue-600 hover:text-blue-900" title="Sélectionner meilleur proforma">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="filteredDemandes.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                Aucune demande d'achat trouvée
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal création -->
    <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showCreateModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-6">Nouvelle demande d'achat</h3>
          
          <form @submit.prevent="creerDemande" class="space-y-6">
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Entreprise *</label>
                <select v-model="newDemande.entrepriseId" @change="onFormEntrepriseChange" required class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                  <option value="">Sélectionner une entreprise</option>
                  <option v-for="entreprise in entreprises" :key="entreprise.id" :value="entreprise.id">{{ entreprise.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Site *</label>
                <select v-model="newDemande.siteId" @change="onFormSiteChange" required class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                  <option value="">Sélectionner un site</option>
                  <option v-for="site in filteredSitesForForm" :key="site.id" :value="site.id">{{ site.nom }}</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Dépôt cible *</label>
                <select v-model="newDemande.depotCibleId" required class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                  <option value="">Sélectionner un dépôt</option>
                  <option v-for="depot in filteredDepotsForForm" :key="depot.id" :value="depot.id">{{ depot.nom }}</option>
                </select>
              </div>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Date demande *</label>
              <input v-model="newDemande.dateDemande" type="date" required class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Observations</label>
              <textarea v-model="newDemande.observations" rows="3" placeholder="Motif de l'achat, observations..." class="w-full px-4 py-3 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors"></textarea>
            </div>

            <!-- Articles -->
            <div>
              <div class="flex items-center justify-between mb-3">
                <label class="block text-sm font-medium text-gray-700">Articles demandés *</label>
                <button type="button" @click="addArticleLine" class="px-3 py-2 text-sm text-white bg-indigo-600 rounded-lg hover:bg-indigo-700">
                  + Ajouter une ligne
                </button>
              </div>
              <div class="overflow-x-auto border border-gray-200 rounded-lg">
                <table class="min-w-full">
                  <thead class="bg-gray-50">
                    <tr>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Article</th>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider w-32">Quantité</th>
                      <th class="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider w-40">Prix unitaire</th>
                      <th class="px-4 py-3 w-16"></th>
                    </tr>
                  </thead>
                  <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(line, index) in newDemande.details" :key="index">
                      <td class="px-4 py-3">
                        <select v-model="line.articleId" required class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                          <option value="">Sélectionner un article</option>
                          <option v-for="art in articles" :key="art.id" :value="art.id">{{ art.reference }} - {{ art.designation }}</option>
                        </select>
                      </td>
                      <td class="px-4 py-3">
                        <input v-model.number="line.quantite" type="number" min="1" step="1" required placeholder="Qté" 
                               class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                      </td>
                      <td class="px-4 py-3">
                        <input v-model.number="line.prixUnitaire" type="number" min="0" step="0.01" required placeholder="Prix unitaire" 
                               class="w-full px-3 py-2 border border-gray-200 rounded-lg hover:border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                      </td>
                      <td class="px-4 py-3 text-center">
                        <button type="button" @click="removeArticleLine(index)" class="text-red-500 hover:text-red-700">
                          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div class="flex justify-end space-x-3 pt-6 border-t">
              <button type="button" @click="showCreateModal = false" class="btn-secondary">Annuler</button>
              <button type="submit" class="btn-primary">Créer la demande</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal détails -->
    <div v-if="showDetailsModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showDetailsModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-4xl w-full p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-6">
            Demande {{ selectedDemande?.numeroDa }}
          </h3>
          
          <div class="grid grid-cols-3 gap-6 mb-8">
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">Date demande</p>
              <p class="font-semibold text-gray-900">{{ formatDate(selectedDemande?.dateDemande) }}</p>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">Statut</p>
              <span :class="getStatutClass(selectedDemande?.status?.code)" class="inline-block px-3 py-1 text-sm font-semibold rounded-full">
                {{ selectedDemande?.status?.libelle }}
              </span>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500 mb-1">N° DA</p>
              <p class="font-semibold text-gray-900">{{ selectedDemande?.numeroDa }}</p>
            </div>
          </div>

          <div class="grid grid-cols-3 gap-6 mb-8">
            <div class="bg-blue-50 p-4 rounded-lg">
              <p class="text-sm text-blue-700 mb-1 font-medium">Entreprise</p>
              <p class="font-semibold text-gray-900">{{ selectedDemande?.entreprise?.nom || 'N/A' }}</p>
            </div>
            <div class="bg-blue-50 p-4 rounded-lg">
              <p class="text-sm text-blue-700 mb-1 font-medium">Site</p>
              <p class="font-semibold text-gray-900">{{ selectedDemande?.site?.nom || 'N/A' }}</p>
            </div>
            <div class="bg-blue-50 p-4 rounded-lg">
              <p class="text-sm text-blue-700 mb-1 font-medium">Dépôt cible</p>
              <p class="font-semibold text-gray-900">{{ selectedDemande?.depot?.nom || 'N/A' }}</p>
            </div>
          </div>

          <div v-if="selectedDemande?.motifAchat" class="mb-8">
            <h4 class="font-semibold text-gray-900 mb-2">Motif / Observations</h4>
            <p class="text-gray-700 bg-gray-50 p-4 rounded-lg">{{ selectedDemande.motifAchat }}</p>
          </div>

          <div class="mb-6">
            <h4 class="font-semibold text-gray-900 mb-4 text-lg">Articles demandés</h4>
            <div class="overflow-x-auto border border-gray-200 rounded-lg">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Référence</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Désignation</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Quantité</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Prix unitaire</th>
                    <th class="px-6 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Total</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="detail in selectedDemande?.details" :key="detail.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 text-sm font-medium text-gray-900">{{ detail.article?.reference || 'N/A' }}</td>
                    <td class="px-6 py-4 text-sm text-gray-700">{{ detail.article?.designation || 'N/A' }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900 font-semibold">{{ detail.quantiteDemandee }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900">{{ formatCurrency(detail.prixUnitaire) }}</td>
                    <td class="px-6 py-4 text-sm text-gray-900 font-semibold">{{ formatCurrency(detail.quantiteDemandee * detail.prixUnitaire) }}</td>
                  </tr>
                  <tr v-if="!selectedDemande?.details || selectedDemande.details.length === 0">
                    <td colspan="5" class="px-6 py-8 text-center text-gray-500">
                      Aucun article
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div class="flex justify-end space-x-3 pt-6 border-t">
            <button @click="showDetailsModal = false" class="btn-secondary">Fermer</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Proformas -->
    <div v-if="showProformasModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4">
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="showProformasModal = false"></div>
        <div class="relative bg-white rounded-xl shadow-xl max-w-5xl w-full p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-6">
            Proformas pour la DA {{ selectedDemande?.numeroDa }}
          </h3>
          
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Proforma</th>
                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date émission</th>
                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date validité</th>
                  <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="proforma in proformasDemande" :key="proforma.id" class="hover:bg-gray-50">
                  <td class="px-4 py-3 text-sm font-medium text-gray-900">{{ proforma.numeroProforma }}</td>
                  <td class="px-4 py-3 text-sm text-gray-700">{{ proforma.fournisseur?.nom }}</td>
                  <td class="px-4 py-3 text-sm font-semibold text-gray-900">{{ formatCurrency(proforma.montantTtc) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-500">{{ formatDate(proforma.dateEmission) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-500">{{ formatDate(proforma.dateValidite) }}</td>
                  <td class="px-4 py-3">
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
                <tr v-if="proformasDemande.length === 0">
                  <td colspan="6" class="px-6 py-12 text-center text-gray-500">
                    Aucun proforma pour cette demande d'achat
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="flex justify-end space-x-3 pt-6 border-t mt-6">
            <button @click="showProformasModal = false" class="btn-secondary">Fermer</button>
            <button v-if="proformasDemande.length > 0" @click="selectionnerMeilleurProforma(selectedDemande); showProformasModal = false" 
                    class="btn-primary">
              Sélectionner le meilleur
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { achatApi, stockApi, referenceApi } from '@/services/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const demandes = ref([])
const depots = ref([])
const articles = ref([])
const entreprises = ref([])
const sites = ref([])

const showCreateModal = ref(false)
const showDetailsModal = ref(false)
const showProformasModal = ref(false)
const selectedDemande = ref(null)
const proformasDemande = ref([])

const filters = reactive({
  search: '',
  entreprise: '',
  site: '',
  depot: '',
  statut: '',
  dateDebut: '',
  dateFin: ''
})

const newDemande = reactive({
  entrepriseId: '',
  siteId: '',
  depotCibleId: '',
  dateDemande: new Date().toISOString().split('T')[0],
  observations: '',
  details: [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
})

// Computed pour les filtres synchronisés
const filteredSitesForFilters = computed(() => {
  if (!filters.entreprise) return sites.value
  return sites.value.filter(s => s.entreprise?.id === filters.entreprise)
})

const filteredDepotsForFilters = computed(() => {
  if (!filters.site) return depots.value
  return depots.value.filter(d => d.site?.id === filters.site)
})

// Computed pour le formulaire de création
const filteredSitesForForm = computed(() => {
  if (!newDemande.entrepriseId) return sites.value
  return sites.value.filter(s => s.entreprise?.id === newDemande.entrepriseId)
})

const filteredDepotsForForm = computed(() => {
  if (!newDemande.siteId) return depots.value
  return depots.value.filter(d => d.site?.id === newDemande.siteId)
})

const filteredDemandes = computed(() => {
  return demandes.value.filter(da => {
    if (filters.entreprise && da.entreprise?.id !== filters.entreprise) return false
    if (filters.site && da.site?.id !== filters.site) return false
    if (filters.depot && da.depot?.id !== filters.depot) return false
    if (filters.statut && da.status?.code !== filters.statut) return false
    if (filters.dateDebut && da.dateDemande < filters.dateDebut) return false
    return true
  })
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('fr-FR')
}

const formatCurrency = (value) => {
  if (!value && value !== 0) return 'N/A'
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'MGA' }).format(value)
}

const getStatutClass = (code) => {
  const classes = {
    'BROUILLON': 'bg-gray-100 text-gray-800',
    'SOUMIS': 'bg-blue-100 text-blue-800',
    'APPROUVE': 'bg-green-100 text-green-800',
    'EN_ATTENTE_PROFORMA': 'bg-yellow-100 text-yellow-800',
    'EN_ATTENTE_FINANCE': 'bg-purple-100 text-purple-800',
    'VALIDE_FINANCE': 'bg-green-100 text-green-800',
    'REJETE_FINANCE': 'bg-red-100 text-red-800'
  }
  return classes[code] || 'bg-gray-100 text-gray-800'
}

const addArticleLine = () => {
  newDemande.details.push({ articleId: '', quantite: 1, prixUnitaire: 0 })
}

const removeArticleLine = (index) => {
  newDemande.details.splice(index, 1)
}

// Gestion des changements de filtres
const onFilterEntrepriseChange = () => {
  filters.site = ''
  filters.depot = ''
}

const onFilterSiteChange = () => {
  filters.depot = ''
}

// Gestion des changements dans le formulaire
const onFormEntrepriseChange = () => {
  newDemande.siteId = ''
  newDemande.depotCibleId = ''
}

const onFormSiteChange = () => {
  newDemande.depotCibleId = ''
}

const valider = async (da) => {
  if (confirm('Valider cette demande d\'achat ?')) {
    try {
      await achatApi.validerDemandeAchat(da.id)
      loadDemandes()
      alert('Demande validée avec succès')
    } catch (error) {
      console.error('Erreur validation:', error)
      alert('Erreur lors de la validation')
    }
  }
}

const soumettre = async (da) => {
  if (confirm('Soumettre cette demande d\'achat pour validation ?')) {
    try {
      await achatApi.soumettreDemandeAchat(da.id)
      loadDemandes()
      alert('Demande soumise avec succès')
    } catch (error) {
      console.error('Erreur soumission:', error)
      alert('Erreur lors de la soumission')
    }
  }
}

const refuser = async (da) => {
  const motif = prompt('Motif du refus:')
  if (motif) {
    try {
      // Appeler l'API pour rejeter la demande
      await achatApi.rejeterDemandeAchat(da.id, motif)
      loadDemandes()
      alert('Demande refusée')
    } catch (error) {
      console.error('Erreur refus:', error)
      alert('Erreur lors du refus: ' + (error.response?.data?.message || error.message))
    }
  }
}

const viewDetails = async (da) => {
  try {
    // Fetch full details from backend
    const response = await achatApi.getDemandeAchat(da.id)
    selectedDemande.value = response.data
    showDetailsModal.value = true
  } catch (error) {
    console.error('Erreur chargement détails:', error)
    selectedDemande.value = da
    showDetailsModal.value = true
  }
}

const creerDemande = async () => {
  try {
    const data = {
      dateDemande: newDemande.dateDemande,
      entrepriseId: newDemande.entrepriseId,
      siteId: newDemande.siteId,
      depotId: newDemande.depotCibleId,
      motifAchat: newDemande.observations,
      details: newDemande.details.filter(d => d.articleId).map(d => ({
        articleId: d.articleId,
        quantiteDemandee: d.quantite,
        prixUnitaire: d.prixUnitaire
      }))
    }
    await achatApi.creerDemandeAchat(data)
    showCreateModal.value = false
    loadDemandes()
    // Reset form
    newDemande.entrepriseId = ''
    newDemande.siteId = ''
    newDemande.depotCibleId = ''
    newDemande.observations = ''
    newDemande.details = [{ articleId: '', quantite: 1, prixUnitaire: 0 }]
  } catch (error) {
    console.error('Erreur création DA:', error)
    alert('Erreur lors de la création')
  }
}


const approuver = async (da) => {
  if (confirm('Approuver cette demande ?')) {
    try {
      await achatApi.approuverDemandeAchat(da.id, authStore.user?.id || 1)
      loadDemandes()
    } catch (error) {
      console.error('Erreur approbation:', error)
    }
  }
}

const selectionnerMeilleurProforma = async (da) => {
  if (confirm(`Sélectionner automatiquement le meilleur proforma pour la DA ${da.numeroDa} ?\n\nLe système va sélectionner le proforma avec le prix le plus bas et annuler les autres.`)) {
    try {
      await achatApi.selectionnerMeilleurProforma(da.id)
      alert('Le meilleur proforma a été sélectionné avec succès')
      loadDemandes()
    } catch (error) {
      console.error('Erreur sélection proforma:', error)
      alert('Erreur lors de la sélection du proforma: ' + (error.response?.data?.message || error.message))
    }
  }
}

const viewProformas = async (da) => {
  try {
    selectedDemande.value = da
    const response = await achatApi.getProformas(da.id)
    proformasDemande.value = response.data
    showProformasModal.value = true
  } catch (error) {
    console.error('Erreur chargement proformas:', error)
    alert('Erreur lors du chargement des proformas')
  }
}

const loadDemandes = async () => {
  try {
    const response = await achatApi.getDemandesAchat()
    demandes.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement DA:', error)
  }
}

const loadDepots = async () => {
  try {
    const response = await stockApi.getDepots()
    depots.value = response.data || []
  } catch (error) {
    depots.value = [
      { id: 1, nom: 'Dépôt Central Tana' },
      { id: 2, nom: 'Dépôt Secondaire' }
    ]
  }
}

const loadArticles = async () => {
  try {
    const response = await stockApi.getArticles()
    articles.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement articles:', error)
    articles.value = []
  }
}

const loadEntreprises = async () => {
  try {
    const response = await referenceApi.getEntreprises()
    entreprises.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement entreprises:', error)
    entreprises.value = [
      { id: 1, nom: 'Madagascar Distribution SARL' }
    ]
  }
}

const loadSites = async () => {
  try {
    const response = await referenceApi.getSites()
    sites.value = response.data || []
  } catch (error) {
    console.error('Erreur chargement sites:', error)
    sites.value = [
      { id: 1, nom: 'Site Principal Tana' }
    ]
  }
}

onMounted(() => {
  loadDemandes()
  loadDepots()
  loadArticles()
  loadEntreprises()
  loadSites()
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
