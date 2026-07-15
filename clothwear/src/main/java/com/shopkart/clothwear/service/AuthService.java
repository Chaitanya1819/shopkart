package com.shopkart.clothwear.service;

import com.shopkart.clothwear.dto.AuthDto;
import com.shopkart.clothwear.exception.EmailAlreadyExistsException;
import com.shopkart.clothwear.exception.InvalidCredentialsException;
import com.shopkart.clothwear.model.User;
import com.shopkart.clothwear.repository.UserRepository;
import com.shopkart.clothwear.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
//tells service this class contains business logic
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        // Throws 409 Conflict via GlobalExceptionHandler
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthDto.AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        // Throws 401 Unauthorized via GlobalExceptionHandler
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthDto.AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }
}