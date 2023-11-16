package com.exhibition.modules.event;

import com.exhibition.modules.account.Account;
import com.exhibition.modules.account.CurrentAccount;
import com.exhibition.modules.event.form.EventForm;
import com.exhibition.modules.event.validator.EventValidator;
import com.exhibition.modules.exhibit.Exhibit;
import com.exhibition.modules.exhibit.ExhibitRepository;
import com.exhibition.modules.exhibit.ExhibitService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/exhibit/{path}")
@RequiredArgsConstructor
public class EventController {

    private final ExhibitService exhibitService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final ExhibitRepository exhibitRepository;
    private final EnrollmentRepository enrollmentRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        model.addAttribute(exhibit);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(exhibit);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), exhibit, account);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }


    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event,
                           Model model) {
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(exhibitRepository.findExhibitWithOwnersByPath(path));
        return "event/view";
    }

    @GetMapping("/events")
    public String viewExhibitEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Exhibit exhibit = exhibitService.getExhibit(path);
        model.addAttribute(account);
        model.addAttribute(exhibit);

        List<Event> events = eventRepository.findByExhibitOrderByStartDateTime(exhibit);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        events.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "exhibit/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable("id") Event event, Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        model.addAttribute(exhibit);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("id") Event event, @Valid EventForm eventForm, Errors errors,
                                    Model model) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(exhibit);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() +  "/events/" + event.getId();
    }


    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Exhibit exhibit = exhibitService.getExhibitToUpdateStatus(account, path);
        eventService.deleteEvent(event);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account,
                                @PathVariable String path, @PathVariable("id") Event event) {
        Exhibit exhibit = exhibitService.getExhibitToEnroll(path);
        eventService.newEnrollment(event, account);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account,
                                   @PathVariable String path, @PathVariable("id") Event event) {
        Exhibit exhibit = exhibitService.getExhibitToEnroll(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                          @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Exhibit exhibit = exhibitService.getExhibitToUpdate(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/exhibit/" + exhibit.getEncodedPath() + "/events/" + event.getId();
    }

}
