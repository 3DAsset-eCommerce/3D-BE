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
        javaMailSender.setPort(465); // 포트 번호를 465로 설정

        Properties javaMailProperties = new Properties();

        javaMailProperties.put("mail.smtp.auth", true);
        javaMailProperties.put("mail.smtp.starttls.enable", false); // STARTTLS 비활성화
        javaMailProperties.put("mail.smtp.ssl.enable", true); // SSL 활성화
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
        javaMailProperties.put("mail.smtp.socketFactory.port", 465); // 포트 번호를 465로 설정

        javaMailSender.setJavaMailProperties(javaMailProperties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}