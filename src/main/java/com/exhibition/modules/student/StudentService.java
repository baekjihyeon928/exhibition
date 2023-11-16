package com.exhibition.modules.student;

import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student findOrCreateNew(String studentName) {
        Student student = studentRepository.findByName(studentName);
        if (student == null) {
            student = studentRepository.save(Student.builder().name(studentName).build());
        }
        return student;
    }

}
