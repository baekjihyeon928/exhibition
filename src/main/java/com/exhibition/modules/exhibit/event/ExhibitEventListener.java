package com.exhibition.modules.exhibit.event;

import com.exhibition.infra.config.AppProperties;
import com.exhibition.infra.mail.EmailMessage;
import com.exhibition.infra.mail.EmailService;
import com.exhibition.modules.account.Account;
import com.exhibition.modules.account.AccountPredicates;
import com.exhibition.modules.account.AccountRepository;
import com.exhibition.modules.exhibit.Exhibit;
import com.exhibition.modules.exhibit.ExhibitRepository;
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
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class ExhibitEventListener {

    public final ExhibitRepository exhibitRepository;
    public final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleExhibitCreatedEvent(ExhibitCreatedEvent exhibitCreatedEvent) {
        Exhibit exhibit = exhibitRepository.findExhibitWithTagsAndMajorsById(exhibitCreatedEvent.getExhibit().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndMajors(exhibit.getTags(), exhibit.getMajors()));
        accounts.forEach(account -> {
            if (account.isExhibitCreatedByEmail()) {
                sendExhibitCreatedEmail(exhibit, account, "새로운 작품이 전시되었습니다",
                        "온라인 전시회, '" + exhibit.getTitle() + "' 작품이 전시되었습니다.");
            }

            if (account.isExhibitCreatedByWeb()) {
                createNotification(exhibit, account, exhibit.getShortDescription(), NotificationType.EXHIBIT_CREATED);
            }
        });
    }

    @EventListener
    public void handleExhibitUpdateEvent(ExhibitUpdateEvent exhibitUpdateEvent) {
        Exhibit exhibit = exhibitRepository.findExhibitWithOwnersAndMemebersById(exhibitUpdateEvent.getExhibit().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(exhibit.getOwners());
        accounts.addAll(exhibit.getOwners());

        accounts.forEach(account -> {
            if (account.isExhibitUpdatedByEmail()) {
                sendExhibitCreatedEmail(exhibit, account, exhibitUpdateEvent.getMessage(),
                        " '" + exhibit.getTitle() + "' 스터디에 새소식이 있습니다.");
            }

            if (account.isExhibitUpdatedByWeb()) {
                createNotification(exhibit, account, exhibitUpdateEvent.getMessage(), NotificationType.EXHIBIT_UPDATED);
            }
        });
    }

    @EventListener
    public void handleExhibitEvaluatedEvent(ExhibitEvaluatedEvent exhibitEvaluatedEvent) {
        Exhibit exhibit = exhibitRepository.findExhibitWithOwnersById(exhibitEvaluatedEvent.getExhibit().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(exhibit.getOwners());

        accounts.forEach(account -> {
            if (account.isExhibitPassedByEmail()) {
                sendExhibitCreatedEmail(exhibit, account, exhibitEvaluatedEvent.getMessage(),
                        "온라인 전시회, '" + exhibit.getTitle() + "' 평가가 완료되었습니다.");
            }

            if (account.isExhibitPassedByWeb()) {
                createNotification(exhibit, account, exhibitEvaluatedEvent.getMessage(), NotificationType.EXHIBIT_EVALUATED);
            }
        });
    }

    private void createNotification(Exhibit exhibit, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(exhibit.getTitle());
        notification.setLink("/exhibit/" + exhibit.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendExhibitCreatedEmail(Exhibit exhibit, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("name", account.getName());
        context.setVariable("link", "/exhibit/" + exhibit.getEncodedPath());
        context.setVariable("linkName", exhibit.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
