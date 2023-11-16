package com.exhibition.modules.file;

import com.exhibition.modules.account.Account;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "idx")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File {


    @Id
    @GeneratedValue
    private Long idx; //인덱스

    private String fileName;
}
