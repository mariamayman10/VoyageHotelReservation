package org.example.voyage.auth;

import org.example.voyage.exception.EmailAlreadyExistsException;
import org.example.voyage.exception.InvalidCredentialsException;
import org.example.voyage.security.UserPrincipal;
import org.example.voyage.user.User;
import org.example.voyage.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.voyage.auth.dto.AuthResponse;
import org.example.voyage.auth.dto.LoginRequest;
import org.example.voyage.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request){
        if(userService.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(User.UserRole.CUSTOMER);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userService.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(user.getId(), user.getEmail(), user.getRole(), user.getPasswordHash());

        return new AuthResponse(jwtService.generateToken(userPrincipal));
    }
    public AuthResponse login(LoginRequest request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getPasswordHash()
        );

        return new AuthResponse(jwtService.generateToken(principal));
    }
}
