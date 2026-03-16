package com.homeeco.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homeeco.entity.User;
import com.homeeco.service.OtpService;
import com.homeeco.service.UserService;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    // ================= REGISTER =================
   @PostMapping("/register")
public ResponseEntity<String> register(@RequestBody User user) {

    String result = userService.register(user);

    if ("EMAIL_EXISTS".equals(result)) {
        return ResponseEntity.badRequest().body("Email already exists");
    }

    return ResponseEntity.ok("Registered successfully");
}


    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        Optional<User> result =
                userService.login(user.getEmail(), user.getPassword());

        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        }

        return ResponseEntity.status(401).body("Invalid email or password");
    }

    // ================= SEND OTP =================
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email required");
        }

        otpService.sendOtp(email);
        return ResponseEntity.ok("OTP sent");
    }

    // ================= VERIFY OTP =================
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String otp = body.get("otp");

        boolean valid = otpService.verifyOtp(email, otp);

        if (valid) {
            return ResponseEntity.ok("OTP verified");
        }

        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody User user) {

        String result =
                userService.resetPassword(user.getEmail(), user.getPassword());

        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok("Password reset successful");
        }

        return ResponseEntity.badRequest().body("Invalid request");
    }
}
