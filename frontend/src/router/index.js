import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Lazy loading des composants
const LoginView = () => import('@/views/LoginView.vue')
const DashboardView = () => import('@/views/DashboardView.vue')

// Achats
const DemandesAchatView = () => import('@/views/achats/DemandesAchatView.vue')
const ProformasAchatView = () => import('@/views/achats/ProformasAchatView.vue')
const BonsCommandeAchatView = () => import('@/views/achats/BonsCommandeAchatView.vue')
const BonsLivraisonAchatView = () => import('@/views/achats/BonsLivraisonAchatView.vue')
const FacturesAchatView = () => import('@/views/achats/FacturesAchatView.vue')
const DashboardAchatsView = () => import('@/views/achats/DashboardAchatsView.vue')

// Ventes
const DemandesAchatClientView = () => import('@/views/ventes/DemandesAchatClientView.vue')
const DemandesProformaView = () => import('@/views/ventes/DemandesProformaView.vue')
const DevisVenteView = () => import('@/views/ventes/DevisVenteView.vue')
const BonsCommandeVenteView = () => import('@/views/ventes/BonsCommandeVenteView.vue')
const BonsLivraisonVenteView = () => import('@/views/ventes/BonsLivraisonVenteView.vue')
const FacturesVenteView = () => import('@/views/ventes/FacturesVenteView.vue')
const DashboardVentesView = () => import('@/views/ventes/DashboardVentesView.vue')

// Stock
const ValorisationStockView = () => import('@/views/stock/ValorisationStockView.vue')
const ArticlesView = () => import('@/views/stock/ArticlesView.vue')
const LotsView = () => import('@/views/stock/LotsView.vue')
const MouvementsView = () => import('@/views/stock/MouvementsView.vue')
const DepotsView = () => import('@/views/stock/DepotsView.vue')
const DashboardStockView = () => import('@/views/stock/DashboardStockView.vue')

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false, title: 'Connexion' }
  },
  {
    path: '/',
    name: 'dashboard',
    component: DashboardView,
    meta: { requiresAuth: true, title: 'Tableau de bord' }
  },
  
  // ===================== ACHATS =====================
  {
    path: '/achats/dashboard',
    name: 'dashboard-achats',
    component: DashboardAchatsView,
    meta: { requiresAuth: true, title: 'Dashboard Achats' }
  },
  {
    path: '/achats/demandes',
    name: 'demandes-achat',
    component: DemandesAchatView,
    meta: { requiresAuth: true, title: 'Demandes d\'achat' }
  },
  {
    path: '/achats/proformas',
    name: 'proformas-achat',
    component: ProformasAchatView,
    meta: { requiresAuth: true, title: 'Proformas fournisseurs' }
  },
  {
    path: '/achats/bons-commande',
    name: 'bons-commande-achat',
    component: BonsCommandeAchatView,
    meta: { requiresAuth: true, title: 'Bons de commande achat' }
  },
  {
    path: '/achats/bons-livraison',
    name: 'bons-livraison-achat',
    component: BonsLivraisonAchatView,
    meta: { requiresAuth: true, title: 'Bons de livraison achat' }
  },
  {
    path: '/achats/factures',
    name: 'factures-achat',
    component: FacturesAchatView,
    meta: { requiresAuth: true, title: 'Factures fournisseurs' }
  },
  
  // ===================== VENTES =====================
  {
    path: '/ventes/dashboard',
    name: 'dashboard-ventes',
    component: DashboardVentesView,
    meta: { requiresAuth: true, title: 'Dashboard Ventes' }
  },
  {
    path: '/ventes/demandes-client',
    name: 'demandes-achat-client',
    component: DemandesAchatClientView,
    meta: { requiresAuth: true, title: 'Demandes d\'achat client' }
  },
  {
    path: '/ventes/demandes-proforma',
    name: 'demandes-proforma',
    component: DemandesProformaView,
    meta: { requiresAuth: true, title: 'Demandes de proforma' }
  },
  {
    path: '/ventes/devis',
    name: 'devis-vente',
    component: DevisVenteView,
    meta: { requiresAuth: true, title: 'Devis / Proformas' }
  },
  {
    path: '/ventes/bons-commande',
    name: 'bons-commande-vente',
    component: BonsCommandeVenteView,
    meta: { requiresAuth: true, title: 'Bons de commande vente' }
  },
  {
    path: '/ventes/bons-livraison',
    name: 'bons-livraison-vente',
    component: BonsLivraisonVenteView,
    meta: { requiresAuth: true, title: 'Bons de livraison vente' }
  },
  {
    path: '/ventes/factures',
    name: 'factures-vente',
    component: FacturesVenteView,
    meta: { requiresAuth: true, title: 'Factures clients' }
  },
  
  // ===================== STOCK =====================
  {
    path: '/stock/dashboard',
    name: 'dashboard-stock',
    component: DashboardStockView,
    meta: { requiresAuth: true, title: 'Dashboard Stock' }
  },
  {
    path: '/stock/valorisation',
    name: 'valorisation-stock',
    component: ValorisationStockView,
    meta: { requiresAuth: true, title: 'Valorisation des stocks' }
  },
  {
    path: '/stock/articles',
    name: 'articles',
    component: ArticlesView,
    meta: { requiresAuth: true, title: 'Articles' }
  },
  {
    path: '/stock/lots',
    name: 'lots',
    component: LotsView,
    meta: { requiresAuth: true, title: 'Gestion des lots' }
  },
  {
    path: '/stock/mouvements',
    name: 'mouvements',
    component: MouvementsView,
    meta: { requiresAuth: true, title: 'Mouvements de stock' }
  },
  {
    path: '/stock/depots',
    name: 'depots',
    component: DepotsView,
    meta: { requiresAuth: true, title: 'Dépôts' }
  },
  
  // Catch-all redirect
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
