package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.Servi;

import java.util.List;

public interface ServiRepository extends JpaRepository<Servi, Long> {

    List<Servi> findByActiveTrue();
    List<Servi> findByNameContainingIgnoreCase(String name);
}
