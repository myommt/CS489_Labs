package cs489.miu.dentalsurgeryapp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "jb123";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
        System.out.println("Hash length: " + hashedPassword.length());
        
        // Verify the password works
        boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
        System.out.println("Password verification: " + matches);
    }
}