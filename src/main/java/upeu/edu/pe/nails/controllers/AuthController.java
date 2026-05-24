package upeu.edu.pe.nails.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.nails.dto.AuthResponse;
import upeu.edu.pe.nails.dto.LoginRequest;
import upeu.edu.pe.nails.entities.User;
import upeu.edu.pe.nails.jwt.JwtService;
import upeu.edu.pe.nails.services.*;
import upeu.edu.pe.nails.token.TokenBlacklistService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            TokenBlacklistService blacklistService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<User> register(
            @RequestBody User user
    ) {

        User createdUser =
                userService.registerClient(user);

        return ResponseEntity.ok(createdUser);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {

        User user = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        String token =
                jwtService.generateToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Extraemos la expiración real del token para que el servicio sepa cuándo borrarlo de memoria
            Date expiration = jwtService.extractClaims(token).getExpiration();

            // Mandamos el token a morir en la lista negra
            blacklistService.blacklistToken(token, expiration);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sesión cerrada correctamente");
        return ResponseEntity.ok(response);
    }
}
