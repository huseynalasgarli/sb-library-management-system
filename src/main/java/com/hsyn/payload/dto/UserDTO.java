package com.hsyn.payload.dto;

import com.hsyn.domain.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;

    @NotNull(message = "email is required")
    private String email;

    @NotNull(message = "password is required")
    private String password;
    private String phone;
    private UserRole userRole;

    @NotNull(message = "full name is required")
    private String fullName;

    private LocalDateTime lastLogin;
}
