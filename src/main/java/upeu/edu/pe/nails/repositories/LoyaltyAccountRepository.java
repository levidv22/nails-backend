package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.LoyaltyAccount;
import upeu.edu.pe.nails.entities.User;

import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {

    Optional<LoyaltyAccount> findByClient(User client);

}