package org.cencora.warehouse.entity;
import jakarta.persistence.*;
import org.cencora.common.entity.BaseEntity;

import java.util.List;

@Entity
@Table(name = "zones")
public class ZoneEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_zone")
    private StorageZone temperatureZone;

    // ---- Relations ----

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private WarehouseFloorEntity floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private BlockEntity block;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
    private List<LaneEntity> lanes;

    // getters & setters
}
