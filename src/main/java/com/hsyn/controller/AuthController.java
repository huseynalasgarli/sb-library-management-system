package com.hsyn.controller;

import com.hsyn.exception.UserException;
import com.hsyn.payload.dto.UserDTO;
import com.hsyn.payload.response.AuthResponse;
import com.hsyn.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUpHandler(
            @RequestBody @Valid UserDTO req
    ) throws UserException {
        AuthResponse authResponse = authService.signUp(req);
        return ResponseEntity.ok(authResponse);
    }


}
