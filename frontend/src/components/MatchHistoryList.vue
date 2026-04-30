<script setup>
import MatchCard from './MatchCard.vue'

defineProps({
  games: { type: Array, required: true },
})
</script>

<template>
  <div v-if="games.length === 0" class="bg-white rounded-xl shadow-sm p-12 text-center text-gray-400 text-sm">
    해당 모드의 게임 기록이 없습니다
  </div>
  <div v-else class="flex flex-col gap-2">
    <MatchCard v-for="game in games" :key="game.gameId" :game="game" />
  </div>
</template>
