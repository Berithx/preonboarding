package com.berith.preonboarding.user;

import com.berith.preonboarding.user.dto.SignRequest;
import com.berith.preonboarding.user.dto.SignResponse;
import com.berith.preonboarding.user.dto.SignupRequest;
import com.berith.preonboarding.user.dto.SignupResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Onboarding Task API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @ApiResponse(responseCode = "201", description = "Success", content = {@Content(schema = @Schema(implementation = SignupResponse.class))})
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request));
    }

    @PostMapping("/sign")
    @ApiResponse(responseCode = "200", description = "token")
    @ApiResponse(responseCode = "404", description = "not found user", content = @Content(mediaType = "text/plain", schema = @Schema(type = "Stirng", example = "존재하지 않는 사용자입니다.")))
    public ResponseEntity<SignResponse> sign(@Valid @RequestBody SignRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.sign(request));
    }
}
