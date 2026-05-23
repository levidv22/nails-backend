package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.*;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRating(Integer rating);
    List<Review> findTop10ByOrderByCreatedAtDesc();
    List<Review> findByClient(User client);

}
