import { useState, useCallback } from 'react';

export default function usePlayerStats() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchStats = useCallback((nickname) => {
    setLoading(true);
    setError(null);
    fetch(`/api/stats/${encodeURIComponent(nickname)}`)
      .then((res) => {
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        return res.json();
      })
      .then((json) => {
        setData(json);
        setLoading(false);
      })
      .catch((err) => {
        setError(err);
        setData(null);
        setLoading(false);
      });
  }, []);

  return { data, loading, error, fetchStats };
}
