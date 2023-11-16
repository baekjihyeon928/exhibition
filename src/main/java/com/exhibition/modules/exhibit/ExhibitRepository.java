package com.exhibition.modules.exhibit;

import com.exhibition.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ExhibitRepository extends JpaRepository<Exhibit, Long> , ExhibitRepositoryExtension{

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "majors", "owners"}, type = EntityGraph.EntityGraphType.LOAD)
    Exhibit findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "owners"})
    Exhibit findExhibitWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"majors", "owners"})
    Exhibit findExhibitWithMajorsByPath(String path);

    @EntityGraph(attributePaths = "members")
    Exhibit findExhibitWithMembersByPath(String path);

    @EntityGraph(attributePaths = "owners")
    Exhibit findExhibitWithOwnersByPath(String path);

    Exhibit findExhibitOnlyByPath(String path);

    @EntityGraph(attributePaths = {"majors", "tags"})
    Exhibit findExhibitWithTagsAndMajorsById(Long id);

    @EntityGraph(attributePaths = {"majors"})
    Exhibit findExhibitWithMajorsById(Long id);

    @EntityGraph(attributePaths = {"students"})
    Exhibit findExhibitWithStudentsById(Long id);

    @EntityGraph(attributePaths = {"majors", "tags"})
    List<Exhibit> findFirst30ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Exhibit> findByOwnersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    @EntityGraph(attributePaths = "owners")
    Exhibit findExhibitWithOwnersById(Long id);

    @EntityGraph(attributePaths = {"members", "owners"})
    Exhibit findExhibitWithOwnersAndMemebersById(Long id);
}
