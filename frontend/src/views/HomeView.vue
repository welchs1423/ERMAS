<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const inputValue = ref('')

function handleSearch() {
  const nickname = inputValue.value.trim()
  if (!nickname) return
  router.push(`/player/${encodeURIComponent(nickname)}`)
}
</script>

<template>
  <div class="min-h-screen bg-[#f0eff0] flex items-center justify-center">
    <div class="bg-white rounded-2xl shadow-lg p-10 w-full max-w-md">
      <h1 class="text-center text-3xl font-extrabold text-gray-800 mb-1 tracking-tight">
        ERMAS
      </h1>
      <p class="text-center text-gray-400 text-sm mb-6">이터널 리턴 전적 조회</p>
      <form @submit.prevent="handleSearch">
        <div class="flex gap-2">
          <input
            v-model="inputValue"
            type="text"
            placeholder="닉네임을 입력하세요"
            class="flex-1 border border-gray-300 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:border-blue-500 transition-colors"
            autofocus
          />
          <button
            type="submit"
            class="px-5 py-2.5 bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium rounded-lg transition-colors"
          >
            검색
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
