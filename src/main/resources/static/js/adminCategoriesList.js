// 카테고리 삭제 함수
function deleteCategory(categoryId) {
  if (confirm('정말로 이 카테고리를 삭제하시겠습니까?')) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = `/admin/categories/${categoryId}/delete`;
    document.body.appendChild(form);
    form.submit();
  }
} 