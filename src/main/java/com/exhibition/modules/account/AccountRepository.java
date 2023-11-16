package com.exhibition.modules.account;

import com.exhibition.modules.exhibit.Exhibit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);

    boolean existsById(String id);

    Account findByEmail(String email);

    Account findById(String id);

    @EntityGraph(attributePaths = {"tags", "majors"})
    Account findAccountWithTagsAndMajorsById(String id);

    @EntityGraph(attributePaths = {"majors"})
    Account findAccountWithMajorsByIdx(Long idx);

    @EntityGraph(attributePaths = {"students"})
    Account findAccountWithStudentsByIdx(Long idx);

    @EntityGraph(attributePaths = {"professors"}) //교수별로
    Account findAccountWithProfessorsByIdx(Long idx);
}

