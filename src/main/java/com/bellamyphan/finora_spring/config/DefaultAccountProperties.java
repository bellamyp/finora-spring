package com.bellamyphan.finora_spring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class DefaultAccountProperties {

    private Admin admin = new Admin();
    private UserAccount user = new UserAccount();

    @Getter
    @Setter
    public static class Admin {
        private String email;
        private String password;
        private String name;
    }

    @Getter
    @Setter
    public static class UserAccount {
        private String email;
        private String password;
        private String name;
    }
}
