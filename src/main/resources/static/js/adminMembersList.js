// 관리자 회원 목록 - 등급 변경 확인
document.addEventListener('DOMContentLoaded', function () {
  // 등급 변경 확인
  document.querySelectorAll('select[name="grade"]').forEach(select => {
    select.addEventListener('change', function (e) {
      const memberName = this.closest('tr').querySelector('td:first-child .text-sm').textContent;
      const newGrade = this.value;

      if (!confirm(`${memberName} 회원의 등급을 ${newGrade}로 변경하시겠습니까?`)) {
        // 취소시 원래 값으로 되돌리기
        this.value = this.querySelector('option[selected]').value;
        return false;
      }
    });
  });
}); 