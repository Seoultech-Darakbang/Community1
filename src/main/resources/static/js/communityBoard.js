// Tailwind CSS 다크모드 설정
tailwind.config = {
  darkMode: 'class'
};

// 다크모드 관련 함수 (데스크탑과 모바일 아이콘 모두 업데이트)
function updateDarkModeIcons() {
  const desktopIcon = document.getElementById('darkModeToggle')?.querySelector('i');
  const mobileIcon = document.getElementById('darkModeToggleMobile')?.querySelector('i');
  if (document.documentElement.classList.contains('dark')) {
    if (desktopIcon) desktopIcon.className = 'fas fa-sun text-yellow-400';
    if (mobileIcon) mobileIcon.className = 'fas fa-sun text-yellow-400';
  } else {
    if (desktopIcon) desktopIcon.className = 'fas fa-moon text-gray-700';
    if (mobileIcon) mobileIcon.className = 'fas fa-moon text-gray-700';
  }
}

function initialTheme() {
  if (localStorage.theme === 'dark') {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
  updateDarkModeIcons();
}

function toggleDarkMode() {
  if (document.documentElement.classList.contains('dark')) {
    document.documentElement.classList.remove('dark');
    localStorage.theme = 'light';
  } else {
    document.documentElement.classList.add('dark');
    localStorage.theme = 'dark';
  }
  updateDarkModeIcons();
}

// DOMContentLoaded 이벤트 리스너
document.addEventListener('DOMContentLoaded', function () {
  // 다크모드 토글 이벤트 리스너
  document.getElementById('darkModeToggle')?.addEventListener('click', toggleDarkMode);
  document.getElementById('darkModeToggleMobile')?.addEventListener('click', toggleDarkMode);

  // 모바일 메뉴 토글 처리
  const mobileMenu = document.getElementById('mobileMenu');
  document.getElementById('menuToggle')?.addEventListener('click', () => {
    mobileMenu.classList.toggle('translate-x-full');
  });
  document.getElementById('closeMenu')?.addEventListener('click', () => {
    mobileMenu.classList.add('translate-x-full');
  });

  // 스크롤 시 섹션 애니메이션 처리
  const faders = document.querySelectorAll('.fade-in-section');
  const appearOptions = {
    threshold: 0.1,
    rootMargin: "0px 0px -50px 0px"
  };

  const appearOnScroll = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add('visible');
      observer.unobserve(entry.target);
    });
  }, appearOptions);

  faders.forEach(fader => {
    appearOnScroll.observe(fader);
  });

  // 초기 테마 설정
  initialTheme();
}); 