const TABS = ['전체', '솔로큐', '듀오큐', '스쿼드큐'];

function WinRateCircle({ winRate, wins, total }) {
  const radius = 42;
  const circumference = 2 * Math.PI * radius;
  const dashOffset = circumference - (winRate / 100) * circumference;

  return (
    <div className="relative flex items-center justify-center w-28 h-28">
      <svg width="112" height="112" className="-rotate-90">
        <circle
          cx="56"
          cy="56"
          r={radius}
          fill="none"
          stroke="#e5e7eb"
          strokeWidth="9"
        />
        <circle
          cx="56"
          cy="56"
          r={radius}
          fill="none"
          stroke="#3b82f6"
          strokeWidth="9"
          strokeDasharray={circumference}
          strokeDashoffset={dashOffset}
          strokeLinecap="round"
          style={{ transition: 'stroke-dashoffset 0.6s ease' }}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-2xl font-bold text-gray-800 leading-none">
          {winRate}%
        </span>
        <span className="text-xs text-gray-400 mt-1">
          {wins}승 / {total}게임
        </span>
      </div>
    </div>
  );
}

export default function QueueStatsSidebar({ activeTab, onTabChange, stats }) {
  return (
    <div className="bg-white rounded-xl shadow-sm overflow-hidden">
      <div className="grid grid-cols-4 border-b border-gray-100">
        {TABS.map((tab) => (
          <button
            key={tab}
            onClick={() => onTabChange(tab)}
            className={`py-3 text-xs font-semibold transition-colors ${
              activeTab === tab
                ? 'text-blue-500 border-b-2 border-blue-500'
                : 'text-gray-400 hover:text-gray-600 hover:bg-gray-50'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      {stats && stats.totalGames > 0 ? (
        <div className="p-5">
          <div className="flex justify-center mb-5">
            <WinRateCircle
              winRate={stats.winRate}
              wins={stats.totalWins}
              total={stats.totalGames}
            />
          </div>

          <div className="space-y-3">
            <StatRow label="평균 순위" value={`#${stats.avgRank}`} />
            <StatRow label="평균 킬" value={`${stats.avgKills}킬`} />
            <StatRow label="평균 딜량" value={stats.avgDamage.toLocaleString()} />
          </div>

          <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-2 gap-2 text-center">
            <div>
              <div className="text-2xl font-bold text-blue-500">{stats.totalWins}</div>
              <div className="text-xs text-gray-400 mt-0.5">우승</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-600">
                {stats.totalGames - stats.totalWins}
              </div>
              <div className="text-xs text-gray-400 mt-0.5">기타</div>
            </div>
          </div>
        </div>
      ) : (
        <div className="p-10 text-center text-gray-400 text-sm">
          해당 큐의 게임 기록이 없습니다
        </div>
      )}
    </div>
  );
}

function StatRow({ label, value }) {
  return (
    <div className="flex items-center justify-between text-sm">
      <span className="text-gray-400">{label}</span>
      <span className="font-semibold text-gray-700">{value}</span>
    </div>
  );
}
