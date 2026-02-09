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
  const availableEndpoints = ref(null)

  // Récupérer la liste des endpoints disponibles depuis le backend
  const fetchAvailableEndpoints = async () => {
    try {
      const token = localStorage.getItem('token')
      const response = await axios.get(`${API_BASE_URL}/api/meta/endpoints`, {
        headers: {
          'Authorization': token ? `Bearer ${token}` : '',
          'Content-Type': 'application/json'
        }
      })
      availableEndpoints.value = response.data
      console.log('Endpoints disponibles chargés:', availableEndpoints.value)
      return response.data
    } catch (error) {
      console.error('Erreur chargement endpoints:', error)
      return null
    }
  }

  // Message de bienvenue initial
  const initializeChat = async () => {
    // Charger les endpoints disponibles au démarrage
    if (!availableEndpoints.value) {
      await fetchAvailableEndpoints()
    }
    
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

  // Envoyer un message avec intelligence dynamique (OPTIMISÉ - 1 seul appel)
  const sendMessage = async (messageText) => {
    if (!messageText.trim()) return

    error.value = null

    // Vérifier le temps écoulé depuis la dernière requête
    const lastRequest = localStorage.getItem('gemini_last_request')
    if (lastRequest) {
      const timeSinceLastRequest = Date.now() - parseInt(lastRequest)
      const minWaitTime = 60000 // 60 secondes
      if (timeSinceLastRequest < minWaitTime) {
        const remainingSeconds = Math.ceil((minWaitTime - timeSinceLastRequest) / 1000)
        const warningMsg = `⏳ Veuillez patienter encore ${remainingSeconds} secondes avant la prochaine question (limite de l'API Gemini)`
        
        messages.value.push({
          id: Date.now(),
          text: warningMsg,
          sender: 'bot',
          timestamp: new Date(),
          isError: true
        })
        return
      }
    }

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
      // Charger les endpoints si pas encore fait
      if (!availableEndpoints.value) {
        await fetchAvailableEndpoints()
      }

      // Construire le contexte des endpoints disponibles
      const endpointsContext = availableEndpoints.value 
        ? Object.entries(availableEndpoints.value)
            .map(([module, endpoints]) => {
              const endpointsList = endpoints
                .map(e => `  - ${e.method} ${e.path} - ${e.description}`)
                .join('\n')
              return `\n**${module.toUpperCase()}:**\n${endpointsList}`
            })
            .join('\n')
        : 'Endpoints non disponibles'

      console.log('🔍 Analyse et réponse en un seul appel...')
      
      // APPROCHE OPTIMISÉE: Un seul appel à Gemini qui fait tout
      const smartPrompt = `Tu es l'assistant ERP. L'utilisateur demande: "${messageText}"

ÉTAPE 1 - ANALYSE:
Voici les endpoints API disponibles:
${endpointsContext}

Détermine si cette question nécessite des DONNÉES du système ou si c'est une question sur le PROCESSUS/AIDE.

ÉTAPE 2 - ACTION:
- Si DONNÉES nécessaires: Réponds avec un JSON: {"action": "fetch", "endpoints": ["liste"], "reason": "pourquoi"}
- Si PROCESSUS/AIDE: Réponds DIRECTEMENT à la question de manière complète et structurée

IMPORTANT: 
- Pour des questions comme "combien de...", "liste des...", "état de..." → utilise "fetch"
- Pour des questions comme "comment faire...", "c'est quoi...", "explique..." → réponds directement
- Utilise UNIQUEMENT les endpoints listés ci-dessus
- Pas de \`\`\`json, juste le JSON pur si fetch

Exemples:
Q: "Il y a combien de proformas?" → {"action": "fetch", "endpoints": ["/api/achats/proformas"], "reason": "Compter les proformas"}
Q: "Comment créer une DA?" → [Réponse directe complète sur le processus]

Réponds maintenant:`

      const initialResponse = await geminiApi.sendMessage(smartPrompt)
      
      // Nettoyer la réponse
      let cleanedResponse = initialResponse.trim()
      cleanedResponse = cleanedResponse.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
      
      console.log('Réponse IA:', cleanedResponse)
      
      let finalResponse = ''

      // Vérifier si c'est une demande de fetch ou une réponse directe
      try {
        const parsed = JSON.parse(cleanedResponse)
        
        if (parsed.action === 'fetch' && parsed.endpoints && parsed.endpoints.length > 0) {
          // Récupérer les données
          console.log('📡 Récupération des données:', parsed.endpoints)
          const endpointsData = await fetchEndpointsData(parsed.endpoints)
          
          // Formatter les données pour affichage
          const dataLines = []
          for (const [endpoint, result] of Object.entries(endpointsData)) {
            if (result.success) {
              if (Array.isArray(result.data)) {
                dataLines.push(`📊 ${endpoint}: ${result.data.length} élément(s)`)
              } else {
                dataLines.push(`📊 ${endpoint}: Données récupérées`)
              }
            } else {
              dataLines.push(`❌ ${endpoint}: Erreur - ${result.error}`)
            }
          }
          
          finalResponse = `${parsed.reason}\n\n${dataLines.join('\n')}`
        } else {
          // Réponse directe impossible à parser comme JSON = réponse textuelle
          throw new Error('Not JSON')
        }
      } catch {
        // C'est une réponse textuelle directe (pas du JSON)
        finalResponse = cleanedResponse
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
