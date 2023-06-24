package com.phoenix.assetbe.core.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class MailUtils {

    private static JavaMailSender mailSender;

    @Value("${MAIL_FROM_ADDRESS}")
    private static String username;

    @Autowired
    private MailUtils(JavaMailSender mailSender) {
                this.mailSender = mailSender;
    }

    /**
     * 메일을 발송합니다.
     *
     * @param toEmail 수신자(여러명일 경우 ',' 구분
     * @param title 제목
     * @param content 내용
     * @return
     */
    public static boolean send(String toEmail, String title, String content) {
        if (StringUtils.isEmpty(toEmail)) return false; // 수신자가 없을 경우 종료

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            ClassPathResource imageResource = new ClassPathResource("static/logo.jpg");

            messageHelper.setSubject(title);
            messageHelper.setText(content, true);
            messageHelper.addInline("logo", imageResource);
            messageHelper.setFrom(username);
            messageHelper.setTo(toEmail);

            mailSender.send(message); // 메일발송

            return true;
        }catch(MailException es){
            es.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}