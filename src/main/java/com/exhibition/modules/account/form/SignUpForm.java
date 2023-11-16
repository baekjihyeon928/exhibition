package com.exhibition.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {


    @NotBlank
    @Length(min=4,max=16)
    @Pattern(regexp = "^[a-z0-9]{4,16}$")
    private String id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min=8,max=50)
    private String password;

    @NotBlank
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣]{1,20}$")
    private String name;

}
