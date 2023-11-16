package com.exhibition.modules.professor;

import com.exhibition.modules.account.Account;
import com.exhibition.modules.major.Major;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Professor findByName(String name);

}

