// 아이디 중복 검사를 위한 AJAX 함수
function checkDuplicateLoginId() {
  const loginIdInput = document.getElementById('loginId');
  const loginId = loginIdInput.value.trim();

  if (!loginId) {
    alert('아이디를 입력해주세요.');
    return;
  }

  fetch('/api/v1/members/new/confirmLoginId?loginId=' + encodeURIComponent(loginId), {
    method: 'GET'
  })
    .then(response => response.json())
    .then(result => {
      // 서버 응답 형식은 ApiResponse<T>로 { success, message, data }를 포함합니다.
      const feedbackElement = document.getElementById('loginIdFeedback');
      if (result.success === true) {
        // 사용 가능한 아이디일 경우
        feedbackElement.textContent = result.message;
        feedbackElement.style.color = 'green';
      } else {
        // 중복된 아이디인 경우
        feedbackElement.textContent = result.message;
        feedbackElement.style.color = 'red';
      }
    })
    .catch(error => {
      console.error('AJAX 요청 중 오류 발생:', error);
      const feedbackElement = document.getElementById('loginIdFeedback');
      feedbackElement.textContent = '서버 요청 중 오류가 발생했습니다.';
      feedbackElement.style.color = 'red';
    });
}

// DOMContentLoaded 이벤트에서 버튼의 클릭 이벤트 리스너를 등록합니다.
document.addEventListener('DOMContentLoaded', function () {
  const duplicateCheckButton = document.getElementById('duplicateCheckBtn');
  if (duplicateCheckButton) {
    duplicateCheckButton.addEventListener('click', function (event) {
      event.preventDefault(); // 기본 폼 제출을 방지
      checkDuplicateLoginId();
    });
  }
}); 