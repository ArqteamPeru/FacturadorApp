package com.facturador.util;

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;

public class SeguridadUtil {

    /**
     * Hash a password using BCrypt.
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Verify a password against the stored hash. Supports legacy SHA-256 hashes.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null) {
            return false;
        }
        if (isLegacyHash(storedHash)) {
            return storedHash.equals(legacySHA256(password));
        }
        return BCrypt.checkpw(password, storedHash);
    }

    /**
     * Determine if the hash corresponds to the old SHA-256 scheme.
     */
    public static boolean isLegacyHash(String hash) {
        return hash.matches("[a-fA-F0-9]{64}");
    }

    // SHA-256 hashing used in previous versions
    private static String legacySHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }
}
