package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.User;

public interface UserService {

    User registerClient(User user);
    User login(String email, String password);
    User updateProfile(Long userId,User updatedUser);
    User findClientByPhone(String phone);
    void changePassword(Long userId, String oldPassword, String newPassword);
}