package darak.community.web.dto;

import darak.community.service.member.request.PasswordChangeServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordForm {

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String newPassword;

    @NotEmpty
    private String newPasswordConfirm;

    public boolean isNewPasswordMatch() {
        return newPassword.equals(newPasswordConfirm);
    }

    public PasswordChangeServiceRequest toServiceRequest(Long memberId) {
        return new PasswordChangeServiceRequest(memberId, oldPassword, newPassword);
    }

}
