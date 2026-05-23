package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.*;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.*;
import upeu.edu.pe.nails.services.*;

import java.math.*;

@Service
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {

    private static final int REQUIRED_RESERVATIONS = 3;
    private static final int DISCOUNT_PERCENTAGE = 20;

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final UserRepository userRepository;

    public LoyaltyServiceImpl(
            LoyaltyAccountRepository loyaltyAccountRepository,
            UserRepository userRepository
    ) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BigDecimal calculateDiscount(User client, BigDecimal basePrice) {

        if (client == null || client.getId() == null) {
            throw new RuntimeException("Cliente inválido");
        }
        if (basePrice == null) {
            throw new RuntimeException("Precio inválido");
        }
        LoyaltyAccount account = getOrCreateAccount(client);
        if (Boolean.TRUE.equals(account.getDiscountAvailable())) {

            BigDecimal discount = basePrice
                    .multiply(BigDecimal.valueOf(DISCOUNT_PERCENTAGE))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            //account.setDiscountAmount(discount);
            account.setDiscountPercentage(DISCOUNT_PERCENTAGE);

            return discount;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public void updateProgress(User client) {
        LoyaltyAccount account = getOrCreateAccount(client);
        int newProgress = account.getCurrentProgress() == null
                ? 0
                : account.getCurrentProgress();
        int completed = account.getCompletedReservations() == null
                ? 0
                : account.getCompletedReservations();
        completed++;
        newProgress++;
        if (newProgress >= REQUIRED_RESERVATIONS) {
            account.setDiscountAvailable(true);
            newProgress = 0;
        }
        account.setCompletedReservations(completed);
        account.setCurrentProgress(newProgress);
        loyaltyAccountRepository.save(account);
    }

    @Override
    public void consumeDiscount(User client) {
        LoyaltyAccount account = getOrCreateAccount(client);
        account.setDiscountAvailable(false);
        account.setCurrentProgress(0);
        loyaltyAccountRepository.save(account);
    }

    @Override
    public boolean hasDiscountAvailable(User client) {
        LoyaltyAccount account = getOrCreateAccount(client);
        return Boolean.TRUE.equals(account.getDiscountAvailable());
    }

    private LoyaltyAccount getOrCreateAccount(User client) {

        return loyaltyAccountRepository.findByClient(client)
                .orElseGet(() -> {

                    LoyaltyAccount account = new LoyaltyAccount();

                    account.setClient(client);
                    account.setCompletedReservations(0);
                    account.setCurrentProgress(0);
                    account.setDiscountAvailable(false);
                    account.setDiscountPercentage(DISCOUNT_PERCENTAGE);

                    return loyaltyAccountRepository.save(account);
                });
    }
}