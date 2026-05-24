package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.nails.dto.AuthResponse;
import upeu.edu.pe.nails.dto.LoginRequest;
import upeu.edu.pe.nails.entities.User;
import upeu.edu.pe.nails.jwt.JwtService;
import upeu.edu.pe.nails.services.*;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
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
}
