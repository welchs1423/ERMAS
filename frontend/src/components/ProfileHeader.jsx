import { useState } from 'react';

export default function ProfileHeader({ nickname, onSearch }) {
  const [inputValue, setInputValue] = useState('');
  const initial = nickname ? nickname.charAt(0).toUpperCase() : '?';

  const handleSubmit = (e) => {
    e.preventDefault();
    if (inputValue.trim()) onSearch(inputValue.trim());
  };

  return (
    <div className="bg-[#1e2025] py-8 border-b border-[#2e3038]">
      <div className="max-w-6xl mx-auto px-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-5">
            <div className="relative">
              <div className="w-20 h-20 rounded-full bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center text-white text-3xl font-bold shadow-lg">
                {initial}
              </div>
              <div className="absolute -bottom-1 -right-1 bg-[#3b82f6] text-white text-xs px-1.5 py-0.5 rounded-full font-bold">
                100
              </div>
            </div>

            <div>
              <div className="text-gray-400 text-xs mb-0.5">이터널 리턴</div>
              <div className="text-white text-2xl font-bold tracking-tight">
                {nickname ?? '유저 정보 없음'}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <form onSubmit={handleSubmit} className="flex gap-2">
              <input
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="닉네임 검색"
                className="bg-[#2a2d35] text-gray-200 border border-[#3e4049] rounded-md px-3 py-2 text-sm focus:outline-none focus:border-blue-500 placeholder-gray-500 w-44"
              />
              <button
                type="submit"
                className="px-4 py-2 bg-blue-500 hover:bg-blue-600 active:bg-blue-700 text-white text-sm font-medium rounded-md transition-colors shadow-sm"
              >
                검색
              </button>
            </form>
            <select className="bg-[#2a2d35] text-gray-300 border border-[#3e4049] rounded-md px-3 py-2 text-sm focus:outline-none focus:border-blue-500 cursor-pointer">
              <option value="27">시즌 27</option>
              <option value="26">시즌 26</option>
              <option value="25">시즌 25</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  );
}
