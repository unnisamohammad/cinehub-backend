package com.razkart.cinehub.event.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Cast member for an event.
 */
@Entity
@Table(name = "event_cast")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "person_name", nullable = false, length = 100)
    private String personName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private RoleType roleType;

    @Column(name = "character_name", length = 100)
    private String characterName;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}
