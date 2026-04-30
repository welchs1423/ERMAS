<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  nickname: { type: String, required: true },
  refreshing: { type: Boolean, default: false },
})

const emit = defineEmits(['refresh'])

const router = useRouter()
const inputValue = ref('')

const initial = () => props.nickname ? props.nickname.charAt(0).toUpperCase() : '?'

function handleSearch() {
  const nick = inputValue.value.trim()
  if (!nick) return
  router.push(`/player/${encodeURIComponent(nick)}`)
}
</script>

<template>
  <div class="bg-[#1e2025] py-8 border-b border-[#2e3038]">
    <div class="max-w-6xl mx-auto px-4">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-5">
          <div class="relative">
            <div class="w-20 h-20 rounded-full bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center text-white text-3xl font-bold shadow-lg">
              {{ initial() }}
            </div>
            <div class="absolute -bottom-1 -right-1 bg-blue-500 text-white text-xs px-1.5 py-0.5 rounded-full font-bold">
              100
            </div>
          </div>

          <div>
            <div class="text-gray-400 text-xs mb-0.5">이터널 리턴</div>
            <div class="text-white text-2xl font-bold tracking-tight">{{ nickname }}</div>
            <button
              :disabled="refreshing"
              class="mt-2 flex items-center gap-1.5 px-3 py-1.5 bg-[#2a2d35] hover:bg-[#33363f] border border-[#3e4049] text-gray-300 hover:text-white text-xs font-medium rounded-md transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              @click="emit('refresh')"
            >
              <svg
                :class="refreshing ? 'animate-spin' : ''"
                xmlns="http://www.w3.org/2000/svg"
                width="12"
                height="12"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" />
                <path d="M21 3v5h-5" />
                <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" />
                <path d="M8 16H3v5" />
              </svg>
              전적 갱신
            </button>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <form class="flex gap-2" @submit.prevent="handleSearch">
            <input
              v-model="inputValue"
              type="text"
              placeholder="닉네임 검색"
              class="bg-[#2a2d35] text-gray-200 border border-[#3e4049] rounded-md px-3 py-2 text-sm focus:outline-none focus:border-blue-500 placeholder-gray-500 w-44"
            />
            <button
              type="submit"
              class="px-3 py-2 bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium rounded-md transition-colors"
            >
              검색
            </button>
          </form>
          <select class="bg-[#2a2d35] text-gray-300 border border-[#3e4049] rounded-md px-3 py-2 text-sm focus:outline-none focus:border-blue-500 cursor-pointer">
            <option value="27">시즌 27</option>
            <option value="26">시즌 26</option>
            <option value="25">시즌 25</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>
