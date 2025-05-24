package com.app.playbooker.config;

import com.app.playbooker.entity.Roles;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.Providers;
import com.app.playbooker.repository.RolesRepository;
import com.app.playbooker.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    Logger LOG = LoggerFactory.getLogger(OAuthSuccessHandler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        LOG.info("OAuth Authentication handler");
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        LOG.info(oAuth2User.getName());
        LOG.info("{}", oAuth2User.getAttributes());

        String email = oAuth2User.getAttribute("email").toString();
        User user = new User();

        user.setName(oAuth2User.getAttribute("name").toString());
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword("default_password");
        user.setProvider(Providers.GOOGLE);
        Roles roles = rolesRepository.findByName("ROLE_USER").get();
        user.setRoles(Set.of(roles));

        User existingUser = userRepository.findByEmail(email);
        if (Objects.isNull(existingUser)) {
            userRepository.save(user);
            LOG.info("User saved successfully");
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/home");
    }
}
