package com.razkart.cinehub.show.entity;

import com.razkart.cinehub.common.entity.BaseEntity;
import com.razkart.cinehub.event.entity.Event;
import com.razkart.cinehub.venue.entity.Screen;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Show entity representing a showtime for an event.
 */
@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShowStatus status = ShowStatus.SCHEDULED;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShowPricing> pricing = new ArrayList<>();

    public void addPricing(ShowPricing showPricing) {
        pricing.add(showPricing);
        showPricing.setShow(this);
    }

    public boolean isBookable() {
        return this.status == ShowStatus.SCHEDULED &&
               (this.showDate.isAfter(LocalDate.now()) ||
                (this.showDate.isEqual(LocalDate.now()) && this.startTime.isAfter(LocalTime.now())));
    }

    public boolean isCancelled() {
        return this.status == ShowStatus.CANCELLED;
    }

    public boolean isHousefull() {
        return this.status == ShowStatus.HOUSEFULL;
    }
}
