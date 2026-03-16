package com.homeeco.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homeeco.entity.User;
import com.homeeco.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ================= REGISTER =================
    public String register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "EMAIL_EXISTS";
        }

        userRepository.save(user);
        return "SUCCESS";
    }

    // ================= LOGIN =================
    public Optional<User> login(String email, String password) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()
                && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }

        return Optional.empty();
    }

    // ================= RESET PASSWORD =================
    public String resetPassword(String email, String password) {

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return "INVALID";

        User user = userOpt.get();
        user.setPassword(password);

        userRepository.save(user);
        return "SUCCESS";
    }
}
