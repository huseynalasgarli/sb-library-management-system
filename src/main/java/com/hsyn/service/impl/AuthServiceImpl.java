package com.hsyn.service.impl;

import com.hsyn.configurations.JwtProvider;
import com.hsyn.domain.UserRole;
import com.hsyn.exception.UserException;
import com.hsyn.mapper.UserMapper;
import com.hsyn.model.PasswordResetToken;
import com.hsyn.model.User;
import com.hsyn.payload.dto.UserDTO;
import com.hsyn.payload.response.AuthResponse;
import com.hsyn.repository.PasswordResetTokenRepository;
import com.hsyn.repository.UserRepository;
import com.hsyn.service.AuthService;
import com.hsyn.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserServiceImpl customUserServiceImpl;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    public AuthResponse login(String username, String password) throws UserException {
    Authentication authentication = authenticate(username,password);

    SecurityContextHolder.getContext().setAuthentication(authentication);
//    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//  String role =authorities.iterator().next().getAuthority();
    String token = jwtProvider.generateToken(authentication);

    User user = userRepository.findByEmail(username);

    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    AuthResponse authResponse = new AuthResponse();
    authResponse.setTitle("Login successful");
    authResponse.setMessage("Welcome back " + username);
    authResponse.setJwt(token);
    authResponse.setUser(UserMapper.toDTO(user));

    return authResponse;
    }

    private Authentication authenticate(String username, String password) throws UserException {

        UserDetails userDetails = customUserServiceImpl.loadUserByUsername(username);

        if(userDetails == null) {
            throw new UserException("user not found with username - " + username);
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UserException("password not match");
        }
        return new UsernamePasswordAuthenticationToken(username,null,userDetails.getAuthorities());
    }

    @Override
    public AuthResponse signUp(UserDTO userDTO) throws UserException {
        User user = userRepository.findByEmail(userDTO.getEmail());

        if (user == null) {
            throw new UserException ("Email ID already registered.");
        }
        User createdUser = new User();
        createdUser.setEmail(userDTO.getEmail());
        createdUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        createdUser.setPhone(userDTO.getPhone());
        createdUser.setFullName(userDTO.getFullName());
        createdUser.setLastLogin(LocalDateTime.now());
        createdUser.setRole(UserRole.ROLE_USER);

        User savedUser = userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setTitle("Welcome " + createdUser.getFullName());
        authResponse.setMessage("register successful");
        authResponse.setUser(UserMapper.toDTO(savedUser));
        return authResponse;
    }

    @Transactional
    public void createPasswordResetToken(String email) throws UserException {

        String frontendUrl = "http://localhost:5173";
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserException("user not found with the email");
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + token;
        String subject = "Password reset request";
        String body = "You requested to reset your password. Use this link (valid 5 minutes): " + resetLink;

        //sent email

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(String token, String password) throws Exception {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(
                        () -> new  Exception("token not valid")
                );

        if(resetToken.isExpired()){
            passwordResetTokenRepository.delete(resetToken);
            throw new Exception("token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }
}
