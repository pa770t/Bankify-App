package bankify.service;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String generateRandomPassword(int length) {
        // Use SecureRandom for better randomness
        Random random = new SecureRandom();

        // Use StringBuilder for efficient string concatenation
        StringBuilder sb = new StringBuilder(length);

        // Loop to append 'length' number of random characters
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
