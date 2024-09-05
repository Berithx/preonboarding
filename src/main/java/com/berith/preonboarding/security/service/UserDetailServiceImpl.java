package com.berith.preonboarding.security.service;

import com.berith.preonboarding.common.exception.CustomException;
import com.berith.preonboarding.security.principal.UserPrincipal;
import com.berith.preonboarding.user.User;
import com.berith.preonboarding.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.")
        );
        return new UserPrincipal(user);
    }
}
