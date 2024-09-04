package com.berith.preonboarding;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/others")
public class OtherDomain {

    @PostMapping("/posts")
    public ResponseEntity<?> posts() {
        return new ResponseEntity<>("정상 접근", HttpStatus.OK);
    }
}
