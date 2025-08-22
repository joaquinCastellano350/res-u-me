package com.joaquin.backend.auth;

import com.joaquin.backend.auth.dto.AuthToken;
import com.joaquin.backend.auth.dto.LoginRequest;
import com.joaquin.backend.auth.dto.SignupRequest;
import com.joaquin.backend.security.JwtService;
import com.joaquin.backend.user.User;
import com.joaquin.backend.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Object signup(@Valid @RequestBody SignupRequest req) {
        User u = userService.register(req);
        return new Object() {
            public final String id = u.getId().toString();
            public final  String email = u.getEmail();
            public final String createdAt = u.getCreated_at().toString();
        };
    }

    @PostMapping("/login")
    public AuthToken login(@Valid @RequestBody LoginRequest req) {
        User u = userService.login(req);
        String token = jwtService.generateToken(u.getId() , u.getEmail());
        return new AuthToken(token, "Bearer", 3600);
    }

}
