package darak.community.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Board {
    @Id @GeneratedValue
    private Long id;

    private String name;

    private String description;
}
