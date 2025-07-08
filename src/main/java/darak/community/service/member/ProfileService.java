package darak.community.service.member;

import darak.community.service.member.request.ProfileUpdateServiceRequest;
import darak.community.service.member.response.ProfileResponse;
import darak.community.service.member.response.ProfileStatsResponse;

public interface ProfileService {
    ProfileResponse getProfile(Long memberId);

    ProfileStatsResponse getProfileStats(Long memberId);

    void updateProfile(Long memberId, ProfileUpdateServiceRequest request);
} 