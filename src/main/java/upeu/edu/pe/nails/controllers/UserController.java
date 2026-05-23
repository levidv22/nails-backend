package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upeu.edu.pe.nails.entities.User;
import upeu.edu.pe.nails.services.FileStorageService;
import upeu.edu.pe.nails.services.UserService;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    public UserController(UserService userService,
                          FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(path = "/profile")
    public ResponseEntity<User> getProfile(@RequestParam Long userId) {

        User user = userService
                .findClientByPhone(
                        userService.findClientByPhone(userId.toString()).getPhone()
                );

        return ResponseEntity.ok(user);
    }

    @PutMapping(path = "/profile/{userId}")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long userId,
            @RequestPart("user") User user,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.uploadImage(image);
            user.setProfileImage(imagePath);
        }
        User updated = userService.updateProfile(userId, user);
        return ResponseEntity.ok(updated);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<User> findByPhone(@RequestParam String phone) {
        User user = userService.findClientByPhone(phone);
        return ResponseEntity.ok(user);
    }
}