package com.joaquin.backend.user;


import com.joaquin.backend.auth.dto.LoginRequest;
import com.joaquin.backend.auth.dto.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(SignupRequest req) {
        if (userRepository.existsByEmail(req.email().toLowerCase())) {
            throw new IllegalStateException("Email already exists");
        }
        User u = new User();
        u.setEmail(req.email());
        u.setPassword_hash(passwordEncoder.encode(req.password()));
        if (req.fullname() != null && !req.fullname().isEmpty()){
            u.setFull_name(req.fullname());
        }
        userRepository.save(u);
        return u;
    }

    public User login(LoginRequest req) {
        User u = userRepository.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new IllegalStateException("Email not found"));
        if (!passwordEncoder.matches(req.password() , u.getPassword_hash())) {
            throw new IllegalStateException("Invalid credentials");
        }
        return u;

    }
}
