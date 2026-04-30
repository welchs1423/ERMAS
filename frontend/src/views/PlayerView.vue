<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { usePlayerStats } from '../composables/usePlayerStats'
import ProfileHeader from '../components/ProfileHeader.vue'
import QueueStatsSidebar from '../components/QueueStatsSidebar.vue'
import MatchHistoryList from '../components/MatchHistoryList.vue'

const PREMADE_COUNT_MAP = { total: null, solo: 1, duo: 2, squad: 3 }

const route = useRoute()
const { data, loading, error, fetchStats } = usePlayerStats()
const activeTab = ref('total')

function load() {
  fetchStats(decodeURIComponent(route.params.nickname))
}

onMounted(load)
watch(() => route.params.nickname, load)

const activeStats = computed(() => data.value?.[activeTab.value])

const filteredGames = computed(() => {
  if (!data.value?.games) return []
  const count = PREMADE_COUNT_MAP[activeTab.value]
  return count === null
    ? data.value.games
    : data.value.games.filter((g) => g.premadeCount === count)
})
</script>

<template>
  <div class="min-h-screen bg-[#f0eff0]">
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="flex flex-col items-center gap-3 text-gray-500">
        <div class="w-8 h-8 border-[3px] border-blue-400 border-t-transparent rounded-full animate-spin" />
        <span class="text-sm">데이터 불러오는 중...</span>
      </div>
    </div>

    <div v-else-if="error" class="flex items-center justify-center min-h-screen">
      <div class="bg-white rounded-xl shadow p-8 text-center">
        <p class="text-red-500 text-sm">{{ error }}</p>
      </div>
    </div>

    <template v-else-if="data">
      <ProfileHeader
        :nickname="data.nickname"
        :refreshing="loading"
        @refresh="load"
      />
      <div class="max-w-6xl mx-auto px-4 py-6">
        <div class="grid grid-cols-[300px_1fr] gap-4 items-start">
          <QueueStatsSidebar
            :active-tab="activeTab"
            :stats="activeStats"
            @tab-change="activeTab = $event"
          />
          <MatchHistoryList :games="filteredGames" />
        </div>
      </div>
    </template>
  </div>
</template>
