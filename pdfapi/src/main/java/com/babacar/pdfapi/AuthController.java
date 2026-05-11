package com.babacar.pdfapi;

import com.babacar.pdfapi.dto.AuthResponse;
import com.babacar.pdfapi.dto.LoginRequest;
import com.babacar.pdfapi.dto.RegisterRequest;
import com.babacar.pdfapi.model.User;
import com.babacar.pdfapi.security.JwtProvider;
import com.babacar.pdfapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // Validation
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Username is required", false));
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Password is required", false));
            }
            if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Email is required", false));
            }

            User user = userService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail()
            );

            String token = jwtProvider.generateTokenFromUsername(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Validation
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Username is required", false));
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new AuthResponse("Password is required", false));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtProvider.generateToken(authentication);
            User user = userService.findByUsername(loginRequest.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid username or password", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("{\"status\": \"API is running\"}");
    }
}
