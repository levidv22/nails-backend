package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.*;
import upeu.edu.pe.nails.entities.*;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<User> findByRole(RoleType role);
    List<User> findByActiveTrue();

}
