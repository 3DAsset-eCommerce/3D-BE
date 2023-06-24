package com.phoenix.assetbe.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MyMailConfig {

    private final MailProperties mailProperties;

    @Bean
    public JavaMailSender javaMailService() throws Exception {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailProperties.getHost());
        javaMailSender.setUsername(mailProperties.getUsername());
        javaMailSender.setPassword(mailProperties.getPassword());
        javaMailSender.setPort(mailProperties.getPort());

        Properties javaMailProperties = new Properties();

        javaMailProperties.put("mail.smtp.auth", true);
        javaMailProperties.put("mail.smtp.host", "smtp.gmail.com");
        javaMailProperties.put("mail.smtp.starttls.enable", true);
        javaMailProperties.put("mail.smtp.port", mailProperties.getPort());

        javaMailSender.setJavaMailProperties(javaMailProperties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}