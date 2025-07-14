// Layout 공통 JavaScript - 다크모드 초기화
// HTML 요소가 렌더링되기 전에 즉시 실행됨
(function () {
  if (localStorage.theme === 'dark') {
    document.documentElement.classList.add('dark');
    document.documentElement.style.background = '#111827'; // bg-gray-900
    document.documentElement.style.color = '#ffffff';
  }
  document.documentElement.classList.remove('no-js');
})();

// Tailwind CSS 다크모드 설정
if (typeof tailwind !== 'undefined') {
  tailwind.config = {
    darkMode: 'class'
  };
} 