package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.*;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);
    List<Notification> findBySentFalse();

}