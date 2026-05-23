package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.User;

import java.math.BigDecimal;

public interface LoyaltyService {

    BigDecimal calculateDiscount(User client, BigDecimal basePrice);
    void updateProgress(User client);
    void consumeDiscount(User client);
    boolean hasDiscountAvailable(User client);
}
