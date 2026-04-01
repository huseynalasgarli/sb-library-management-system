package com.hsyn.service;

import com.hsyn.exception.UserException;
import com.hsyn.payload.dto.UserDTO;
import com.hsyn.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String username, String password);
    AuthResponse signUp(UserDTO userDTO) throws UserException;

    void createPasswordResetToken(String email);
    void resetPassword(String token, String password);

}
