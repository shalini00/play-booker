package com.app.playbooker.audit;

import com.app.playbooker.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component("entityAuditor")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                return Optional.ofNullable(jwtUtil.getEmailFromToken(httpServletRequest));

            }
        } catch (Exception e) {
            // Log the fallback if needed
        }

        // Fallback if no request context is available (e.g. in cron jobs)
        return Optional.of("system");
    }
}
