import { useState } from 'react';
import usePlayerStats from './hooks/usePlayerStats';
import ProfileHeader from './components/ProfileHeader';
import QueueStatsSidebar from './components/QueueStatsSidebar';
import MatchHistoryList from './components/MatchHistoryList';

const PREMADE_COUNT_MAP = { 전체: null, 솔로큐: 1, 듀오큐: 2, 스쿼드큐: 3 };
const STATS_KEY_MAP     = { 전체: 'total', 솔로큐: 'solo', 듀오큐: 'duo', 스쿼드큐: 'squad' };

export default function App() {
  const [activeTab, setActiveTab] = useState('전체');
  const [inputValue, setInputValue] = useState('');
  const { data, loading, error, fetchStats } = usePlayerStats();

  const handleSearch = (nickname) => {
    if (nickname.trim()) {
      setActiveTab('전체');
      fetchStats(nickname.trim());
    }
  };

  // 초기 검색 화면 (데이터 없음, 로딩 아님)
  if (!data && !loading) {
    return (
      <div className="min-h-screen bg-[#f0eff0] flex items-center justify-center">
        <div className="bg-white rounded-2xl shadow-lg p-10 w-full max-w-md">
          <h1 className="text-center text-2xl font-bold text-gray-800 mb-1">ERMAS</h1>
          <p className="text-center text-gray-400 text-sm mb-6">이터널 리턴 전적 검색</p>
          <form onSubmit={(e) => { e.preventDefault(); handleSearch(inputValue); }}>
            <div className="flex gap-2">
              <input
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="닉네임을 입력하세요"
                className="flex-1 border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:border-blue-500"
                autoFocus
              />
              <button
                type="submit"
                className="px-5 py-2 bg-blue-500 hover:bg-blue-600 text-white text-sm font-medium rounded-lg transition-colors"
              >
                검색
              </button>
            </div>
            {error && (
              <p className="mt-3 text-red-500 text-sm text-center">
                {error.message ?? '검색에 실패했습니다.'}
              </p>
            )}
          </form>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#f0eff0]">
        <div className="flex flex-col items-center gap-3 text-gray-500">
          <div className="w-8 h-8 border-[3px] border-blue-400 border-t-transparent rounded-full animate-spin" />
          <span className="text-sm">데이터 로딩 중...</span>
        </div>
      </div>
    );
  }

  if (!data) return null;

  const activeStats   = data[STATS_KEY_MAP[activeTab]];
  const filteredGames =
    PREMADE_COUNT_MAP[activeTab] === null
      ? (data.games ?? [])
      : (data.games ?? []).filter((g) => g.premadeCount === PREMADE_COUNT_MAP[activeTab]);

  return (
    <div className="min-h-screen bg-[#f0eff0]">
      <ProfileHeader nickname={data.nickname} onSearch={handleSearch} />
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="grid grid-cols-[300px_1fr] gap-4 items-start">
          <QueueStatsSidebar
            activeTab={activeTab}
            onTabChange={setActiveTab}
            stats={activeStats}
          />
          <MatchHistoryList games={filteredGames} />
        </div>
      </div>
    </div>
  );
}
