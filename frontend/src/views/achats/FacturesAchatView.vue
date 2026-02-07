<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">Factures fournisseurs</h1>
        <p class="mt-1 text-sm text-gray-500">Gestion des factures d'achat</p>
      </div>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-white rounded-xl shadow-sm p-4 border border-gray-100">
        <p class="text-sm text-gray-500">Total factures</p>
        <p class="text-2xl font-bold text-gray-900">{{ factures.length }}</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-4 border border-gray-100">
        <p class="text-sm text-gray-500">Non payées</p>
        <p class="text-2xl font-bold text-red-600">{{ formatMontant(totalNonPaye) }} MGA</p>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-4 border border-gray-100">
        <p class="text-sm text-gray-500">Payées</p>
        <p class="text-2xl font-bold text-green-600">{{ formatMontant(totalPaye) }} MGA</p>
      </div>
    </div>

    <!-- Tableau -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">N° Facture</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fournisseur</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant TTC</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Reste à payer</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="facture in factures" :key="facture.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap font-medium text-gray-900">{{ facture.numeroFactureFournisseur }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(facture.dateFacture) }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ facture.fournisseur?.nom }}</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">{{ formatMontant(facture.montantTtc) }} MGA</td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium" :class="facture.resteAPayer > 0 ? 'text-red-600' : 'text-green-600'">
                {{ formatMontant(facture.resteAPayer) }} MGA
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="facture.resteAPayer > 0 ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ facture.resteAPayer > 0 ? 'Non payé' : 'Payé' }}
                </span>
              </td>
            </tr>
            <tr v-if="factures.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-gray-500">Aucune facture</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { achatApi } from '@/services/api'

const factures = ref([])

const formatDate = (date) => date ? new Date(date).toLocaleDateString('fr-FR') : ''
const formatMontant = (m) => m ? new Intl.NumberFormat('fr-FR').format(m) : '0'

const totalNonPaye = computed(() => factures.value.reduce((sum, f) => sum + (f.resteAPayer || 0), 0))
const totalPaye = computed(() => factures.value.reduce((sum, f) => sum + (f.montantTtc - f.resteAPayer), 0))

const loadFactures = async () => {
  try {
    const response = await achatApi.getFactures()
    factures.value = response.data || []
  } catch (e) {
    factures.value = [
      { id: 1, numeroFactureFournisseur: 'FA-001', dateFacture: '2024-01-25', fournisseur: { nom: 'Papeterie Plus' }, montantTtc: 1800000, resteAPayer: 1800000 }
    ]
  }
}

onMounted(loadFactures)
</script>
