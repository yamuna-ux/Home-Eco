package com.homeeco.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.homeeco.entity.OtpData;
import com.homeeco.repository.OtpRepository;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    // ================= SEND OTP =================
    public void sendOtp(String email) {

    System.out.println("OTP request received for: " + email); // 👈 ADD THIS

    String otp = String.valueOf(new Random().nextInt(900000) + 100000);
    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

    OtpData otpData = otpRepository.findByEmail(email)
            .orElse(new OtpData());

    otpData.setEmail(email);
    otpData.setOtp(otp);
    otpData.setExpiryTime(expiryTime);
    otpRepository.save(otpData);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("HomeEco OTP Verification");
    message.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");

    mailSender.send(message);   // 👈 this line is key

    System.out.println("OTP mail sent attempt completed"); // 👈 ADD THIS
}

    // ================= VERIFY OTP =================
    public boolean verifyOtp(String email, String enteredOtp) {

        Optional<OtpData> otpOptional = otpRepository.findByEmail(email);

        if (otpOptional.isEmpty()) {
            return false;
        }

        OtpData otpData = otpOptional.get();

        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            return false;
        }

        return otpData.getOtp().equals(enteredOtp);
    }
}
