package com.berith.preonboarding;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OtherDomain", description = "Another domain that requires authentication")
@RestController
@RequestMapping("/api/others")
public class OtherDomain {

    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "text/plain", schema = @Schema(type = "Stirng", example = "정상 접근")))
    @ApiResponse(responseCode = "403", description = "UnAuthorize")
    @PostMapping("/posts")
    public ResponseEntity<?> posts() {
        return new ResponseEntity<>("정상 접근", HttpStatus.OK);
    }
}
