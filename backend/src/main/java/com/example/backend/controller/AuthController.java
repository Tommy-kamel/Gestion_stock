package com.example.backend.controller;

import com.example.backend.entity.Utilisateur;
import com.example.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Utilisateur> utilisateur = authService.authenticate(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );

        if (utilisateur.isPresent()) {
            // Pour l'instant, on retourne juste un token mock
            // En production, utiliser JWT
            Map<String, Object> response = new HashMap<>();
            response.put("token", "jwt-token-" + utilisateur.get().getId());
            response.put("user", Map.of(
                "id", utilisateur.get().getId(),
                "nom", utilisateur.get().getNom()
            ));

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Identifiants incorrects"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            Utilisateur utilisateur = authService.createUser(
                registerRequest.getUsername(),
                registerRequest.getPassword()
            );

            return ResponseEntity.ok(Map.of("message", "Utilisateur créé", "id", utilisateur.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de la création"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        // Pour l'instant, retourner un profil mock
        // En production, récupérer depuis le token JWT
        return ResponseEntity.ok(Map.of(
            "id", 1,
            "nom", "Admin",
            "prenom", "System",
            "email", "admin@example.com",
            "role", "ADMIN",
            "entreprise", Map.of("id", 1L, "nom", "Entreprise Test")
        ));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}