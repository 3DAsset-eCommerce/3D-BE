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
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
        javaMailProperties.put("mail.smtp.socketFactory.port", mailProperties.getPort());
        javaMailProperties.put("mail.smtp.port", mailProperties.getPort());

        javaMailSender.setJavaMailProperties(javaMailProperties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}