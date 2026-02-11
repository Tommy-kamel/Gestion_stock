<template>
  <!-- Bouton flottant pour ouvrir le chatbot -->
  <div v-if="!chatStore.isOpen" class="fixed bottom-6 right-6 z-50">
    <button
      @click="chatStore.openChat"
      class="bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-full p-4 shadow-2xl hover:scale-110 transition-transform duration-200 flex items-center justify-center group relative"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
      </svg>
      
      <!-- Badge de notification -->
      <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center animate-pulse">
        AI
      </span>

      <!-- Tooltip -->
      <div class="absolute bottom-full right-0 mb-2 px-3 py-2 bg-gray-900 text-white text-sm rounded-lg whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
        Assistant IA ERP
      </div>
    </button>
  </div>

  <!-- Fenêtre du chatbot -->
  <Transition name="slide-up">
    <div v-if="chatStore.isOpen" class="fixed bottom-6 right-6 w-96 h-[600px] bg-white rounded-2xl shadow-2xl z-50 flex flex-col overflow-hidden border border-gray-200">
      <!-- Header -->
      <div class="bg-gradient-to-r from-indigo-600 to-purple-600 text-white p-4 flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="relative">
            <div class="w-10 h-10 bg-white rounded-full flex items-center justify-center">
              <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
              </svg>
            </div>
            <span class="absolute bottom-0 right-0 w-3 h-3 bg-green-400 border-2 border-white rounded-full"></span>
          </div>
          <div>
            <h3 class="font-semibold">Assistant IA ERP</h3>
            <p class="text-xs text-indigo-100">En ligne - Propulsé par Gemini</p>
          </div>
        </div>
        
        <div class="flex items-center space-x-2">
          <button
            @click="chatStore.resetConversation"
            class="hover:bg-white/20 rounded-lg p-2 transition-colors"
            title="Nouvelle conversation"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
            </svg>
          </button>
          <button
            @click="chatStore.closeChat"
            class="hover:bg-white/20 rounded-lg p-2 transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Zone de messages -->
      <div ref="messagesContainer" class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
        <div
          v-for="message in chatStore.messages"
          :key="message.id"
          :class="[
            'flex',
            message.sender === 'user' ? 'justify-end' : 'justify-start'
          ]"
        >
          <div
            :class="[
              'max-w-[80%] rounded-2xl px-4 py-2 shadow-sm',
              message.sender === 'user'
                ? 'bg-indigo-600 text-white rounded-br-none'
                : message.isError
                ? 'bg-red-100 text-red-800 rounded-bl-none'
                : 'bg-white text-gray-800 rounded-bl-none border border-gray-200'
            ]"
          >
            <p class="text-sm whitespace-pre-wrap break-words">{{ message.text }}</p>
            <span
              :class="[
                'text-xs block mt-1',
                message.sender === 'user' ? 'text-indigo-200' : 'text-gray-400'
              ]"
            >
              {{ formatTime(message.timestamp) }}
            </span>
          </div>
        </div>

        <!-- Indicateur de chargement -->
        <div v-if="chatStore.isLoading" class="flex justify-start">
          <div class="bg-white rounded-2xl rounded-bl-none px-4 py-3 shadow-sm border border-gray-200">
            <div class="flex space-x-2">
              <div class="w-2 h-2 bg-indigo-600 rounded-full animate-bounce" style="animation-delay: 0ms"></div>
              <div class="w-2 h-2 bg-indigo-600 rounded-full animate-bounce" style="animation-delay: 150ms"></div>
              <div class="w-2 h-2 bg-indigo-600 rounded-full animate-bounce" style="animation-delay: 300ms"></div>
            </div>
          </div>
        </div>

        <!-- Questions suggérées (seulement au début) -->
        <div v-if="chatStore.messages.length <= 1 && !chatStore.isLoading" class="space-y-2">
          <p class="text-xs text-gray-500 font-medium px-2">Questions suggérées :</p>
          <button
            v-for="(question, index) in chatStore.suggestedQuestions"
            :key="index"
            @click="sendSuggestedQuestion(question)"
            class="w-full text-left text-sm bg-white hover:bg-indigo-50 text-gray-700 rounded-lg px-3 py-2 border border-gray-200 transition-colors"
          >
            {{ question }}
          </button>
        </div>
      </div>

      <!-- Zone de saisie -->
      <div class="p-4 bg-white border-t border-gray-200">
        <form @submit.prevent="sendMessage" class="flex items-end space-x-2">
          <textarea
            v-model="userInput"
            @keydown.enter.exact.prevent="sendMessage"
            placeholder="Posez votre question..."
            rows="1"
            class="flex-1 resize-none rounded-xl border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            :disabled="chatStore.isLoading"
          ></textarea>
          <button
            type="submit"
            :disabled="!userInput.trim() || chatStore.isLoading"
            class="bg-indigo-600 text-white rounded-xl p-3 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
          </button>
        </form>
        <p class="text-xs text-gray-400 mt-2 text-center">
          Appuyez sur Entrée pour envoyer • Shift+Entrée pour nouvelle ligne
        </p>
        <!-- Avertissement rate limiting -->
        <div class="mt-2 bg-amber-50 border border-amber-200 rounded-lg p-2">
          <p class="text-xs text-amber-800 text-center">
            ⏳ <strong>Délai minimum : 60 secondes</strong> entre chaque question (limite API Gemini)
          </p>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { useChatbotStore } from '@/stores/chatbot'

const chatStore = useChatbotStore()
const userInput = ref('')
const messagesContainer = ref(null)

// Envoyer un message
const sendMessage = async () => {
  if (!userInput.value.trim() || chatStore.isLoading) return
  
  const message = userInput.value
  userInput.value = ''
  
  await chatStore.sendMessage(message)
  scrollToBottom()
}

// Envoyer une question suggérée
const sendSuggestedQuestion = async (question) => {
  await chatStore.sendMessage(question)
  scrollToBottom()
}

// Scroller vers le bas
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// Formater l'heure
const formatTime = (date) => {
  return new Date(date).toLocaleTimeString('fr-FR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// Auto-scroll quand nouveaux messages
watch(
  () => chatStore.messages.length,
  () => {
    scrollToBottom()
  }
)
</script>

<style scoped>
/* Animation d'apparition */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

/* Scrollbar personnalisée */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}
</style>
