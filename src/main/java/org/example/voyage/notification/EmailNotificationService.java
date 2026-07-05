package org.example.voyage.notification;

import org.example.voyage.notification.dto.NotificationPayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailNotificationService {
    private final JavaMailSender mailSender;

    EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = "${app.rabbitmq.queues.email}")
    public void handleNotification(NotificationPayload payload) {
        try{
            sendEmail(payload);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(NotificationPayload payload) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(payload.getGuestEmail());

        if("CONFIRMED".equals(payload.getEventType())){
            message.setSubject("Booking Confirmed - " + payload.getHotelName());
            message.setText(String.format(
                    "Dear %s,\n\nYour booking at %s from %s to %s is confirmed.\nBooking ID: %s",
                    payload.getGuestName(), payload.getHotelName(),
                    payload.getCheckIn(), payload.getCheckOut(), payload.getBookingId()
            ));
        }
        else {
            message.setSubject("Booking Cancelled — " + payload.getHotelName());
            message.setText(String.format(
                    "Dear %s,\n\nYour booking at %s (ID: %s) has been cancelled.",
                    payload.getGuestName(), payload.getHotelName(), payload.getBookingId()
            ));
        }
        mailSender.send(message);
    }
}
