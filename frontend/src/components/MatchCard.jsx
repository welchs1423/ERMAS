const QUEUE_LABEL = { 1: '솔로큐', 2: '듀오큐', 3: '스쿼드큐' };

const QUEUE_BADGE_CLASS = {
  1: 'bg-blue-100 text-blue-600',
  2: 'bg-purple-100 text-purple-600',
  3: 'bg-green-100 text-green-600',
};

const MATCHING_MODE_LABEL = { 2: '일반', 3: '랭크' };

// 1위: 파랑, 2~3위: 초록, 나머지: 회색
function getRankStyle(rank) {
  if (rank === 1) return { border: 'border-blue-500', bg: 'bg-blue-50', text: 'text-blue-500' };
  if (rank <= 3)  return { border: 'border-green-500', bg: 'bg-green-50', text: 'text-green-500' };
  return { border: 'border-gray-300', bg: 'bg-gray-50', text: 'text-gray-400' };
}

function formatDuration(seconds) {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m}분 ${String(s).padStart(2, '0')}초`;
}

function formatDate(dateStr) {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`;
}

export default function MatchCard({ game }) {
  const rankStyle = getRankStyle(game.gameRank);

  return (
    <div
      className={`flex items-center bg-white rounded-xl shadow-sm overflow-hidden border-l-4 transition-shadow hover:shadow-md ${rankStyle.border}`}
    >
      {/* 순위 영역 */}
      <div
        className={`w-16 flex-shrink-0 flex flex-col items-center justify-center self-stretch py-4 ${rankStyle.bg}`}
      >
        <span className={`text-2xl font-extrabold leading-none ${rankStyle.text}`}>
          #{game.gameRank}
        </span>
        {game.gameRank === 1 && (
          <span className="text-xs text-blue-400 font-semibold mt-1">우승</span>
        )}
        {game.gameRank >= 2 && game.gameRank <= 3 && (
          <span className="text-xs text-green-400 font-semibold mt-1">TOP3</span>
        )}
      </div>

      {/* 캐릭터 아이콘 (실제 이미지 미구현: 캐릭터 번호 표시) */}
      <div className="w-14 h-14 flex-shrink-0 mx-3 bg-gray-200 rounded-lg flex flex-col items-center justify-center border border-gray-300">
        <span className="text-xs text-gray-400 leading-none">No.</span>
        <span className="text-sm font-bold text-gray-600 leading-tight">
          {game.characterNum}
        </span>
      </div>

      {/* 게임 정보 */}
      <div className="flex-1 min-w-0 py-4 pr-2">
        <div className="flex items-center gap-2 mb-2">
          <span
            className={`text-xs px-2 py-0.5 rounded-full font-semibold ${
              QUEUE_BADGE_CLASS[game.premadeCount]
            }`}
          >
            {QUEUE_LABEL[game.premadeCount]}
          </span>
          <span className="text-xs px-2 py-0.5 rounded-full bg-gray-100 text-gray-500 font-medium">
            {MATCHING_MODE_LABEL[game.matchingMode] ?? game.matchingMode}
          </span>
          <span className="text-xs text-gray-400">{formatDate(game.startDtm)}</span>
        </div>

        <div className="flex items-baseline gap-1 text-sm">
          <span className="font-bold text-blue-600">{game.playerKill}킬</span>
          <span className="text-gray-300">/</span>
          <span className="text-gray-500">{game.playerAssistant}도움</span>
          <span className="text-gray-300 mx-1">·</span>
          <span className="text-gray-400 text-xs">{formatDuration(game.duration)}</span>
        </div>
      </div>

      {/* 딜량 */}
      <div className="flex-shrink-0 px-5 text-center">
        <div className="text-xs text-gray-400 mb-1">딜량</div>
        <div className={`text-sm font-bold ${rankStyle.text}`}>
          {game.damageToPlayer.toLocaleString()}
        </div>
      </div>

      {/* 아이템 슬롯 (데이터 없음: 빈 슬롯 6개) */}
      <div className="flex-shrink-0 flex items-center gap-1 px-4">
        {Array.from({ length: 6 }).map((_, i) => (
          <div
            key={i}
            className="w-8 h-8 bg-gray-200 rounded-md border border-gray-300"
          />
        ))}
      </div>
    </div>
  );
}
