package upeu.edu.pe.nails.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import upeu.edu.pe.nails.services.FileStorageService;

import java.io.File;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + File.separator + "uploads";

    @Override
    public String uploadImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Archivo inválido");
        }

        try {

            File directory = new File(UPLOAD_DIR);

            if (!directory.exists()) {

                boolean created = directory.mkdirs();

                if (!created) {
                    throw new RuntimeException(
                            "No se pudo crear la carpeta uploads"
                    );
                }
            }

            String originalFilename =
                    file.getOriginalFilename();

            String extension = "";

            if (originalFilename != null
                    && originalFilename.contains(".")) {

                extension = originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );
            }

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + UUID.randomUUID()
                            + extension;

            File destination =
                    new File(directory, fileName);

            file.transferTo(destination);

            return "uploads/" + fileName;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al subir la imagen: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void deleteImage(String image) {

        if (image == null || image.isBlank()) {
            return;
        }

        try {

            File file = new File(
                    System.getProperty("user.dir")
                            + File.separator
                            + image
            );

            if (file.exists()) {

                boolean deleted = file.delete();

                if (!deleted) {

                    throw new RuntimeException(
                            "No se pudo eliminar la imagen"
                    );
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al eliminar la imagen: "
                            + e.getMessage()
            );
        }
    }
}