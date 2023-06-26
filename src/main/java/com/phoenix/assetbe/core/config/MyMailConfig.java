package com.phoenix.assetbe.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.net.ssl.SSLSocketFactory;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MyMailConfig {

    private final MailProperties mailProperties;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(this.mailProperties.getHost());
        javaMailSender.setUsername(this.mailProperties.getUsername());
        javaMailSender.setPassword(this.mailProperties.getPassword());
        javaMailSender.setPort(this.mailProperties.getPort());

        Properties properties = new Properties();
        properties.put("mail.smtp.socketFactory.port", this.mailProperties.getPort());
        properties.put("mail.smtp.socketFactory.class", this.mailProperties.getSocketFactoryClass());
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        javaMailSender.setJavaMailProperties(properties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}