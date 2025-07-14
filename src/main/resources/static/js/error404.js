// Tailwind CSS의 다크모드 설정 (클래스 기반)
tailwind.config = {
  darkMode: 'class'
};

// 404 페이지 메인 JavaScript
document.addEventListener('DOMContentLoaded', function () {
  // 다크모드 토글 기능
  const darkModeToggle = document.getElementById('darkModeToggle');
  const darkModeToggleMobile = document.getElementById('darkModeToggleMobile');
  const menuToggle = document.getElementById('menuToggle');
  const mobileMenu = document.getElementById('mobileMenu');
  const closeMenu = document.getElementById('closeMenu');

  // 다크모드 초기 설정
  function initialTheme() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
      document.documentElement.classList.add('dark');
      updateThemeIcons(true);
    } else {
      document.documentElement.classList.remove('dark');
      updateThemeIcons(false);
    }
  }

  // 다크모드 토글
  function toggleDarkMode() {
    const isDark = document.documentElement.classList.toggle('dark');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    updateThemeIcons(isDark);
  }

  // 아이콘 업데이트
  function updateThemeIcons(isDark) {
    const icons = [
      darkModeToggle?.querySelector('i'),
      darkModeToggleMobile?.querySelector('i')
    ].filter(Boolean);

    icons.forEach(icon => {
      if (isDark) {
        icon.className = 'fas fa-sun text-yellow-400';
      } else {
        icon.className = 'fas fa-moon text-gray-700';
      }
    });
  }

  // 이벤트 리스너
  darkModeToggle?.addEventListener('click', toggleDarkMode);
  darkModeToggleMobile?.addEventListener('click', toggleDarkMode);

  // 모바일 메뉴 토글
  menuToggle?.addEventListener('click', function () {
    mobileMenu.classList.remove('translate-x-full');
  });

  closeMenu?.addEventListener('click', function () {
    mobileMenu.classList.add('translate-x-full');
  });

  // 모바일 메뉴 외부 클릭 시 닫기
  document.addEventListener('click', function (e) {
    if (!mobileMenu.contains(e.target) && !menuToggle.contains(e.target)) {
      mobileMenu.classList.add('translate-x-full');
    }
  });

  // 초기 테마 설정
  initialTheme();
}); 