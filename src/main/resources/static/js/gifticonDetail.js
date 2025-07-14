// 기프티콘 상세 페이지 JavaScript
console.log('Gifticon detail scripts loaded');

document.addEventListener('DOMContentLoaded', function () {
  console.log('Gifticon detail DOMContentLoaded');

  const claimButton = document.querySelector('.claim-btn');
  if (claimButton) {
    console.log('Claim button found:', claimButton);
    claimButton.addEventListener('click', function () {
      console.log('Claim button clicked');
      claimGifticon(this);
    });
  } else {
    console.log('Claim button not found');
  }
});

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

  console.log('Sending fetch request to:', `/api/v1/community/gifticons/${gifticonId}/claim`);

  fetch(`/api/v1/community/gifticons/${gifticonId}/claim`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    }
  })
    .then(response => {
      console.log('Response received:', response);
      return response.json();
    })
    .then(data => {
      console.log('Response data:', data);
      if (data.success && data.data && data.data.id) {
        showGifticonAlert('success', '기프티콘을 성공적으로 수령했습니다!', function () {
          window.location.href = '/community/gifticons/my';
        });
      } else {
        showGifticonAlert('error', data.message || '기프티콘 수령에 실패했습니다.');
        button.disabled = false;
        button.innerHTML = '<i class="fas fa-gift mr-2"></i>지금 바로 수령하기';
      }
    })
    .catch(error => {
      console.error('Error:', error);
      showGifticonAlert('error', '수령 중 오류가 발생했습니다.');
      button.disabled = false;
      button.innerHTML = '<i class="fas fa-gift mr-2"></i>지금 바로 수령하기';
    });
}

// 링크 복사
function copyLink() {
  navigator.clipboard.writeText(window.location.href).then(function () {
    showGifticonAlert('success', '링크가 클립보드에 복사되었습니다.');
  }, function (err) {
    console.error('복사 실패: ', err);
    showGifticonAlert('error', '링크 복사에 실패했습니다.');
  });
}

// 카카오톡 공유
function shareToKakao() {
  showGifticonAlert('info', '카카오톡 공유 기능은 개발 중입니다.');
}

// 알림 표시
function showGifticonAlert(type, message, callback) {
  console.log('showGifticonAlert called:', type, message);
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