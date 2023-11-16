package com.exhibition.modules.professor;

import com.exhibition.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Professor {


    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;


    @Override
    public String toString() {
        return String.format("%s",name);

    }


}
