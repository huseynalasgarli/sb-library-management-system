package com.hsyn.controller;

import com.hsyn.exception.UserException;
import com.hsyn.payload.dto.UserDTO;
import com.hsyn.payload.request.ForgotPasswordRequest;
import com.hsyn.payload.request.LoginRequest;
import com.hsyn.payload.request.ResetPasswordRequest;
import com.hsyn.payload.response.ApiResponse;
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody LoginRequest req
    ) throws UserException {
        AuthResponse authResponse = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest req
    ) throws UserException {
        authService.createPasswordResetToken(req.getEmail());

        ApiResponse apiResponse = new ApiResponse(
                "A reset link was sent to your email",true
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestBody ResetPasswordRequest req
    ) throws Exception {
        authService.resetPassword(req.getToken(),req.getPassword());
        ApiResponse apiResponse = new ApiResponse(
                "Password reset is successful",true
        );
        return ResponseEntity.ok(apiResponse);
    }

}
