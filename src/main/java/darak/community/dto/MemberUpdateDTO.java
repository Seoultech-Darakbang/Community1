package darak.community.dto;


import darak.community.domain.MemberGrade;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberUpdateDTO { // Required for transporting Controller to Service Layer
    @NotEmpty
    private Long id;
    private String password;

    @NotEmpty
    private String name;

    private String phone;

    private LocalDate birth;

    private String email;
}
