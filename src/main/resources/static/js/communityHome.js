// 커뮤니티 홈페이지 즐겨찾기 관리 JavaScript
document.addEventListener('DOMContentLoaded', function () {
  const manageFavoritesBtn = document.getElementById('manageFavoritesBtn');
  const favoritesModal = document.getElementById('favoritesModal');
  const closeFavoritesModal = document.getElementById('closeFavoritesModal');
  const favoriteToggleBtns = document.querySelectorAll('.favorite-toggle-btn');
  const favoritesList = document.getElementById('favoritesList');
  const noFavoritesMessage = document.getElementById('noFavoritesMessage');

  // 모달 열기
  manageFavoritesBtn.addEventListener('click', function () {
    favoritesModal.classList.remove('hidden');
    loadFavoriteStatus();
  });

  // 모달 닫기
  closeFavoritesModal.addEventListener('click', function () {
    favoritesModal.classList.add('hidden');
  });

  // 모달 외부 클릭 시 닫기
  favoritesModal.addEventListener('click', function (e) {
    if (e.target === favoritesModal) {
      favoritesModal.classList.add('hidden');
    }
  });

  // 모든 게시판의 즐겨찾기 상태 로드
  function loadFavoriteStatus() {
    favoriteToggleBtns.forEach(btn => {
      fetch(`/api/favorites/${btn.dataset.boardId}/status`)
        .then(res => res.json())
        .then(json => {
          if (json.success) {
            const isFav = json.data.favorite;
            updateFavoriteButton(btn, isFav);
          } else {
            console.error('즐겨찾기 상태 조회 실패:', json.message);
            updateFavoriteButton(btn, false);
          }
        })
        .catch(error => {
          console.error('네트워크 오류:', error);
          updateFavoriteButton(btn, false);
        });
    });
  }

  // 즐겨찾기 버튼 상태 업데이트
  function updateFavoriteButton(btn, isFavorite) {
    const textSpan = btn.querySelector('.favorite-btn-text');

    if (isFavorite) {
      btn.className = 'favorite-toggle-btn px-3 py-1 rounded text-sm font-medium transition-colors bg-yellow-500 text-white hover:bg-yellow-600';
      textSpan.textContent = '즐겨찾기 해제';
    } else {
      btn.className = 'favorite-toggle-btn px-3 py-1 rounded text-sm font-medium transition-colors bg-gray-200 text-gray-700 hover:bg-gray-300';
      textSpan.textContent = '즐겨찾기 추가';
    }

    btn.setAttribute('data-is-favorite', isFavorite);
  }

  // 즐겨찾기 목록에 새 항목 추가
  function addFavoriteToList(boardId, boardName) {
    // 이미 존재하는지 확인
    const existingItem = favoritesList.querySelector(`[data-board-id="${boardId}"]`);
    if (existingItem) {
      return; // 이미 존재하면 추가하지 않음
    }

    // 새 즐겨찾기 항목 생성
    const newFavoriteItem = document.createElement('a');
    newFavoriteItem.href = `/community/boards/${boardId}`;
    newFavoriteItem.setAttribute('data-board-id', boardId);
    newFavoriteItem.className = 'favorite-item inline-block px-4 py-2 bg-blue-500 dark:bg-blue-600 text-white rounded-md hover:bg-blue-600 dark:hover:bg-blue-700 transition-all duration-300 opacity-0 scale-95';
    newFavoriteItem.textContent = boardName;

    // 즐겨찾기 목록에 추가
    favoritesList.appendChild(newFavoriteItem);

    // 애니메이션 효과
    setTimeout(() => {
      newFavoriteItem.classList.remove('opacity-0', 'scale-95');
      newFavoriteItem.classList.add('opacity-100', 'scale-100');
    }, 50);

    // 빈 메시지 숨기기
    updateEmptyMessage();
  }

  // 즐겨찾기 목록에서 항목 제거
  function removeFavoriteFromList(boardId) {
    const favoriteItem = favoritesList.querySelector(`[data-board-id="${boardId}"]`);
    if (favoriteItem) {
      // 페이드아웃 애니메이션
      favoriteItem.classList.add('opacity-0', 'scale-95');

      // 애니메이션 완료 후 DOM에서 제거
      setTimeout(() => {
        favoriteItem.remove();
        updateEmptyMessage();
      }, 300);
    }
  }

  // 빈 메시지 표시/숨김 처리
  function updateEmptyMessage() {
    const favoriteItems = favoritesList.querySelectorAll('.favorite-item');
    const visibleItems = Array.from(favoriteItems).filter(item => !item.classList.contains('opacity-0'));

    if (visibleItems.length === 0) {
      noFavoritesMessage.classList.remove('hidden');
      noFavoritesMessage.classList.add('block');
    } else {
      noFavoritesMessage.classList.remove('block');
      noFavoritesMessage.classList.add('hidden');
    }
  }

  // 즐겨찾기 토글 이벤트
  favoriteToggleBtns.forEach(btn => {
    btn.addEventListener('click', function () {
      const boardId = this.getAttribute('data-board-id');
      const boardName = this.getAttribute('data-board-name');
      const isFavorite = this.getAttribute('data-is-favorite') === 'true';

      // 버튼 비활성화
      this.disabled = true;
      const textSpan = this.querySelector('.favorite-btn-text');
      const originalText = textSpan.textContent;
      textSpan.textContent = '처리중...';

      const url = `/api/favorites/${boardId}`;
      const method = isFavorite ? 'DELETE' : 'POST';

      fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        }
      })
        .then(response => response.json())
        .then(data => {
          if (data.success) {
            // 모달 버튼 상태 업데이트
            updateFavoriteButton(this, data.data.favorite);

            // 즐겨찾기 목록 동적 업데이트
            if (data.data.favorite) {
              addFavoriteToList(boardId, boardName);
            } else {
              removeFavoriteFromList(boardId);
            }

            showToast(data.message, 'success');
          } else {
            showToast(data.message, 'error');
            updateFavoriteButton(this, isFavorite); // 원래 상태로 되돌리기
          }
        })
        .catch(error => {
          console.error('네트워크 오류:', error);
          showToast('네트워크 오류가 발생했습니다.', 'error');
          updateFavoriteButton(this, isFavorite); // 원래 상태로 되돌리기
        })
        .finally(() => {
          this.disabled = false;
        });
    });
  });

  // 토스트 메시지 표시
  function showToast(message, type = 'info') {
    // 기존 토스트 제거
    const existingToast = document.getElementById('toast');
    if (existingToast) {
      existingToast.remove();
    }

    // 토스트 생성
    const toast = document.createElement('div');
    toast.id = 'toast';
    toast.className = `fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 ${type === 'success' ? 'bg-green-500 text-white' :
      type === 'error' ? 'bg-red-500 text-white' :
        'bg-blue-500 text-white'
      }`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // 슬라이드 인 애니메이션
    setTimeout(() => {
      toast.classList.add('transform', 'translate-x-0');
    }, 50);

    // 3초 후 자동 제거
    setTimeout(() => {
      if (toast && toast.parentNode) {
        toast.classList.add('opacity-0', 'transform', 'translate-x-full');
        setTimeout(() => {
          toast.remove();
        }, 300);
      }
    }, 3000);
  }

  // 페이지 로드 시 빈 메시지 상태 확인
  updateEmptyMessage();
}); 