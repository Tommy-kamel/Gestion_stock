<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Écran de connexion -->
    <template v-if="!authStore.isAuthenticated">
      <router-view />
    </template>

    <!-- Application principale -->
    <template v-else>
      <div class="flex h-screen overflow-hidden">
        <!-- Sidebar -->
        <aside 
          :class="[
            'fixed inset-y-0 left-0 z-50 w-64 bg-slate-800 transform transition-transform duration-300 lg:relative lg:translate-x-0',
            sidebarOpen ? 'translate-x-0' : '-translate-x-full'
          ]"
        >
          <!-- Logo -->
          <div class="flex items-center justify-between h-16 px-4 bg-slate-900">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-indigo-500 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
              <span class="font-bold text-white text-lg">ERP Stock</span>
            </div>
            <button @click="sidebarOpen = false" class="lg:hidden text-gray-400 hover:text-white">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Navigation -->
          <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto max-h-[calc(100vh-8rem)]">
            <!-- Dashboard -->
            <router-link to="/" class="nav-link" active-class="active" exact>
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
              </svg>
              <span>Tableau de bord</span>
            </router-link>

            <!-- Module STOCK -->
            <div class="pt-2">
              <button 
                @click="toggleMenu('stock')"
                class="nav-section-header"
              >
                <div class="flex items-center">
                  <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                  </svg>
                  <span>Stock</span>
                </div>
                <svg :class="['w-4 h-4 transition-transform', openMenus.stock ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              <div v-show="openMenus.stock" class="pl-4 space-y-1 mt-1">
                <router-link to="/stock/dashboard" class="nav-sublink" active-class="active">
                  Dashboard
                </router-link>
                <router-link to="/stock/valorisation" class="nav-sublink" active-class="active">
                  Valorisation des stocks
                </router-link>
                <router-link to="/stock/mouvements" class="nav-sublink" active-class="active">
                  Mouvements
                </router-link>
                <!-- <router-link to="/stock/articles" class="nav-sublink" active-class="active">
                  Articles
                </router-link> -->
                <!-- <router-link to="/stock/lots" class="nav-sublink" active-class="active">
                  Lots
                </router-link> -->
                <!-- <router-link to="/stock/depots" class="nav-sublink" active-class="active">
                  Dépôts
                </router-link> -->
              </div>
            </div>

            <!-- Module ACHATS -->
            <div class="pt-4">
              <button 
                @click="toggleMenu('achats')"
                class="nav-section-header"
              >
                <div class="flex items-center">
                  <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  <span>Achats</span>
                </div>
                <svg :class="['w-4 h-4 transition-transform', openMenus.achats ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              <div v-show="openMenus.achats" class="pl-4 space-y-1 mt-1">
                <router-link to="/achats/dashboard" class="nav-sublink" active-class="active">
                  Dashboard
                </router-link>
                <router-link to="/achats/demandes" class="nav-sublink" active-class="active">
                  Demandes d'achat
                </router-link>
                <router-link to="/achats/proformas" class="nav-sublink" active-class="active">
                  Proformas fournisseurs
                </router-link>
                <router-link to="/achats/bons-commande" class="nav-sublink" active-class="active">
                  Bons de commande
                </router-link>
              </div>
            </div>

            <!-- Module VENTES -->
            <div class="pt-2">
              <button 
                @click="toggleMenu('ventes')"
                class="nav-section-header"
              >
                <div class="flex items-center">
                  <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                  </svg>
                  <span>Ventes</span>
                </div>
                <svg :class="['w-4 h-4 transition-transform', openMenus.ventes ? 'rotate-180' : '']" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              <div v-show="openMenus.ventes" class="pl-4 space-y-1 mt-1">
                <router-link to="/ventes/dashboard" class="nav-sublink" active-class="active">
                  Dashboard
                </router-link>
                <router-link to="/ventes/demandes-client" class="nav-sublink" active-class="active">
                  Demandes d'achat client
                </router-link>
                <router-link to="/ventes/demandes-proforma" class="nav-sublink" active-class="active">
                  Creation proforma
                </router-link>
                <!-- <router-link to="/ventes/devis" class="nav-sublink" active-class="active">
                  Devis / Proformas
                </router-link> -->
                <router-link to="/ventes/bons-commande" class="nav-sublink" active-class="active">
                  Bons de commande
                </router-link>
                <!-- <router-link to="/ventes/bons-livraison" class="nav-sublink" active-class="active">
                  Bons de livraison
                </router-link>
                <router-link to="/ventes/factures" class="nav-sublink" active-class="active">
                  Factures clients
                </router-link> -->
              </div>
            </div>
          </nav>
          
          <!-- User info at bottom -->
          <div class="p-4 border-t border-slate-700">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-indigo-500 rounded-full flex items-center justify-center">
                <span class="text-white font-medium">{{ authStore.userInitials }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-white truncate">{{ authStore.userName }}</p>
                <p class="text-xs text-slate-400 truncate">Super Admin</p>
              </div>
            </div>
          </div>
        </aside>

        <!-- Overlay mobile -->
        <div 
          v-if="sidebarOpen" 
          class="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden"
          @click="sidebarOpen = false"
        ></div>

        <!-- Main content -->
        <div class="flex-1 flex flex-col overflow-hidden">
          <!-- Header -->
          <header class="bg-white shadow-sm border-b border-gray-200 sticky top-0 z-30">
            <div class="flex items-center justify-between h-16 px-4 sm:px-6">
              <!-- Mobile menu button -->
              <button @click="sidebarOpen = true" class="lg:hidden text-gray-500 hover:text-gray-700">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
              </button>

              <!-- Breadcrumb / Page title -->
              <div class="flex items-center space-x-2">
                <h1 class="text-lg font-semibold text-gray-900">
                  {{ currentPageTitle }}
                </h1>
              </div>

              <!-- Right side -->
              <div class="flex items-center space-x-4">
                <!-- Notifications -->
                <button class="relative p-2 text-gray-500 hover:text-gray-700 rounded-full hover:bg-gray-100">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                  </svg>
                  <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
                </button>

                <!-- User dropdown -->
                <div class="relative">
                  <button 
                    @click="userMenuOpen = !userMenuOpen"
                    class="flex items-center space-x-2 text-gray-700 hover:text-gray-900"
                  >
                    <div class="w-8 h-8 bg-indigo-100 rounded-full flex items-center justify-center">
                      <span class="text-sm font-medium text-indigo-700">{{ authStore.userInitials }}</span>
                    </div>
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>

                  <div 
                    v-if="userMenuOpen"
                    class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-100 py-1 z-50"
                  >
                    <div class="px-4 py-2 border-b border-gray-100">
                      <p class="text-sm font-medium text-gray-900">{{ authStore.userName }}</p>
                      <p class="text-xs text-gray-500">{{ authStore.user?.email }}</p>
                    </div>
                    <button 
                      @click="logout"
                      class="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 flex items-center"
                    >
                      <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                      </svg>
                      Se déconnecter
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </header>

          <!-- Main content area -->
          <main class="flex-1 overflow-y-auto bg-gray-50 p-6">
            <router-view/>
          </main>
        </div>
      </div>

      <!-- Chatbot IA -->
      <Chatbot />
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Chatbot from '@/components/Chatbot.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sidebarOpen = ref(false)
const userMenuOpen = ref(false)

// Menus dépliables
const openMenus = reactive({
  achats: true,
  ventes: false,
  stock: false,
  finances: false,
  referentiel: false
})

const toggleMenu = (menu) => {
  openMenus[menu] = !openMenus[menu]
}

// Computed
const currentPageTitle = computed(() => route.meta?.title || 'Tableau de bord')

// Méthodes
const logout = () => {
  authStore.logout()
  userMenuOpen.value = false
  router.push('/login')
}

// Fermer menus sur changement de route (mobile)
watch(() => route.path, () => {
  sidebarOpen.value = false
  userMenuOpen.value = false
})
</script>

<style>
/* Navigation links */
.nav-link {
  @apply flex items-center px-3 py-2.5 text-slate-300 rounded-lg hover:bg-slate-700 hover:text-white transition-colors;
}
.nav-link.active {
  @apply bg-indigo-600 text-white;
}
.nav-link svg {
  @apply mr-3;
}

/* Section headers (collapsible) */
.nav-section-header {
  @apply w-full flex items-center justify-between px-3 py-2.5 text-slate-300 rounded-lg hover:bg-slate-700 hover:text-white transition-colors;
}

/* Sub links */
.nav-sublink {
  @apply block px-3 py-2 text-sm text-slate-400 rounded-lg hover:bg-slate-700 hover:text-white transition-colors;
}
.nav-sublink.active {
  @apply bg-slate-700 text-indigo-400;
}
</style>
