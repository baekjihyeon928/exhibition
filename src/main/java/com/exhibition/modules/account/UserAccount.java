package com.exhibition.modules.account;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class UserAccount extends User {

    /**
    private Account account;

    public UserAccount(Account account){
            super(account.getId(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
            this.account = account;
    }**/

    private Account account;

    public UserAccount(Account account) {
        super(account.getId(), account.getPassword(), authorities(account));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (account.isProfessor()) {
            //authorities.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
            List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
        }
        else {
            //authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }

}
