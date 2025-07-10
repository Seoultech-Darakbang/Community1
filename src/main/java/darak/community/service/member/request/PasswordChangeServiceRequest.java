package darak.community.service.member.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordChangeServiceRequest {

    private Long memberId;
    private String oldPassword;
    private String newPassword;

}
