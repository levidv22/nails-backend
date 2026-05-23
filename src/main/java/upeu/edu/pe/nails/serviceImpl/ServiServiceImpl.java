package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.*;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.Servi;
import upeu.edu.pe.nails.repositories.ServiRepository;
import upeu.edu.pe.nails.services.ServiService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ServiServiceImpl implements ServiService {

    private final ServiRepository serviRepository;

    public ServiServiceImpl(ServiRepository serviRepository) {

        this.serviRepository = serviRepository;
    }

    @Override
    public Servi createService(Servi servi) {
        if (servi.getName() == null
                || servi.getName().isBlank()) {

            throw new RuntimeException(
                    "El nombre del servicio es obligatorio"
            );
        }
        if (servi.getDurationMinutes() == null
                || servi.getDurationMinutes() <= 0) {

            throw new RuntimeException(
                    "La duración debe ser mayor a 0"
            );
        }
        if (servi.getBasePrice() == null
                || servi.getBasePrice()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "El precio debe ser mayor a 0"
            );
        }
        servi.setId(null);
        servi.setActive(true);
        servi.setCreatedAt(LocalDateTime.now());
        if (servi.getImage() == null
                || servi.getImage().isBlank()) {

            servi.setImage(
                    "default-service.png"
            );
        }

        return serviRepository.save(servi);
    }

    @Override
    public Servi updateService(Long serviId,
                                 Servi updatedService) {

        Servi existingServi = serviRepository.findById(serviId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Servicio no encontrado"
                        )
                );
        if (updatedService.getName() == null
                || updatedService.getName().isBlank()) {

            throw new RuntimeException(
                    "El nombre del servicio es obligatorio"
            );
        }
        if (updatedService.getDurationMinutes() == null
                || updatedService.getDurationMinutes() <= 0) {

            throw new RuntimeException(
                    "La duración debe ser mayor a 0"
            );
        }
        if (updatedService.getBasePrice() == null
                || updatedService.getBasePrice()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "El precio debe ser mayor a 0"
            );
        }
        existingServi.setName(
                updatedService.getName()
        );
        existingServi.setDescription(
                updatedService.getDescription()
        );
        existingServi.setDurationMinutes(
                updatedService.getDurationMinutes()
        );
        existingServi.setBasePrice(
                updatedService.getBasePrice()
        );
        if (updatedService.getImage() != null
                && !updatedService.getImage().isBlank()) {

            existingServi.setImage(
                    updatedService.getImage()
            );
        }

        return serviRepository.save(existingServi);
    }

    @Override
    public void deleteService(Long serviId) {

        Servi servi = serviRepository.findById(serviId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Servicio no encontrado"
                        )
                );
        servi.setActive(false);

        serviRepository.save(servi);
    }

    @Override
    public List<Servi> getAllActiveServices() {

        return serviRepository.findByActiveTrue();
    }

    @Override
    public Servi getServiceById(Long serviId) {

        return serviRepository.findById(serviId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Servicio no encontrado"
                        )
                );
    }

}