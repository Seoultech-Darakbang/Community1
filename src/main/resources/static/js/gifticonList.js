// 기프티콘 목록 페이지 JavaScript
console.log('Gifticon list scripts loaded');

document.addEventListener('DOMContentLoaded', function () {
  console.log('Gifticon list DOMContentLoaded');

  // 탭 버튼
  const activeTab = document.getElementById('activeTab');
  const myTab = document.getElementById('myTab');

  if (activeTab) {
    activeTab.addEventListener('click', showActiveTab);
  }
  if (myTab) {
    myTab.addEventListener('click', showMyTab);
  }

  // 수령 버튼들
  const claimButtons = document.querySelectorAll('.claim-btn');
  console.log('Found claim buttons:', claimButtons.length);
  claimButtons.forEach(button => {
    button.addEventListener('click', function () {
      console.log('Claim button clicked');
      claimGifticon(this);
    });
  });

  // 사용 버튼들
  const useButtons = document.querySelectorAll('.use-btn');
  console.log('Found use buttons:', useButtons.length);
  useButtons.forEach(button => {
    button.addEventListener('click', function () {
      console.log('Use button clicked');
      useGifticon(this);
    });
  });

  // 복사 버튼들
  const copyButtons = document.querySelectorAll('.copy-btn');
  console.log('Found copy buttons:', copyButtons.length);
  copyButtons.forEach(button => {
    button.addEventListener('click', function () {
      console.log('Copy button clicked');
      const code = this.dataset.code;
      copyToClipboard(code);
    });
  });
});

// 활성 탭 보기
function showActiveTab() {
  console.log('showActiveTab called');
  document.getElementById('activeTab').className = 'px-6 py-2 rounded-md text-sm font-medium bg-purple-600 text-white transition-all';
  const myTab = document.getElementById('myTab');
  if (myTab) {
    myTab.className = 'px-6 py-2 rounded-md text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-all';
  }

  document.getElementById('activeGifticons').classList.remove('hidden');
  const myGifticons = document.getElementById('myGifticons');
  if (myGifticons) {
    myGifticons.classList.add('hidden');
  }
}

// 내 기프티콘 탭 보기
function showMyTab() {
  console.log('showMyTab called');
  document.getElementById('activeTab').className = 'px-6 py-2 rounded-md text-sm font-medium text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white transition-all';
  document.getElementById('myTab').className = 'px-6 py-2 rounded-md text-sm font-medium bg-purple-600 text-white transition-all';

  document.getElementById('activeGifticons').classList.add('hidden');
  document.getElementById('myGifticons').classList.remove('hidden');
}

// 기프티콘 수령
function claimGifticon(button) {
  console.log('claimGifticon function called');
  const gifticonId = button.dataset.gifticonId;
  console.log('Gifticon ID:', gifticonId);

  if (!confirm('이 기프티콘을 수령하시겠습니까?')) {
    return;
  }

  button.disabled = true;
  button.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>수령 중...';

  fetch(`/api/v1/community/gifticons/${gifticonId}/claim`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    }
  })
    .then(response => response.json())
    .then(data => {
      console.log('Response data:', data);
      if (data.success && data.data && data.data.id) {
        showListAlert('success', '기프티콘을 성공적으로 수령했습니다!', function () {
          location.reload();
        });
      } else {
        showListAlert('error', data.message || '기프티콘 수령에 실패했습니다.');
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-hand-paper mr-2"></i>선착순 수령하기';
      }
    })
    .catch(error => {
      console.error('Error:', error);
      showListAlert('error', '수령 중 오류가 발생했습니다.');
      button.disabled = false;
      button.innerHTML = '<i class="fas fa-hand-paper mr-2"></i>선착순 수령하기';
    });
}

// 기프티콘 사용
function useGifticon(button) {
  console.log('useGifticon function called');
  const gifticonCode = button.dataset.gifticonCode;
  console.log('Gifticon Code:', gifticonCode);

  if (!confirm('이 기프티콘을 사용처리 하시겠습니까?\n사용 후에는 되돌릴 수 없습니다.')) {
    return;
  }

  button.disabled = true;
  button.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>처리 중...';

  // URL 파라미터로 전송
  const url = new URL('/api/v1/community/gifticons/use', window.location.origin);
  url.searchParams.append('gifticonCode', gifticonCode);

  fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    }
  })
    .then(response => response.json())
    .then(data => {
      console.log('Response data:', data);
      if (data.success) {
        showListAlert('success', '기프티콘을 사용처리했습니다!', function () {
          location.reload();
        });
      } else {
        showListAlert('error', data.message || '사용 처리 중 오류가 발생했습니다.');
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-check mr-2"></i>사용하기';
      }
    })
    .catch(error => {
      console.error('Error:', error);
      showListAlert('error', '사용 처리 중 오류가 발생했습니다.');
      button.disabled = false;
      button.innerHTML = '<i class="fas fa-check mr-2"></i>사용하기';
    });
}

// 클립보드 복사
function copyToClipboard(text) {
  console.log('copyToClipboard called with:', text);
  navigator.clipboard.writeText(text).then(function () {
    showListAlert('success', '기프티콘 코드가 클립보드에 복사되었습니다.');
  }, function (err) {
    console.error('복사 실패: ', err);
    showListAlert('error', '복사에 실패했습니다.');
  });
}

// 알림 표시
function showListAlert(type, message, callback) {
  console.log('showListAlert called:', type, message);
  const alertDiv = document.createElement('div');
  alertDiv.className = `fixed top-4 right-4 px-6 py-4 rounded-lg text-white z-50 transform transition-transform duration-300 translate-x-full shadow-lg`;

  if (type === 'success') {
    alertDiv.classList.add('bg-green-500');
  } else if (type === 'error') {
    alertDiv.classList.add('bg-red-500');
  } else if (type === 'info') {
    alertDiv.classList.add('bg-blue-500');
  }

  alertDiv.innerHTML = `
        <div class="flex items-center">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} mr-2"></i>
            <span>${message}</span>
        </div>
    `;

  document.body.appendChild(alertDiv);

  setTimeout(() => {
    alertDiv.classList.remove('translate-x-full');
  }, 100);

  setTimeout(() => {
    alertDiv.classList.add('translate-x-full');
    setTimeout(() => {
      document.body.removeChild(alertDiv);
      if (callback) callback();
    }, 300);
  }, 3000);
} 