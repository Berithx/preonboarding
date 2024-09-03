package com.berith.preonboarding.user.dto;

import com.berith.preonboarding.user.Authority;
import com.berith.preonboarding.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private String username;
    private String nickname;
    private List<Authority> authorities;

    public SignupResponse(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = user.getAuthorities();
    }
}
