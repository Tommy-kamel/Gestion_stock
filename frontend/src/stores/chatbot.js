import { defineStore } from 'pinia'
import { ref } from 'vue'
import geminiApi from '@/services/geminiApi'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080'

export const useChatbotStore = defineStore('chatbot', () => {
  // État
  const isOpen = ref(false)
  const messages = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  // Message de bienvenue initial
  const initializeChat = () => {
    if (messages.value.length === 0) {
      messages.value.push({
        id: Date.now(),
        text: "Bonjour ! 👋 Je suis votre assistant ERP. Je peux vous aider avec :\n\n• Navigation dans les modules\n• Explication des workflows\n• Résolution de problèmes\n• Fonctionnalités du système\n\nComment puis-je vous aider aujourd'hui ?",
        sender: 'bot',
        timestamp: new Date()
      })
    }
  }

  // Ouvrir/fermer le chatbot
  const toggleChat = () => {
    isOpen.value = !isOpen.value
    if (isOpen.value) {
      initializeChat()
    }
  }

  const openChat = () => {
    isOpen.value = true
    initializeChat()
  }

  const closeChat = () => {
    isOpen.value = false
  }

  // Appeler dynamiquement les endpoints suggérés par l'IA
  const fetchEndpointsData = async (endpoints) => {
    const results = {}
    
    for (const endpoint of endpoints) {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(`${API_BASE_URL}${endpoint}`, {
          headers: {
            'Authorization': token ? `Bearer ${token}` : '',
            'Content-Type': 'application/json'
          }
        })
        
        results[endpoint] = {
          success: true,
          data: response.data,
          count: Array.isArray(response.data) ? response.data.length : null
        }
      } catch (error) {
        console.error(`Erreur endpoint ${endpoint}:`, error)
        results[endpoint] = {
          success: false,
          error: error.message
        }
      }
    }
    
    return results
  }

  // Envoyer un message avec intelligence dynamique
  const sendMessage = async (messageText) => {
    if (!messageText.trim()) return

    error.value = null

    // Ajouter le message utilisateur
    const userMessage = {
      id: Date.now(),
      text: messageText,
      sender: 'user',
      timestamp: new Date()
    }
    messages.value.push(userMessage)

    // Démarrer le chargement
    isLoading.value = true

    try {
      // ÉTAPE 1: Demander à Gemini quels endpoints appeler
      const analysisPrompt = `Question utilisateur: "${messageText}"

Analyse cette question et réponds UNIQUEMENT avec un JSON valide (pas de markdown, pas de texte avant/après):
- Si la question porte sur des DONNÉES réelles du système → {"needsData": true, "endpoints": ["liste des endpoints"], "explanation": "pourquoi"}
- Si la question porte sur le PROCESSUS/AIDE → {"needsData": false, "response": "ta réponse complète ici"}

Exemple 1: "Il y a combien de proformas fournisseur ?"
{"needsData": true, "endpoints": ["/api/achats/proformas"], "explanation": "Pour compter les proformas fournisseurs"}

Exemple 2: "Comment créer une DA ?"
{"needsData": false, "response": "Pour créer une Demande d'Achat : 1. Menu Achats > Demandes d'Achat..."}

Réponds UNIQUEMENT avec le JSON, sans \`\`\`json ni aucun autre texte:`

      const analysisResponse = await geminiApi.sendMessage(analysisPrompt)
      
      // Nettoyer la réponse (enlever les markdown si présents)
      let cleanedResponse = analysisResponse.trim()
      cleanedResponse = cleanedResponse.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
      
      console.log('Réponse IA:', cleanedResponse)
      
      let analysis
      try {
        analysis = JSON.parse(cleanedResponse)
      } catch (parseError) {
        console.error('Erreur parsing JSON:', parseError, cleanedResponse)
        throw new Error('Erreur d\'analyse de la question. Reformulez svp.')
      }

      let finalResponse = ''

      if (analysis.needsData && analysis.endpoints && analysis.endpoints.length > 0) {
        // ÉTAPE 2: Récupérer les données des endpoints
        const endpointsData = await fetchEndpointsData(analysis.endpoints)
        
        // ÉTAPE 3: Reformuler avec Gemini en incluant les données réelles
        const dataContext = Object.entries(endpointsData)
          .map(([endpoint, result]) => {
            if (result.success) {
              return `\n[DONNÉES DE ${endpoint}]:\n${JSON.stringify(result.data, null, 2)}`
            } else {
              return `\n[ERREUR ${endpoint}]: ${result.error}`
            }
          })
          .join('\n')

        const finalPrompt = `Question utilisateur: "${messageText}"

${dataContext}

Maintenant, réponds à la question de l'utilisateur en utilisant ces données RÉELLES du système.
Donne une réponse précise, claire et structurée en français.
Utilise des émojis si approprié.
Formate bien la réponse avec des listes ou tableaux si nécessaire.`

        finalResponse = await geminiApi.sendMessage(finalPrompt)
        
      } else if (!analysis.needsData && analysis.response) {
        // Réponse directe sans besoin de données
        finalResponse = analysis.response
      } else {
        throw new Error('Format de réponse invalide de l\'IA')
      }

      // Ajouter la réponse du bot
      const botMessage = {
        id: Date.now() + 1,
        text: finalResponse,
        sender: 'bot',
        timestamp: new Date()
      }
      messages.value.push(botMessage)
    } catch (err) {
      console.error('Erreur chatbot:', err)
      error.value = err.message

      // Message d'erreur dans le chat
      const errorMessage = {
        id: Date.now() + 1,
        text: `❌ Erreur: ${err.message}`,
        sender: 'bot',
        timestamp: new Date(),
        isError: true
      }
      messages.value.push(errorMessage)
    } finally {
      isLoading.value = false
    }
  }

  // Réinitialiser la conversation
  const resetConversation = () => {
    messages.value = []
    geminiApi.resetConversation()
    initializeChat()
  }

  // Exemples de questions suggérées
  const suggestedQuestions = [
    "Comment créer une demande d'achat ?",
    "Expliquez le workflow des ventes",
    "Comment fonctionne le stock FIFO ?",
    "Qu'est-ce qu'un proforma de vente ?",
    "Comment valider une demande d'achat ?"
  ]

  return {
    // État
    isOpen,
    messages,
    isLoading,
    error,
    suggestedQuestions,
    
    // Actions
    toggleChat,
    openChat,
    closeChat,
    sendMessage,
    resetConversation,
    initializeChat
  }
})
