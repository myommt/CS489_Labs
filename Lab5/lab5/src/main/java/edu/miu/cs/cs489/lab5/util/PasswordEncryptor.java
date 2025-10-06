package edu.miu.cs.cs489.lab5.util;

import org.jasypt.util.text.AES256TextEncryptor;

/**
 * Small helper to produce an encrypted value for use in application properties.
 * Usage (javac + run or run from IDE):
 *   Set environment variable or pass -Djasypt.encryptor.password=secretKey when decrypting at runtime.
 */
public class PasswordEncryptor {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java PasswordEncryptor <plain-text>");
            System.exit(1);
        }
        String plain = args[0];
        // For local encryption only: create AES encryptor with a temporary password
        // Note: Use the same encryptor password when starting the app: -Djasypt.encryptor.password=yourKey
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        String key = System.getProperty("jasypt.encryptor.password");
        if (key == null) {
            System.err.println("Set system property -Djasypt.encryptor.password=<key> to control encryption key");
            System.exit(2);
        }
        encryptor.setPassword(key);
        String encrypted = encryptor.encrypt(plain);
        System.out.println("ENC(" + encrypted + ")");
    }
}
