// Home 페이지 JavaScript
document.addEventListener('DOMContentLoaded', () => {
  // 다크모드 관련 함수
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

  // 다크모드 토글 이벤트 리스너 등록
  const darkModeToggle = document.getElementById('darkModeToggle');
  const darkModeToggleMobile = document.getElementById('darkModeToggleMobile');

  if (darkModeToggle) {
    darkModeToggle.addEventListener('click', toggleDarkMode);
  }
  if (darkModeToggleMobile) {
    darkModeToggleMobile.addEventListener('click', toggleDarkMode);
  }

  // 모바일 메뉴 토글 처리
  const mobileMenu = document.getElementById('mobileMenu');
  const menuToggle = document.getElementById('menuToggle');
  const closeMenu = document.getElementById('closeMenu');

  if (menuToggle && mobileMenu) {
    menuToggle.addEventListener('click', () => {
      mobileMenu.classList.toggle('translate-x-full');
    });
  }
  if (closeMenu && mobileMenu) {
    closeMenu.addEventListener('click', () => {
      mobileMenu.classList.add('translate-x-full');
    });
  }

  // 슬라이드 기능: 3초 간격으로 자동 전환
  const slides = document.querySelectorAll('.slide');
  if (slides.length > 0) {
    let currentSlide = 0;
    setInterval(() => {
      slides[currentSlide].classList.add('hidden');
      currentSlide = (currentSlide + 1) % slides.length;
      slides[currentSlide].classList.remove('hidden');
    }, 3000);
  }

  // 스크롤 시 섹션 애니메이션 처리
  const faders = document.querySelectorAll('.fade-in-section');
  const appearOptions = {
    threshold: 0.1,
    rootMargin: "0px 0px -50px 0px"
  };

  const appearOnScroll = new IntersectionObserver(function (entries, observer) {
    entries.forEach(entry => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add('visible');
      observer.unobserve(entry.target);
    });
  }, appearOptions);

  faders.forEach(fader => {
    appearOnScroll.observe(fader);
  });

  initialTheme();
}); 