package com.exhibition.modules.major;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Major {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String collage;

    @Column(nullable = false)
    private String department;

    @Override
    public String toString() {
        return String.format("%s/%s",collage,department);

    }
}
