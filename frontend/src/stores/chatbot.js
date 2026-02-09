import { defineStore } from 'pinia'
import { ref } from 'vue'
import geminiApi from '@/services/geminiApi'

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
        text: "Bonjour ! 👋 Je suis votre assistant ERP. Je peux répondre à vos questions sur :\n\n• Les fonctionnalités du système\n• L'utilisation des modules\n• Les processus de gestion\n• Les bonnes pratiques\n\nPosez-moi une question !",
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

  // Envoyer un message simple
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
      // Prompt simple et direct
      const prompt = `Tu es un assistant ERP spécialisé dans la gestion d'entreprise. 
Réponds DIRECTEMENT en texte simple à la question: "${messageText}"

Contexte du système:
- Gestion des achats (demandes, proformas, bons de commande, factures)
- Gestion des ventes (devis, proformas, bons de commande, factures)
- Gestion du stock (articles, dépôts, mouvements, valorisation FIFO)
- Gestion financière (caisses, paiements)
- Workflows de validation (demandeur → responsable → direction → finance)

IMPORTANT: 
- Réponds UNIQUEMENT en texte naturel, JAMAIS en JSON
- Ne retourne JAMAIS de structure comme {"needsData": ..., "response": ...}
- N'utilise PAS de formatage Markdown (pas de **, pas de __, pas de ##)
- Utilise uniquement du texte simple avec des tirets - pour les listes
- Donne directement la réponse en français de manière professionnelle et pédagogique`

      let response = await geminiApi.sendMessage(prompt)

      // Nettoyer la réponse si elle contient du JSON ou du Markdown
      response = response.trim()
      
      // Supprimer le formatage Markdown
      response = response.replace(/\*\*/g, '')  // Retirer **
      response = response.replace(/\_\_/g, '')  // Retirer __
      response = response.replace(/^#{1,6}\s+/gm, '')  // Retirer les titres ##
      
      if (response.includes('```json') || response.includes('"needsData"') || response.includes('"response"')) {
        // Extraire le contenu du champ "response" si présent
        try {
          const cleanedJson = response.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
          const parsed = JSON.parse(cleanedJson)
          if (parsed.response) {
            response = parsed.response
          }
        } catch {
          // Si parsing échoue, garder la réponse telle quelle
        }
      }

      // Ajouter la réponse du bot
      const botMessage = {
        id: Date.now() + 1,
        text: response,
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
