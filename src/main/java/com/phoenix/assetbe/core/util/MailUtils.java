package com.phoenix.assetbe.core.util;

import com.phoenix.assetbe.controller.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class MailUtils {
    private static MailProperties mailProperties;

    private static JavaMailSender mailSender;

    @Autowired
    private MailUtils(MailProperties mailProperties, JavaMailSender mailSender) {
        this.mailProperties = mailProperties;
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
            messageHelper.setFrom(mailProperties.getFromMail());
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