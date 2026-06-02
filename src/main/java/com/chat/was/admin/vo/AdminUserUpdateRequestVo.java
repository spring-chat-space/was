package com.chat.was.admin.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 VO.
 * 관리자가 다른 사용자 정보를 수정할 때 전달되는 요청 바디.
 */
@Getter
@NoArgsConstructor
public class AdminUserUpdateRequestVo {

    /** 수정할 이름 */
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String adminName;

    /** 수정할 이메일 */
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    /** 수정할 전화번호 */
    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(
        regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
        message = "올바른 휴대폰 번호 형식이 아닙니다."
    )
    private String phoneNumber;

    /** 수정할 권한 (ROLE_USER / ROLE_ADMIN) */
    @NotBlank(message = "권한은 필수 입력값입니다.")
    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN)$", message = "권한 값이 올바르지 않습니다.")
    private String role;

    /** 계정 활성화 여부 (Y / N) */
    @NotBlank(message = "활성화 여부는 필수 입력값입니다.")
    @Pattern(regexp = "^(Y|N)$", message = "활성화 여부 값이 올바르지 않습니다.")
    private String useYn;
}
