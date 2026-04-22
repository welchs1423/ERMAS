import { useState } from 'react';
import usePlayerStats from './hooks/usePlayerStats';
import ProfileHeader from './components/ProfileHeader';
import QueueStatsSidebar from './components/QueueStatsSidebar';
import MatchHistoryList from './components/MatchHistoryList';

// 탭 이름 -> premadeCount 매핑 (null = 전체)
const PREMADE_COUNT_MAP = {
  전체: null,
  솔로큐: 1,
  듀오큐: 2,
  스쿼드큐: 3,
};

// 탭 이름 -> PlayerStatsResponseDto 필드 매핑
const STATS_KEY_MAP = {
  전체: 'total',
  솔로큐: 'solo',
  듀오큐: 'duo',
  스쿼드큐: 'squad',
};

export default function App() {
  const [activeTab, setActiveTab] = useState('전체');
  const { data, loading, error } = usePlayerStats();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#f0eff0]">
        <div className="flex flex-col items-center gap-3 text-gray-500">
          <div className="w-8 h-8 border-3 border-blue-400 border-t-transparent rounded-full animate-spin" />
          <span className="text-sm">데이터 로딩 중...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#f0eff0]">
        <div className="bg-white rounded-xl p-8 shadow text-center">
          <div className="text-red-500 text-lg font-semibold mb-2">연결 실패</div>
          <div className="text-gray-500 text-sm">
            백엔드 서버(포트 8080)가 실행 중인지 확인하세요.
          </div>
        </div>
      </div>
    );
  }

  const activeStats = data?.[STATS_KEY_MAP[activeTab]];

  const filteredGames =
    PREMADE_COUNT_MAP[activeTab] === null
      ? (data?.games ?? [])
      : (data?.games ?? []).filter(
          (g) => g.premadeCount === PREMADE_COUNT_MAP[activeTab]
        );

  return (
    <div className="min-h-screen bg-[#f0eff0]">
      <ProfileHeader nickname={data?.nickname} />

      <div className="max-w-6xl mx-auto px-4 py-6">
        {/* 2단 그리드: 사이드바 30% / 매치 리스트 70% */}
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
