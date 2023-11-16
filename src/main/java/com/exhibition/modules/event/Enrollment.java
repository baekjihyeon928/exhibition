package com.exhibition.modules.event;

import com.exhibition.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndExhibit",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "exhibit")
        },
        subgraphs = @NamedSubgraph(name = "exhibit", attributeNodes = @NamedAttributeNode("exhibit"))
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;

}
