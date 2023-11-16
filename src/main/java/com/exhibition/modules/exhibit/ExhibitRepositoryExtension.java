package com.exhibition.modules.exhibit;

import com.exhibition.modules.student.Student;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.major.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ExhibitRepositoryExtension {
    Page<Exhibit> findByKeyword(String keyword, Pageable pageable);

    List<Exhibit> findByAccount(Set<Tag> tags, Set<Major> majors);

    List<Exhibit> findAccountByMajor(Set<Major> majors);

    List<Exhibit> findByStudent(Set<Student> students);
}
