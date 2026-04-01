package com.hsyn.payload.dto;

import com.hsyn.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String phone;
    private UserRole userRole;
    private String fullName;

    private LocalDateTime lastLogin;
}
