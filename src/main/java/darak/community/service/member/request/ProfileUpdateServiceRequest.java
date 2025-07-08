package darak.community.service.member.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileUpdateServiceRequest {

    private final String name;
    private final String email;
    private final String phone;
    private final LocalDate birth;

    @Builder
    private ProfileUpdateServiceRequest(String name, String email, String phone,
                                        LocalDate birth) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birth = birth;
    }

}