package upeu.edu.pe.nails.token;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // Guarda el token como clave y su fecha de expiración como valor
    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token, Date expirationDate) {
        blacklist.put(token, expirationDate);
        cleanupExpiredTokens();
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    // Limpieza programada simple para no saturar la memoria
    private void cleanupExpiredTokens() {
        Date now = new Date();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}
