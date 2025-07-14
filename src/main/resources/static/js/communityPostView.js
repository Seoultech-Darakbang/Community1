// 커뮤니티 게시글 보기 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function () {
  /* ===== 게시글 좋아요 ===== */
  const likeButton = document.getElementById('likeButton');
  if (likeButton) {
    likeButton.addEventListener('click', async function () {
      const postId = this.dataset.postId;
      const isLiked = this.dataset.liked === 'true';
      const url = `/api/posts/${postId}/like`;
      const method = isLiked ? 'DELETE' : 'POST';

      // 버튼 비활성화
      this.disabled = true;

      try {
        const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' } });
        const data = await res.json();

        // 서버 응답 로깅 추가
        console.log('=== 게시글 좋아요 서버 응답 ===');
        console.log('Full response data:', data);
        console.log('data.success:', data.success);
        console.log('data.data:', data.data);
        console.log('data.data.isLiked:', data.data?.isLiked);
        console.log('data.data.likeCount:', data.data?.likeCount);
        console.log('Original isLiked state:', isLiked);

        if (data && data.success) {
          // 서버 응답 후에만 UI 업데이트
          // data.data.liked를 사용 (isLiked가 아니라)
          console.log('About to call togglePostLikeUI with:', data.data.liked);
          togglePostLikeUI(this, data.data.liked);
          document.querySelector('.post-like-count').textContent = data.data.likeCount;
          showToast(data.message, 'success');
        } else {
          showToast(data?.message ?? '오류가 발생했습니다.', 'error');
        }
      } catch (e) {
        console.error(e);
        showToast('네트워크 오류가 발생했습니다.', 'error');
      } finally {
        this.disabled = false;
      }
    });
  }

  function togglePostLikeUI(btn, liked) {
    console.log('=== togglePostLikeUI 함수 시작 ===');
    console.log('받은 liked 파라미터:', liked, typeof liked);
    console.log('버튼 현재 dataset.liked:', btn.dataset.liked);
    console.log('버튼 HTML before:', btn.innerHTML);
    console.log('버튼 클래스 before:', btn.className);

    // 이전 상태 저장
    const previousLiked = btn.dataset.liked === 'true';
    console.log('Previous liked state:', previousLiked);

    btn.dataset.liked = liked;
    console.log('Updated dataset.liked to:', btn.dataset.liked);

    // 기존 span 요소를 찾아서 텍스트만 업데이트
    const textSpan = btn.querySelector('span');
    console.log('Found span element:', textSpan);
    console.log('Span current text:', textSpan?.textContent);

    if (textSpan) {
      const newText = liked ? '좋아요 취소' : '좋아요';
      console.log('Setting text to:', newText);
      textSpan.textContent = newText;
      console.log('Span text after update:', textSpan.textContent);
    } else {
      // span이 없으면 innerHTML 전체 교체 (fallback)
      console.log('Span not found, using innerHTML fallback');
      btn.innerHTML = `<i class="fas fa-thumbs-up"></i> <span>${liked ? '좋아요 취소' : '좋아요'}</span>`;
    }

    // 클래스도 더 안전하게 토글
    console.log('Updating button classes...');
    btn.classList.remove('bg-blue-500', 'bg-red-500');
    const newClass = liked ? 'bg-red-500' : 'bg-blue-500';
    btn.classList.add(newClass);

    console.log('Added class:', newClass);
    console.log('Button HTML after:', btn.innerHTML);
    console.log('Button classes after:', btn.className);
    console.log('=== togglePostLikeUI 함수 종료 ===');
  }

  /* ===== 댓글 좋아요 ===== */
  document.querySelectorAll('.comment-like-button').forEach(button => {
    button.addEventListener('click', async function () {
      const commentId = this.dataset.commentId;
      const isLiked = this.classList.contains('text-red-500');
      const url = `/api/comments/${commentId}/like`;
      const method = isLiked ? 'DELETE' : 'POST';

      // 버튼 비활성화
      this.disabled = true;

      try {
        const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json' } });
        const data = await res.json();

        if (data && data.success) {
          // 서버 응답 후에만 UI 업데이트
          toggleCommentLikeUI(this, data.data.isLiked, data.data.likeCount);
          showToast(data.message, 'success');
        } else {
          showToast(data?.message ?? '오류가 발생했습니다.', 'error');
        }
      } catch (e) {
        console.error(e);
        showToast('네트워크 오류가 발생했습니다.', 'error');
      } finally {
        this.disabled = false;
      }
    });
  });

  function toggleCommentLikeUI(btn, liked, likeCount = null) {
    /* 텍스트 */
    const textSpan = btn.querySelector('span:first-of-type');
    if (textSpan) textSpan.textContent = liked ? '좋아요 취소' : '좋아요';

    /* 카운트 */
    const cntSpan = btn.querySelector('span:last-of-type');
    if (cntSpan) {
      if (likeCount !== null) {
        cntSpan.textContent = likeCount;
      } else {
        const cur = parseInt(cntSpan.textContent) || 0;
        cntSpan.textContent = liked ? cur + 1 : Math.max(0, cur - 1);
      }
    }

    /* 색상 */
    btn.classList.toggle('text-red-500', liked);
    btn.classList.toggle('text-blue-500', !liked);
  }

  // 답글 작성 버튼 이벤트
  document.querySelectorAll('.reply-button').forEach(button => {
    button.addEventListener('click', function () {
      const commentId = this.getAttribute('data-comment-id');
      const replyForm = document.getElementById(`reply-form-${commentId}`);
      if (replyForm) {
        replyForm.classList.remove('hidden');
      }
    });
  });

  // 답글 작성 취소 버튼 이벤트
  document.querySelectorAll('.reply-cancel-button').forEach(button => {
    button.addEventListener('click', function () {
      const commentId = this.getAttribute('data-comment-id');
      const replyForm = document.getElementById(`reply-form-${commentId}`);
      if (replyForm) {
        replyForm.classList.add('hidden');
      }
    });
  });

  // Toast UI Viewer 초기화
  const viewerElement = document.querySelector('#viewer');
  if (viewerElement && window.toastui) {
    toastui.Editor.factory({
      el: viewerElement,
      viewer: true,
      initialValue: viewerElement.dataset.content || '',
      plugins: window.toastui.Editor.plugin ? [window.toastui.Editor.plugin.codeSyntaxHighlight] : [],
      height: 'auto'
    });
  }
});

// 토스트 메시지 표시 함수
function showToast(message, type = 'info') {
  // 기존 토스트가 있으면 제거
  const existingToast = document.getElementById('toast');
  if (existingToast) {
    existingToast.remove();
  }

  // 토스트 생성
  const toast = document.createElement('div');
  toast.id = 'toast';
  toast.className = `fixed top-4 right-4 px-4 py-2 rounded shadow-lg z-50 ${type === 'success' ? 'bg-green-500 text-white' :
    type === 'error' ? 'bg-red-500 text-white' :
      'bg-blue-500 text-white'
    }`;
  toast.textContent = message;

  document.body.appendChild(toast);

  // 3초 후 자동 제거
  setTimeout(() => {
    if (toast && toast.parentNode) {
      toast.remove();
    }
  }, 3000);
} 