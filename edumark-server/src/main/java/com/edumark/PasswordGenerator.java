package com.edumark;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && "genpwd".equals(args[0])) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = args.length > 1 ? args[1] : "admin123";
            String hash = encoder.encode(password);
            System.out.println("====================================");
            System.out.println("Password: " + password);
            System.out.println("BCrypt Hash: " + hash);
            System.out.println("====================================");
            System.exit(0);
        }
    }
}
