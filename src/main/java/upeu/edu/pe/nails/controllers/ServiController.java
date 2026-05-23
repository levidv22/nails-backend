package upeu.edu.pe.nails.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upeu.edu.pe.nails.entities.Servi;
import upeu.edu.pe.nails.services.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/servi")
public class ServiController {

    private final ServiService serviService;
    private final FileStorageService fileStorageService;

    public ServiController(ServiService serviService,
                           FileStorageService fileStorageService) {
        this.serviService = serviService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(path = "/active")
    public ResponseEntity<List<Servi>> getActiveServices() {

        List<Servi> services = serviService.getAllActiveServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Servi> getById(@PathVariable Long id) {

        Servi servi = serviService.getServiceById(id);
        return ResponseEntity.ok(servi);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Servi> createService(

            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("basePrice") BigDecimal basePrice,

            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {

        Servi servi = new Servi();

        servi.setName(name);
        servi.setDescription(description);
        servi.setDurationMinutes(durationMinutes);
        servi.setBasePrice(basePrice);

        if (image != null && !image.isEmpty()) {

            String imagePath =
                    fileStorageService.uploadImage(image);

            servi.setImage(imagePath);
        }

        Servi created =
                serviService.createService(servi);

        return ResponseEntity.ok(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Servi> updateService(

            @PathVariable Long id,

            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("durationMinutes") Integer durationMinutes,
            @RequestParam("basePrice") BigDecimal basePrice,

            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {

        Servi servi = new Servi();

        servi.setName(name);
        servi.setDescription(description);
        servi.setDurationMinutes(durationMinutes);
        servi.setBasePrice(basePrice);

        if (image != null && !image.isEmpty()) {

            String imagePath =
                    fileStorageService.uploadImage(image);

            servi.setImage(imagePath);
        }

        Servi updated =
                serviService.updateService(id, servi);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {

        Servi servi = serviService.getServiceById(id);
        if (servi.getImage() != null) {
            fileStorageService.deleteImage(servi.getImage());
        }

        serviService.deleteService(id);

        return ResponseEntity.noContent().build();
    }
}