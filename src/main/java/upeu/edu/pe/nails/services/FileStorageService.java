package upeu.edu.pe.nails.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadImage(MultipartFile file);
    void deleteImage(String image);
}