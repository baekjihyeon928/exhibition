package com.exhibition.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {

    @Length(max=35)
    private String bio; //자기소개

    @Length(max=50)
    private String url; //웹사이트 url

    @Length(max=50)
    private String major; //전공

    @Length(max=50)
    private String interest; //관심사

    @Length(max=50)
    private String artName; //작품명

    @Length(max=100)
    private String artBio; //작품소개

    private String profileImage;


}
