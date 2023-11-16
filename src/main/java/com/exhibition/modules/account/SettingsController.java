package com.exhibition.modules.account;

import com.exhibition.modules.account.form.*;
import com.exhibition.modules.professor.Professor;
import com.exhibition.modules.professor.ProfessorForm;
import com.exhibition.modules.professor.ProfessorRepository;
import com.exhibition.modules.student.Student;
import com.exhibition.modules.student.StudentRepository;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.major.Major;
import com.exhibition.modules.major.MajorForm;
import com.exhibition.modules.major.MajorRepository;
import com.exhibition.modules.account.validator.PasswordFormValidator;
import com.exhibition.modules.tag.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.exhibition.modules.account.SettingsController.ROOT;
import static com.exhibition.modules.account.SettingsController.SETTINGS;


@Controller
@RequiredArgsConstructor
@RequestMapping(ROOT + SETTINGS)
public class SettingsController {

    static final String ROOT = "/";
    static final String SETTINGS = "settings";
    static final String PROFILE = "/profile";
    static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";
    static final String TAGS = "/tags";
    static final String MAJORS = "/majors";
    static final String STUDENTS = "/students";
    static final String PROFESSORS = "/professors";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final MajorRepository majorRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordFormValidator());
    }


    /**프로필**/
    @GetMapping(PROFILE)
    public String updateProfileForm(@CurrentAccount Account account, Model model){ //프로필은 현재유저인 로그인된 사용자 자신이 업데이트 한다
        model.addAttribute(account);
        //model.addAttribute(new Profile(account));
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS + PROFILE;
    }

    @PostMapping(PROFILE)
    public String updateProfile(@CurrentAccount Account account, @Valid @ModelAttribute Profile profile, Errors errors, Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS + PROFILE;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message","프로필을 수정했습니다.");
        return "redirect:/" + SETTINGS + PROFILE;
    }


    /**비밀번호 변경**/
    @GetMapping(PASSWORD)
    public String updatePasswordForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS + PASSWORD;
    }

    @PostMapping(PASSWORD)
    public String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attributes){

        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS + PASSWORD;
        }

        accountService.updatePassword(account,passwordForm.getNewPassword());
        attributes.addFlashAttribute("message","패스워드를 변경했습니다.");
        return "redirect:/" + SETTINGS + PASSWORD;
    }


    /**알림 설정**/
    @GetMapping(NOTIFICATIONS)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        //model.addAttribute(new Notifications(account));
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS + NOTIFICATIONS;

    }

    @PostMapping(NOTIFICATIONS)
    public String updateNotifications(@CurrentAccount Account account, @Valid Notifications notifications, Errors errors, Model model, RedirectAttributes attributes){

        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS + NOTIFICATIONS;
        }

        accountService.updateNotifications(account,notifications);
        attributes.addFlashAttribute("message","알림설정를 변경했습니다.");
        return "redirect:/" + SETTINGS + NOTIFICATIONS;
    }


    /**관심 주제 태그**/
    @GetMapping(TAGS)
    public String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList())); //태크목록을 스트링으로

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList()); //관심주제 자동완성
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allTags));
        return SETTINGS + TAGS;
    }

    @PostMapping(TAGS + "/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
        }

        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(TAGS + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    /**학과**/
    @GetMapping(MAJORS)
    public String updateMajorsForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Major> majors = accountService.getMajors(account);
        model.addAttribute("majors", majors.stream().map(Major::toString).collect(Collectors.toList()));

        List<String> allMajors = majorRepository.findAll().stream().map(Major::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allMajors));

        return SETTINGS + MAJORS;
    }

    @PostMapping(MAJORS + "/add")
    @ResponseBody
    public ResponseEntity addMajor(@CurrentAccount Account account, @RequestBody MajorForm majorForm) {
        Major major = majorRepository.findByCollageAndDepartment(majorForm.getCollageName(), majorForm.getDepartmentName());
        if (major == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addMajor(account, major);
        return ResponseEntity.ok().build();
    }

    @PostMapping(MAJORS + "/remove")
    @ResponseBody
    public ResponseEntity removeMajor(@CurrentAccount Account account, @RequestBody MajorForm majorForm) {
        Major major = majorRepository.findByCollageAndDepartment(majorForm.getCollageName(), majorForm.getDepartmentName());
        if (major == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeMajor(account, major);
        return ResponseEntity.ok().build();
    }

    /**학생**/
    @GetMapping(STUDENTS)
    public String updateStudents(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Student> students = accountService.getStudents(account);
        model.addAttribute("students", students.stream().map(Student::getName).collect(Collectors.toList())); //태크목록을 스트링으로

        List<String> allStudents = studentRepository.findAll().stream().map(Student::getName).collect(Collectors.toList()); //관심주제 자동완성
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allStudents));
        return SETTINGS + STUDENTS;
    }



    @PostMapping(STUDENTS + "/add")
    @ResponseBody
    public ResponseEntity addStudent(@CurrentAccount Account account, @RequestBody StudentForm studentForm) {
        String name = studentForm.getStudentName();

        Student student = studentRepository.findByName(name);
        if(student == null){
            student = studentRepository.save(Student.builder().name(studentForm.getStudentName()).build());
        }

        accountService.addStudent(account, student);
        return ResponseEntity.ok().build();
    }


    @PostMapping(STUDENTS + "/remove")
    @ResponseBody
    public ResponseEntity removeStudent(@CurrentAccount Account account, @RequestBody StudentForm studentForm) {
        String name = studentForm.getStudentName();

        Student student = studentRepository.findByName(name);
        if(student == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeStudent(account, student);
        return ResponseEntity.ok().build();
    }

    /**교수님**/
    @GetMapping(PROFESSORS)
    public String updateProfessorsForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Professor> professors = accountService.getProfessors(account);
        model.addAttribute("professors", professors.stream().map(Professor::toString).collect(Collectors.toList()));

        List<String> allProfessors = professorRepository.findAll().stream().map(Professor::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allProfessors));

        return SETTINGS + PROFESSORS;
    }

    @PostMapping(PROFESSORS + "/add")
    @ResponseBody
    public ResponseEntity addProfessor(@CurrentAccount Account account, @RequestBody ProfessorForm professorForm) {
        Professor professor = professorRepository.findByName(professorForm.getName());
        if (professor == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addProfessor(account, professor);
        return ResponseEntity.ok().build();
    }

    @PostMapping(PROFESSORS + "/remove")
    @ResponseBody
    public ResponseEntity removeProfessor(@CurrentAccount Account account, @RequestBody ProfessorForm professorForm) {
        Professor professor = professorRepository.findByName(professorForm.getName());
        if (professor == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeProfessor(account, professor);
        return ResponseEntity.ok().build();
    }


}
