package com.berith.preonboarding.user;

import com.berith.preonboarding.common.exception.CustomException;
import com.berith.preonboarding.user.dto.SignupRequest;
import com.berith.preonboarding.user.dto.SignupResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        this.checkUsernameAndNickname(request);
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .authorities(List.of(Authority.ROLE_USER))
                .build();

        userRepository.save(user);

        return new SignupResponse(user);
    }

    private void checkUsernameAndNickname(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 사용중인 username입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 사용중인 nickname입니다.");
        }
    }
}
