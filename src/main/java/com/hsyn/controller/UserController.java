package com.hsyn.controller;


import com.hsyn.model.User;
import com.hsyn.payload.dto.UserDTO;
import com.hsyn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }


}
