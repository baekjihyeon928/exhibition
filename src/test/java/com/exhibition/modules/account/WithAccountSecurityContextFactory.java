package com.exhibition.modules.account;

import com.exhibition.modules.account.WithAccount;
import com.exhibition.modules.account.form.SignUpForm;
import com.exhibition.modules.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String id = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setId(id);
        signUpForm.setEmail(id + "@email.com");
        signUpForm.setPassword("12345678");
        signUpForm.setName("jihyeon");
        accountService.processNewAccount(signUpForm);

        UserDetails principal = accountService.loadUserByUsername(id);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
