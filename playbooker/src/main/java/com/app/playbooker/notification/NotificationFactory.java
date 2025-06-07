package com.app.playbooker.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public NotificationService getNotificationServiceByType(String type) {
        String beanName = type.concat("-").concat("notification");
        return (NotificationService) applicationContext.getBean(beanName);
    }
}
