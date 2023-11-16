package com.exhibition.infra.config;

import com.exhibition.modules.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity //시큐리티설정을 직접하겠다
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")	//PROFESSOR 권한이 있는 경우 접근 허용
                .antMatchers("/professor/**").hasRole("PROFESSOR")	//PROFESSOR 권한이 있는 경우 접근 허용
                .mvcMatchers("/","/exhibition/{exhibit.path}","/login","/sign-up","/check-email-token",
                        "/email-login","/check-email-login","/login-link", "/search/exhibit").permitAll() //get,post 전부허용
                .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll() //get 만허용
                .anyRequest().authenticated(); //로그인해야만 허용

        http.formLogin()
                .loginPage("/login").permitAll(); //로그인페이지 접근허용

        http.logout()
                .logoutSuccessUrl("/"); //로그아웃했을때 어디로 갈지 경로

        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository());
    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**") ///node_modules/로시작하는 것들 무시
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //이미지 보이게 하기 위한 코드
    }
}
