package org.sysRestaurante.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class Encryption {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Encryption.class.getName());
    private static SecretKeySpec secretKey;

    public static void setKey(String pkey) {
        MessageDigest sha;
        try {
            byte[] key = pkey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");

            LOGGER.info("Encryption hash set.");

        } catch (NoSuchAlgorithmException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
            LOGGER.severe("There was a problem creating the encryption key");
        }
    }

    public static SecretKeySpec getKey() {
        return secretKey;
    }

    public static String encrypt(String word) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, Encryption.getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(word.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
            LOGGER.severe("Error while ecrypting " + ex);
        }
        return null;
    }

    public static String decrypt(String word, String key) {
        try {
            setKey(key);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, Encryption.getKey());
            return new String(cipher.doFinal(Base64.getDecoder().decode(word)));
        } catch (Exception ex) {
            LOGGER.warning("Couldn't decrypt due to something that might be an error: " + ex);
        }
        return null;
    }
}
