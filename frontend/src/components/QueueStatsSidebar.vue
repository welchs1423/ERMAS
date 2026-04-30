<script setup>
import { computed } from 'vue'

const TABS = [
  { key: 'total', label: '전체' },
  { key: 'solo',  label: '솔로' },
  { key: 'duo',   label: '듀오' },
  { key: 'squad', label: '스쿼드' },
]

const props = defineProps({
  activeTab: { type: String, required: true },
  stats:     { type: Object, default: null },
})

const emit = defineEmits(['tab-change'])

const radius        = 42
const circumference = 2 * Math.PI * radius

const dashOffset = computed(() => {
  const rate = props.stats?.winRate ?? 0
  return circumference - (rate / 100) * circumference
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm overflow-hidden">
    <div class="grid grid-cols-4 border-b border-gray-100">
      <button
        v-for="tab in TABS"
        :key="tab.key"
        class="py-3 text-xs font-semibold transition-colors"
        :class="activeTab === tab.key
          ? 'text-blue-500 border-b-2 border-blue-500'
          : 'text-gray-400 hover:text-gray-600 hover:bg-gray-50'"
        @click="emit('tab-change', tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="stats && stats.totalGames > 0" class="p-5">
      <!-- 승률 원형 차트 -->
      <div class="flex justify-center mb-5">
        <div class="relative flex items-center justify-center w-28 h-28">
          <svg width="112" height="112" class="-rotate-90">
            <circle cx="56" cy="56" :r="radius" fill="none" stroke="#e5e7eb" stroke-width="9" />
            <circle
              cx="56"
              cy="56"
              :r="radius"
              fill="none"
              stroke="#3b82f6"
              stroke-width="9"
              :stroke-dasharray="circumference"
              :stroke-dashoffset="dashOffset"
              stroke-linecap="round"
              style="transition: stroke-dashoffset 0.6s ease"
            />
          </svg>
          <div class="absolute inset-0 flex flex-col items-center justify-center">
            <span class="text-2xl font-bold text-gray-800 leading-none">{{ stats.winRate }}%</span>
            <span class="text-xs text-gray-400 mt-1">{{ stats.totalWins }}승 / {{ stats.totalGames }}게임</span>
          </div>
        </div>
      </div>

      <!-- 평균 스탯 -->
      <div class="space-y-3">
        <div class="flex items-center justify-between text-sm">
          <span class="text-gray-400">평균 순위</span>
          <span class="font-semibold text-gray-700">#{{ stats.avgRank }}</span>
        </div>
        <div class="flex items-center justify-between text-sm">
          <span class="text-gray-400">평균 킬</span>
          <span class="font-semibold text-gray-700">{{ stats.avgKills }}킬</span>
        </div>
        <div class="flex items-center justify-between text-sm">
          <span class="text-gray-400">평균 데미지</span>
          <span class="font-semibold text-gray-700">{{ stats.avgDamage.toLocaleString() }}</span>
        </div>
      </div>

      <!-- 승/패 카운트 -->
      <div class="mt-4 pt-4 border-t border-gray-100 grid grid-cols-2 gap-2 text-center">
        <div>
          <div class="text-2xl font-bold text-blue-500">{{ stats.totalWins }}</div>
          <div class="text-xs text-gray-400 mt-0.5">승</div>
        </div>
        <div>
          <div class="text-2xl font-bold text-gray-600">{{ stats.totalGames - stats.totalWins }}</div>
          <div class="text-xs text-gray-400 mt-0.5">패</div>
        </div>
      </div>
    </div>

    <div v-else class="p-10 text-center text-gray-400 text-sm">
      해당 모드의 게임 기록이 없습니다
    </div>
  </div>
</template>
