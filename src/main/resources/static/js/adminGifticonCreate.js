// 관리자 기프티콘 생성 - 실시간 미리보기 및 폼 유효성 검사
document.addEventListener('DOMContentLoaded', function () {
  const titleInput = document.getElementById('title');
  const brandInput = document.getElementById('brand');
  const descriptionInput = document.getElementById('description');
  const imageUrlInput = document.getElementById('imageUrl');
  const quantityInput = document.getElementById('totalQuantity');

  const previewTitle = document.getElementById('previewTitle');
  const previewBrand = document.getElementById('previewBrand');
  const previewDescription = document.getElementById('previewDescription');
  const previewImage = document.getElementById('previewImage');
  const previewIcon = document.getElementById('previewIcon');
  const previewQuantity = document.getElementById('previewQuantity');

  function updatePreview() {
    previewTitle.textContent = titleInput.value || '기프티콘 제목';
    previewBrand.textContent = brandInput.value || '브랜드';
    previewDescription.textContent = descriptionInput.value || '기프티콘 설명';
    previewQuantity.textContent = quantityInput.value || '0';

    if (imageUrlInput.value) {
      previewImage.src = imageUrlInput.value;
      previewImage.classList.remove('hidden');
      previewIcon.classList.add('hidden');
    } else {
      previewImage.classList.add('hidden');
      previewIcon.classList.remove('hidden');
    }
  }

  titleInput.addEventListener('input', updatePreview);
  brandInput.addEventListener('input', updatePreview);
  descriptionInput.addEventListener('input', updatePreview);
  imageUrlInput.addEventListener('input', updatePreview);
  quantityInput.addEventListener('input', updatePreview);

  // 이미지 로드 에러 처리
  previewImage.addEventListener('error', function () {
    previewImage.classList.add('hidden');
    previewIcon.classList.remove('hidden');
  });

  // 초기 날짜 설정
  const now = new Date();
  const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);

  const formatDateTime = (date) => {
    return date.toISOString().slice(0, 16);
  };

  document.getElementById('startTime').value = formatDateTime(now);
  document.getElementById('endTime').value = formatDateTime(tomorrow);

  // 폼 제출 전 유효성 검사
  const form = document.querySelector('form');

  form.addEventListener('submit', function (e) {
    const startTime = new Date(document.getElementById('startTime').value);
    const endTime = new Date(document.getElementById('endTime').value);

    if (startTime >= endTime) {
      e.preventDefault();
      alert('종료 시간은 시작 시간보다 나중이어야 합니다.');
      return;
    }

    if (startTime < new Date()) {
      if (!confirm('시작 시간이 현재 시간보다 이전입니다. 계속하시겠습니까?')) {
        e.preventDefault();
        return;
      }
    }

    console.log('=== 폼 제출 시작 ===');

    const formData = new FormData(form);
    console.log('폼 데이터:');
    for (let [key, value] of formData.entries()) {
      console.log(`  ${key}: ${value}`);
    }

    // 필수 필드 체크
    const title = document.getElementById('title').value;
    const brand = document.getElementById('brand').value;
    const description = document.getElementById('description').value;
    const totalQuantity = document.getElementById('totalQuantity').value;
    const startTime_value = document.getElementById('startTime').value;
    const endTime_value = document.getElementById('endTime').value;

    console.log('필수 필드 체크:');
    console.log('  title:', title);
    console.log('  brand:', brand);
    console.log('  description:', description);
    console.log('  totalQuantity:', totalQuantity);
    console.log('  startTime:', startTime_value);
    console.log('  endTime:', endTime_value);

    if (!title || !brand || !description || !totalQuantity || !startTime_value || !endTime_value) {
      console.error('필수 필드가 비어있음!');
      e.preventDefault();
      alert('모든 필수 필드를 입력해주세요.');
      return false;
    }

    if (parseInt(totalQuantity) <= 0) {
      console.error('totalQuantity가 0 이하:', totalQuantity);
      e.preventDefault();
      alert('총 수량은 1 이상이어야 합니다.');
      return false;
    }

    console.log('폼 검증 통과, 제출 진행...');
  });
}); 