package com.exhibition.modules.event.event;

import com.exhibition.infra.config.AppProperties;
import com.exhibition.infra.mail.EmailMessage;
import com.exhibition.infra.mail.EmailService;
import com.exhibition.modules.account.Account;
import com.exhibition.modules.event.Enrollment;
import com.exhibition.modules.event.Event;
import com.exhibition.modules.exhibit.Exhibit;
import com.exhibition.modules.notification.Notification;
import com.exhibition.modules.notification.NotificationRepository;
import com.exhibition.modules.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Exhibit exhibit = event.getExhibit();

        if (account.isExhibitEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event, exhibit);
        }

        if (account.isExhibitEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, exhibit);
        }
    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Exhibit exhibit) {
        Context context = new Context();
        context.setVariable("name", account.getName());
        context.setVariable("link", "/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", exhibit.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(event.getTitle() + " 스터디 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Exhibit exhibit) {
        Notification notification = new Notification();
        notification.setTitle(exhibit.getTitle() + " / " + event.getTitle());
        notification.setLink("/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}
