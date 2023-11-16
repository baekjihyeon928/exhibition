package com.exhibition.modules.account;

import com.exhibition.modules.account.form.SignUpForm;
import com.exhibition.modules.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){

        if(errors.hasErrors()){
            return "account/sign-up"; //에러가 있으면 회원가입폼을 다시 보여줌
        }

        Account account = accountService.processNewAccount(signUpForm); //AccountService에서 새 계정 저장, 이메일토큰생성, 이메일 전송 행함
        accountService.login(account);

        //TODO 회원 가입 처리
        return "redirect:/";
    }

    //이메일확인
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        if (account == null) {
            model.addAttribute("error", "wrong.email");
            return view;
        }
        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignUp(account);

        model.addAttribute("numberOfUser",accountRepository.count()); //보이는 뷰를 찍음, 몇번째 가입인지 알려줌
        model.addAttribute("name", account.getName()); //이름 가져와서 알려줌
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{id}")
    public String viewProfile(@PathVariable String id, Model model, @CurrentAccount Account account){
        Account byId = accountRepository.findById(id);
        if(id == null){
            throw new IllegalArgumentException(id + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute(byId); //model.addAttribute("account",byId);
        model.addAttribute("isOwner",byId.equals(account));
        return "account/profile";
    }

    //비밀번호 찾기-이메일 로그인 페이지로 감
    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    //비밀번호 찾기
    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }
        if(!account.canSendConfirmEmail()){
            model.addAttribute("error", "이메일 로그인은 1시간 뒤애 사용할 수 있습니다..");
            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message","이메일 인증 메일을 발송했습니다.");
        return "redirect:/email-login";

    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if (account == null || !account.isValidToken(token)) { //계정이 유효하지 않거나 토큰이 유효하지 않을때
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        accountService.login(account); //로그인
        return view;
    }

}
