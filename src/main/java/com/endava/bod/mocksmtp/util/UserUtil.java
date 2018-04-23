package com.endava.bod.mocksmtp.util;

import com.endava.bod.mocksmtp.domain.Activation;
import com.endava.bod.mocksmtp.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.awt.SunHints;

@Component
public class UserUtil {

    @Value("${activation.link}")
    private String activationLink;

    public String generateActivationLink(Activation activation) {
        return activationLink
                .replace("{{user}}",activation.getUser())
                .replace("{{code}}",activation.getCode());
    }

    public String generateDefaultPassword(User user) {
        return user.toString().hashCode()+"";
    }

    public String generateActivationCode(User user) {
        return System.currentTimeMillis()+user.hashCode()+"";
    }
}
