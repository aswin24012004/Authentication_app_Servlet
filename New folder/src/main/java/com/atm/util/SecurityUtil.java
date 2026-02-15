package com.atm.util;

import org.mindrot.jbcrypt.BCrypt;
import java.util.Base64;

public class SecurityUtil {

    // Hashing
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    // Simple Encryption/Decryption (Base64 for demonstration purposes)
    // In a real app, use AES/RSA
    public static String encrypt(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static String decrypt(String encryptedData) {
        return new String(Base64.getDecoder().decode(encryptedData));
    }
}
