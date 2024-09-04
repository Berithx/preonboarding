package com.berith.preonboarding.user;

import com.berith.preonboarding.common.exception.CustomException;
import com.berith.preonboarding.security.principal.UserPrincipal;
import com.berith.preonboarding.security.service.JwtProvider;
import com.berith.preonboarding.user.dto.SignRequest;
import com.berith.preonboarding.user.dto.SignResponse;
import com.berith.preonboarding.user.dto.SignupRequest;
import com.berith.preonboarding.user.dto.SignupResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        this.checkUsernameAndNickname(request);
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .authorities(new ArrayList<>(Arrays.asList(Authority.ROLE_USER)))
                .build();

        userRepository.save(user);

        return new SignupResponse(user);
    }

    @Transactional
    public SignResponse sign(SignRequest request) {
        log.info("인증 객체 생성 전");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword(),
                        null
                )
        );

        log.info("인증 객체 생성 후");

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = findByUsername(userPrincipal.getUsername());
        log.info("사용자 객체 확보");

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken();
        log.info("토큰 생성");

        user.updateRefreshToken(refreshToken);
        log.info("토큰 업데이트");

        return new SignResponse(accessToken);
    }

    private void checkUsernameAndNickname(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 사용중인 username입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 사용중인 nickname입니다.");
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.")
        );
    }
}
