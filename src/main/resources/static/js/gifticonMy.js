// 내 기프티콘 페이지 JavaScript

// 클립보드 복사
function copyToClipboard(button) {
  const code = button.dataset.code;
  navigator.clipboard.writeText(code).then(function () {
    showAlert('success', '기프티콘 코드가 복사되었습니다!');
  }, function (err) {
    showAlert('error', '복사에 실패했습니다.');
  });
}

// 기프티콘 사용
function useGifticon(button) {
  const gifticonCode = button.dataset.gifticonCode;

  if (!confirm('기프티콘을 사용하시겠습니까? (사용 후 되돌릴 수 없습니다)')) {
    return;
  }

  button.disabled = true;
  button.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>사용 중...';

  fetch('/api/v1/community/gifticons/use', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `gifticonCode=${gifticonCode}`
  })
    .then(response => response.text())
    .then(message => {
      showAlert('success', message);
      setTimeout(() => {
        window.location.reload();
      }, 1500);
    })
    .catch(error => {
      console.error('Error:', error);
      showAlert('error', '사용 중 오류가 발생했습니다.');
      button.disabled = false;
      button.innerHTML = '<i class="fas fa-check mr-2"></i>사용하기';
    });
}

// 알림 표시
function showAlert(type, message) {
  const alertDiv = document.createElement('div');
  alertDiv.className = `fixed top-4 right-4 p-4 rounded-lg text-white z-50 ${type === 'success' ? 'bg-green-500' : 'bg-red-500'
    }`;
  alertDiv.innerHTML = `
                    <div class="flex items-center">
                        <i class="fas ${type === 'success' ? 'fa-check' : 'fa-times'} mr-2"></i>
                        ${message}
                    </div>
                `;

  document.body.appendChild(alertDiv);

  setTimeout(() => {
    alertDiv.remove();
  }, 3000);
}

// 애니메이션 효과
document.addEventListener('DOMContentLoaded', function () {
  // 섹션 내부(.transform)만 선택 → 헤더 transform 유지
  const cards = document.querySelectorAll('section .transform');
  cards.forEach((card, index) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(20px)';

    setTimeout(() => {
      card.style.transition = 'all 0.6s ease';
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, index * 100);
  });
}); 