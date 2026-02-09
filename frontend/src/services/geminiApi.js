import axios from 'axios'

const GEMINI_API_KEY = import.meta.env.VITE_GEMINI_API_KEY
const GEMINI_API_URL = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent'

// Contexte système sur l'ERP pour que le chatbot comprenne le système
const ERP_CONTEXT = `
Tu es un assistant intelligent pour un système ERP de gestion de stock.

## MODULES DISPONIBLES:

### 1. RÉFÉRENTIEL
- Entreprises: Gestion des entreprises clientes et fournisseurs
- Clients: Contacts clients avec adresses
- Fournisseurs: Gestion des fournisseurs
- Personnel: Employés de l'entreprise
- Articles: Produits avec catégories (Matières premières, Produits finis, Consommables)
- Dépôts: Entrepôts de stockage

### 2. ACHATS
- Demandes d'Achat (DA): Création de demandes par les services
- Offres Fournisseur: Comparaison des offres reçues
- Proforma Achat: Devis fournisseurs
- Bons de Commande Achat: Commandes validées
- Bons de Livraison Achat: Réception de marchandises
- Factures Achat: Facturation fournisseurs

### 3. VENTES
- Demandes Proforma Client: Demandes des clients
- Proforma Vente: Devis clients (vérification stock automatique)
- Bons de Commande Vente: Commandes clients validées
- Livraison & Encaissement: Sortie stock + encaissement caisse automatique

### 4. STOCK
- Articles: Catalogue produits avec prix achat/vente de référence
- Lots: Traçabilité par lots (FIFO automatique)
- Mouvements: Entrées/Sorties de stock
- État du Stock: Vue en temps réel par dépôt
- Dépôts: Gestion des entrepôts

### 5. FINANCES
- Caisses: Gestion de trésorerie
- Paiements Achat: Règlements fournisseurs
- Paiements Vente: Encaissements clients (automatique lors de livraison)
- Validation Finance: Approbation des paiements

### 6. DIRECTION
- Synthèse: Dashboard global
- Validations: Approbation des demandes d'achat et proformas

## INSTRUCTIONS:

Quand l'utilisateur pose une question sur des données, tu recevras la liste des endpoints API disponibles.
Tu dois choisir les endpoints pertinents et répondre avec un JSON:

{
  "needsData": true,
  "endpoints": ["endpoint1", "endpoint2"],
  "explanation": "Courte explication"
}

Si la question porte sur le processus/aide (pas sur des données):
{
  "needsData": false,
  "response": "Ta réponse complète ici"
}

Réponds de manière claire, précise et professionnelle en français.
`

class GeminiService {
  constructor() {
    this.apiKey = GEMINI_API_KEY
    this.conversationHistory = []
  }

  /**
   * Envoie un message au chatbot Gemini
   * @param {string} userMessage - Message de l'utilisateur
   * @returns {Promise<string>} - Réponse du chatbot
   */
  async sendMessage(userMessage) {
    if (!this.apiKey) {
      throw new Error(
        'Clé API Gemini manquante. Veuillez configurer VITE_GEMINI_API_KEY dans le fichier .env'
      )
    }

    try {
      // Ajouter le message utilisateur à l'historique
      this.conversationHistory.push({
        role: 'user',
        parts: [{ text: userMessage }]
      })

      // Construire le payload pour Gemini
      const payload = {
        contents: [
          {
            role: 'user',
            parts: [{ text: ERP_CONTEXT }]
          },
          ...this.conversationHistory
        ],
        generationConfig: {
          temperature: 0.7,
          topK: 40,
          topP: 0.95,
          maxOutputTokens: 1024
        },
        safetySettings: [
          {
            category: 'HARM_CATEGORY_HARASSMENT',
            threshold: 'BLOCK_NONE'
          },
          {
            category: 'HARM_CATEGORY_HATE_SPEECH',
            threshold: 'BLOCK_NONE'
          },
          {
            category: 'HARM_CATEGORY_SEXUALLY_EXPLICIT',
            threshold: 'BLOCK_NONE'
          },
          {
            category: 'HARM_CATEGORY_DANGEROUS_CONTENT',
            threshold: 'BLOCK_NONE'
          }
        ]
      }

      const response = await axios.post(
        `${GEMINI_API_URL}?key=${this.apiKey}`,
        payload,
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      )

      // Extraire la réponse
      const botResponse = response.data.candidates[0].content.parts[0].text

      // Ajouter la réponse du bot à l'historique
      this.conversationHistory.push({
        role: 'model',
        parts: [{ text: botResponse }]
      })

      return botResponse
    } catch (error) {
      console.error('Erreur Gemini API:', error.response?.data || error.message)
      
      if (error.response?.status === 429) {
        throw new Error('Limite de requêtes atteinte. Veuillez patienter quelques secondes.')
      } else if (error.response?.status === 400) {
        throw new Error('Clé API invalide. Vérifiez votre configuration dans le fichier .env')
      }
      
      throw new Error('Erreur de communication avec le chatbot. Réessayez dans quelques instants.')
    }
  }

  /**
   * Réinitialise l'historique de conversation
   */
  resetConversation() {
    this.conversationHistory = []
  }

  /**
   * Obtient l'historique de conversation
   */
  getConversationHistory() {
    return this.conversationHistory
  }
}

export default new GeminiService()
