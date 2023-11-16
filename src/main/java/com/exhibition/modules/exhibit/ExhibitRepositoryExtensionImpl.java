package com.exhibition.modules.exhibit;

import com.exhibition.modules.major.QMajor;
import com.exhibition.modules.student.QStudent;
import com.exhibition.modules.student.Student;
import com.exhibition.modules.tag.QTag;
import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.major.Major;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class ExhibitRepositoryExtensionImpl extends QuerydslRepositorySupport implements ExhibitRepositoryExtension {

    public  ExhibitRepositoryExtensionImpl(){
        super(Exhibit.class);
    }

    @Override
    public Page<Exhibit> findByKeyword(String keyword, Pageable pageable) {
        QExhibit exhibit = QExhibit.exhibit;
        JPQLQuery<Exhibit> query = from(exhibit).where(exhibit.published.isTrue() //공개된 작품중에서
                .and(exhibit.title.containsIgnoreCase(keyword)) //타이틀이 키워드를 가지고 있거나
                .or(exhibit.name.containsIgnoreCase(keyword)) //이름에 키워드가 있거나
                .or(exhibit.tags.any().title.containsIgnoreCase(keyword)) //태그에 키워드가 있거나
                .or(exhibit.majors.any().department.containsIgnoreCase(keyword))) //전공에 키워드가 있으면
                .leftJoin(exhibit.tags, QTag.tag).fetchJoin()
                .leftJoin(exhibit.majors, QMajor.major).fetchJoin()
                .distinct();
        JPQLQuery<Exhibit> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Exhibit> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Exhibit> findByAccount(Set<Tag> tags, Set<Major> majors) {
        QExhibit exhibit = QExhibit.exhibit;
        JPQLQuery<Exhibit> query = from(exhibit).where(exhibit.published.isTrue()
                .and(exhibit.closed.isFalse())
                .and(exhibit.tags.any().in(tags))
                .and(exhibit.majors.any().in(majors)))
                .leftJoin(exhibit.tags, QTag.tag).fetchJoin()
                .leftJoin(exhibit.majors, QMajor.major).fetchJoin()
                .orderBy(exhibit.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

    @Override
    public List<Exhibit> findAccountByMajor(Set<Major> majors) {
        QExhibit exhibit = QExhibit.exhibit;
        JPQLQuery<Exhibit> query = from(exhibit).where(exhibit.published.isTrue()
                .and(exhibit.closed.isFalse())
                .and(exhibit.majors.any().in(majors)))
                .leftJoin(exhibit.majors, QMajor.major).fetchJoin()
                .orderBy(exhibit.publishedDateTime.desc())
                .distinct();
        return query.fetch();
    }

    @Override
    public List<Exhibit> findByStudent(Set<Student> students) {
        QExhibit exhibit = QExhibit.exhibit;
        JPQLQuery<Exhibit> query = from(exhibit).where(exhibit.published.isTrue()
                .and(exhibit.closed.isFalse())
                .and(exhibit.students.any().in(students)))
                .leftJoin(exhibit.students, QStudent.student).fetchJoin()
                .orderBy(exhibit.publishedDateTime.desc())
                .distinct()
                .limit(30);
        return query.fetch();
    }

}
