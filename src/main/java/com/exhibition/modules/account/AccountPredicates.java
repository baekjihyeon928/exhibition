package com.exhibition.modules.account;

import com.exhibition.modules.tag.Tag;
import com.exhibition.modules.major.Major;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndMajors(Set<Tag> tags, Set<Major> majors) {
        QAccount account = QAccount.account;
        return account.majors.any().in(majors).and(account.tags.any().in(tags));
    }

}
