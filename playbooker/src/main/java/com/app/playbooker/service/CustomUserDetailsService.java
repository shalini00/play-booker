package com.app.playbooker.service;

import com.app.playbooker.dto.UserCreationDTO;
import com.app.playbooker.entity.Roles;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.Providers;
import com.app.playbooker.repository.RolesRepository;
import com.app.playbooker.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    @Qualifier("bCryptPasswordEncoder")
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                authorities
        );

    }

    public User createUser(UserCreationDTO userCreationDTO) {
        User user = new User();
        BeanUtils.copyProperties(userCreationDTO, user);
        user.setPassword(bCryptPasswordEncoder.encode(userCreationDTO.getPassword()));
        Roles roles = rolesRepository.findByName("ROLE_USER").get();
        user.setRoles(Set.of(roles));
        user.setProvider(Providers.DEFAULT);
        return userRepository.save(user);
    }
}
