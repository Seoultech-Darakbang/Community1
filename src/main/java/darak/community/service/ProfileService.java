package darak.community.service;

import darak.community.dto.MyCommentDto;
import darak.community.dto.MyPostDto;
import darak.community.dto.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
    ProfileDto getProfile(Long memberId);

    Page<MyPostDto> getMyPosts(Long memberId, Pageable pageable);

    Page<MyCommentDto> getMyComments(Long memberId, Pageable pageable);

    Page<MyPostDto> getLikedPosts(Long memberId, Pageable pageable);

    Page<MyCommentDto> getLikedComments(Long memberId, Pageable pageable);

    Page<MyPostDto> searchMyPosts(Long memberId, String keyword, String boardName, Pageable pageable);

    Page<MyCommentDto> searchMyComments(Long memberId, String keyword, String boardName, Pageable pageable);

    void updateProfile(Long memberId, String email, String phone);
} 