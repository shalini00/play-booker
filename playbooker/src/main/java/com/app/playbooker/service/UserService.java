package com.app.playbooker.service;

import com.app.playbooker.entity.User;
import com.app.playbooker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isAdminUser(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName().equals(ROLE_ADMIN));
    }

    public User getUserById(String id) {
        return userRepository.findById(id).get();
    }
}
