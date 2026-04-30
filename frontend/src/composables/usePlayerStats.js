import { ref } from 'vue'
import axios from 'axios'

export function usePlayerStats() {
  const data = ref(null)
  const loading = ref(false)
  const error = ref(null)

  async function fetchStats(nickname) {
    loading.value = true
    error.value = null
    try {
      const res = await axios.get(`/api/stats/${encodeURIComponent(nickname)}`)
      data.value = res.data
    } catch (err) {
      error.value = err.response?.data?.message ?? '조회에 실패했습니다.'
      data.value = null
    } finally {
      loading.value = false
    }
  }

  return { data, loading, error, fetchStats }
}
