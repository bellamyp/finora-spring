package com.bellamyphan.finora_spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.ports}")
    private String ports; // comma-separated ports

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private boolean starttlsRequired;

    @Value("${spring.mail.properties.mail.debug}")
    private boolean debug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.starttls.required", starttlsRequired);
        props.put("mail.debug", debug);

        // Try all ports
        for (String portStr : ports.split(",")) {
            try {
                int port = Integer.parseInt(portStr.trim());
                mailSender.setPort(port);

                // Automatically switch SSL for port 465
                if (port == 465) {
                    props.put("mail.smtp.ssl.enable", "true");
                    props.put("mail.smtp.starttls.enable", "false"); // disable STARTTLS
                    props.put("mail.smtp.starttls.required", "false");
                } else {
                    props.put("mail.smtp.ssl.enable", "false");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.starttls.required", "true");
                }

                mailSender.testConnection(); // throws exception if blocked
                logger.info("Connected to Mailgun on port {}{}", port, port == 465 ? " with SSL" : " with STARTTLS");
                break;
            } catch (Exception e) {
                logger.warn("Failed on port {}: {}", portStr, e.getMessage());
            }
        }

        return mailSender;
    }
}
