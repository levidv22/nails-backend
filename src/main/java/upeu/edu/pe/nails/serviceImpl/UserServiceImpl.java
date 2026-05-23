package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.*;
import upeu.edu.pe.nails.services.*;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerClient(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {

            throw new RuntimeException(
                    "El correo ya está registrado"
            );
        }
        if (userRepository.existsByPhone(user.getPhone())) {

            throw new RuntimeException(
                    "El teléfono ya está registrado"
            );
        }
        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            throw new RuntimeException(
                    "La contraseña es obligatoria"
            );
        }
        user.setId(null);
        user.setRole(RoleType.CLIENT);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );
        if (user.getProfileImage() == null
                || user.getProfileImage().isBlank()) {

            user.setProfileImage(
                    "default-profile.png"
            );
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String email,
                      String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Correo incorrecto"
                        )
                );
        if (!Boolean.TRUE.equals(user.getActive())) {

            throw new RuntimeException(
                    "Usuario desactivado"
            );
        }
        boolean matches = passwordEncoder.matches(
                password,
                user.getPassword()
        );
        if (!matches) {

            throw new RuntimeException(
                    "Contraseña incorrecta"
            );
        }
        return user;
    }

    @Override
    public User updateProfile(Long userId,
                              User updatedUser) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
        if (!existingUser.getEmail()
                .equals(updatedUser.getEmail())) {

            if (userRepository.existsByEmail(
                    updatedUser.getEmail())) {

                throw new RuntimeException(
                        "El correo ya está en uso"
                );
            }
        }
        if (!existingUser.getPhone()
                .equals(updatedUser.getPhone())) {

            if (userRepository.existsByPhone(
                    updatedUser.getPhone())) {

                throw new RuntimeException(
                        "El teléfono ya está en uso"
                );
            }
        }
        existingUser.setFullName(
                updatedUser.getFullName()
        );

        existingUser.setPhone(
                updatedUser.getPhone()
        );

        existingUser.setEmail(
                updatedUser.getEmail()
        );
        if (updatedUser.getProfileImage() != null
                && !updatedUser.getProfileImage().isBlank()) {

            existingUser.setProfileImage(
                    updatedUser.getProfileImage()
            );
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User findClientByPhone(String phone) {

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cliente no encontrado"
                        )
                );
        if (user.getRole() != RoleType.CLIENT) {

            throw new RuntimeException(
                    "El usuario no es un cliente"
            );
        }

        return user;
    }

    @Override
    public void changePassword(Long userId,
                               String oldPassword,
                               String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
        boolean matches = passwordEncoder.matches(
                oldPassword,
                user.getPassword()
        );

        if (!matches) {

            throw new RuntimeException(
                    "La contraseña actual es incorrecta"
            );
        }
        if (newPassword == null
                || newPassword.isBlank()) {

            throw new RuntimeException(
                    "La nueva contraseña es obligatoria"
            );
        }
        boolean samePassword = passwordEncoder.matches(
                newPassword,
                user.getPassword()
        );

        if (samePassword) {

            throw new RuntimeException(
                    "La nueva contraseña no puede ser igual a la anterior"
            );
        }
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }

}