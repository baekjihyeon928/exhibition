package com.exhibition.modules.account;

import com.exhibition.modules.account.form.SignUpForm;
import com.exhibition.infra.config.AppProperties;
import com.exhibition.modules.professor.Professor;
import com.exhibition.modules.student.Student;
import com.exhibition.modules.tag.Tag;
import com.exhibition.infra.mail.EmailMessage;
import com.exhibition.infra.mail.EmailService;
import com.exhibition.modules.major.Major;
import com.exhibition.modules.account.form.Notifications;
import com.exhibition.modules.account.form.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service("AccountDetailsService")
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    //private final JavaMailSender javaMailSender;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processNewAccount(SignUpForm signUpForm) {
        //새 계정 signUpForm을 가지고 저장
        Account newAccount = saveNewAccount(signUpForm);

        //이메일 체크 토큰생성
        //newAccount.generateEmailCheckToken();

        //가입확인 이메일 보냄
        sendSignUpConfirmEmail(newAccount);

        return newAccount;

    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);

        //Account account = Account.builder()
        //        .email(signUpForm.getEmail())
        //        .id(signUpForm.getId())
        //        .password(passwordEncoder.encode(signUpForm.getPassword())) //비밀번호 인코딩 AppConfig에서의 PasswordEncoder
        //        .name(signUpForm.getName())
        //        .artAllowedByWeb(true)
        //        .build();
        //Account newAccount = accountRepository.save(account);
        //return newAccount;

        account.generateEmailCheckToken(); //만들때 이메일 체크 토큰 생성
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("name", newAccount.getName());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "서경대학교 전시회에 참여하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("서경대학교 전시회, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);

        //SimpleMailMessage mailMessage = new SimpleMailMessage();
        //mailMessage.setTo(newAccount.getEmail());
        //mailMessage.setSubject("웹 전시회, 회원 가입 인증"); //메일 제목
        //mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail()); //메일 본문
        //javaMailSender.send(mailMessage);
    }

    public void login(Account account) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            new UserAccount(account),
            //account.getId(),
            account.getPassword(),//인코딩한 패스워드 사용
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(token);


        /**UsernamePasswordAuthenticationToken token;
        if(("professor").equals(account.getId())){
            token = new UsernamePasswordAuthenticationToken(
                    new UserAccount(account),
                    account.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR")));
        }
        else {
            token = new UsernamePasswordAuthenticationToken(
                    new UserAccount(account),
                    account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));

        }
        SecurityContextHolder.getContext().setAuthentication(token);**/


    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrId); //이메일로 찾아보고
        if(account == null){
            account = accountRepository.findById(emailOrId); //아이디로도찾아보기
        }

        if(account == null){
            throw  new UsernameNotFoundException(emailOrId);
        }

        return new UserAccount(account);

    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account); //로그인
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile,account); // map(source,destination) source에 있는 값을 destination으로 넣어줌

        //account.setBio(profile.getBio()); 위 코드와 동일
        //account.setUrl(profile.getUrl());
        //account.setMajor(profile.getMajor());
        //account.setInterest(profile.getInterest());
        //account.setArtName(profile.getArtName());
        //account.setArtBio(profile.getArtBio());
        //account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account); //기존 데이터를 위의 내용으로 업데이트 시켜줌 (merge)
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications,account);

        //account.setArtAllowedByEmail(notifications.isArtAllowedByEmail());
        //account.setArtAllowedByWeb(notifications.isArtAllowedByWeb());
        accountRepository.save(account);
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("name", account.getName());
        context.setVariable("linkName", "이메일로 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("서경대학교 전시회, 로그인 링크")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

        //account.generateEmailCheckToken();
        //SimpleMailMessage mailMessage = new SimpleMailMessage();
        //mailMessage.setTo(account.getEmail());
        //mailMessage.setSubject("전시회, 로그인 링크"); //제목
        //mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        //javaMailSender.send(mailMessage);
    }

    /**관심 주제 태그**/
    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getIdx()); //Optional<>의 findById 사용
        byId.ifPresent(a -> a.getTags().add(tag));

    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }

    /**학과 선택 태그**/
    public Set<Major> getMajors(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        return byId.orElseThrow().getMajors();
    }

    public void addMajor(Account account, Major major) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        byId.ifPresent(a -> a.getMajors().add(major));
    }

    public void removeMajor(Account account, Major major) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        byId.ifPresent(a -> a.getMajors().remove(major));
    }

    /**학생**/
    public void addStudent(Account account, Student student) {
        Optional<Account> byId = accountRepository.findById(account.getIdx()); //Optional<>의 findById 사용
        byId.ifPresent(a -> a.getStudents().add(student));
    }


    public Set<Student> getStudents(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        return byId.orElseThrow().getStudents();
    }


    public void removeStudent(Account account, Student student) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        byId.ifPresent(a -> a.getStudents().remove(student));
    }

    /**교수님**/
    public Set<Professor> getProfessors(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        return byId.orElseThrow().getProfessors();
    }

    public void addProfessor(Account account, Professor professor) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        byId.ifPresent(a -> a.getProfessors().add(professor));
    }

    public void removeProfessor(Account account, Professor professor) {
        Optional<Account> byId = accountRepository.findById(account.getIdx());
        byId.ifPresent(a -> a.getProfessors().remove(professor));
    }

}
