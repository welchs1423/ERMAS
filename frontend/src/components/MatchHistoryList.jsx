import MatchCard from './MatchCard';

export default function MatchHistoryList({ games }) {
  if (!games || games.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-sm p-12 text-center text-gray-400 text-sm">
        해당 큐의 게임 기록이 없습니다
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-2">
      {games.map((game) => (
        <MatchCard key={game.gameId} game={game} />
      ))}
    </div>
  );
}
