package kz.rikudo.study.expencetracker.controller;

import kz.rikudo.study.expencetracker.dto.AuthRequest;
import kz.rikudo.study.expencetracker.dto.AuthResponse;
import kz.rikudo.study.expencetracker.dto.RegisterRequest;
import kz.rikudo.study.expencetracker.dto.UserProfileDTO;
import kz.rikudo.study.expencetracker.entity.User;
import kz.rikudo.study.expencetracker.repository.UserRepository;
import kz.rikudo.study.expencetracker.security.jwt.JwtUtil;
import kz.rikudo.study.expencetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req);
        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(org.springframework.security.core.Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();

        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        return ResponseEntity.ok(dto);
    }


}
