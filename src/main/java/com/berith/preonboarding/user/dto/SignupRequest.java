package com.berith.preonboarding.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "username은 공백일 수 없습니다.")
    private String username;
    @NotBlank(message = "password는 공백일 수 없습니다.")
    private String password;
    @NotBlank(message = "nickname은 공백일 수 없습니다.")
    private String nickname;
}
