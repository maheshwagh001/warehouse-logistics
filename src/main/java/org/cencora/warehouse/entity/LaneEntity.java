package org.cencora.warehouse.entity;

import jakarta.persistence.*;
import org.cencora.common.entity.BaseEntity;

@Entity
@Table(name = "lanes")
public class LaneEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lane_id")
    private Long laneId;

    @Column(name = "lane_name", nullable = false, length = 50)
    private String laneName;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "current_pallet_count")
    private Integer currentPalletCount = 0;

    @Column(name = "avg_dwell_time")
    private Integer avgDwellTime;

    // ---- Relations ----

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private ZoneEntity zone;

    // getters & setters
}
