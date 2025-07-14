// 관리자 게시판 목록 - 게시판 삭제 함수
function deleteBoard(boardId) {
  if (confirm('정말로 이 게시판을 삭제하시겠습니까?\n게시글이 있는 게시판은 삭제할 수 없습니다.')) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = `/admin/boards/${boardId}/delete`;
    document.body.appendChild(form);
    form.submit();
  }
} 