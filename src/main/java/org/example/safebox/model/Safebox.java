package org.example.safebox.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="safebox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Generated
public class Safebox {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String password;
    private Integer attempts;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn
    private List<Content> items;

    @Column(name="created_at")
    private LocalDateTime createdAt;

}
