package com.qapla.ERP.Society.service;

import com.qapla.ERP.Society.model.Tender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;



//    public void sendTenderNotification(Tender tender) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo("dohalesanket123@gmail.com");
//        message.setSubject("New Tender Created: " + tender.getTitle());
//        message.setText("A new tender has been created:\n\n"
//                + "Title: " + tender.getTitle() + "\n"
//                + "Description: " + tender.getDescription() + "\n"
//                + "Opening Date: " + tender.getOpeningDate() + "\n"
//                + "Closing Date: " + tender.getClosingDate() + "\n\n"
//                + "Regards,\nSociety Management System");
//
//        mailSender.send(message);
//    }

    public void sendTenderNotification(Tender tender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("dohalesanket123@gmail.com");
        message.setSubject("New Tender Created: " + tender.getTitle());

        String createdBy = tender.getCreatedBy() != null ? tender.getCreatedBy().getMemberName() : "Unknown";
        String createdAt = tender.getCreatedAt() != null ? tender.getCreatedAt().toString() : "Not Available";

        message.setText("A new tender has been created:\n\n"
                + "Title: " + tender.getTitle() + "\n"
                + "Description: " + tender.getDescription() + "\n"
                + "Opening Date: " + tender.getOpeningDate() + "\n"
                + "Closing Date: " + tender.getClosingDate() + "\n"
                + "Created By: " + createdBy + "\n"
                + "Created At: " + createdAt + "\n\n"
                + "Regards,\nSociety Management System");

        mailSender.send(message);
    }

}
