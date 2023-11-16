package com.exhibition.modules.exhibit.validator;

import com.exhibition.modules.exhibit.ExhibitRepository;
import com.exhibition.modules.exhibit.form.ExhibitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ExhibitFormValidator implements Validator {

    private final ExhibitRepository exhibitRepository;


    @Override
    public boolean supports(Class<?> aClass) {

        return ExhibitForm.class.isAssignableFrom(aClass);
    }

    //path값 중복확인
    @Override
    public void validate(Object target, Errors errors) {
        ExhibitForm exhibitForm = (ExhibitForm)target;
        if(exhibitRepository.existsByPath(exhibitForm.getPath())){
            errors.rejectValue("path", "wrong.path","해당 경로 값을 사용할 수 없습니다.");
        }
    }
}
