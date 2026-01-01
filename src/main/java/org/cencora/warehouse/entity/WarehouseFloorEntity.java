package org.cencora.warehouse.entity;

import jakarta.persistence.*;
import org.cencora.common.entity.BaseEntity;

import java.util.List;

@Entity
@Table(name = "warehouse_floors")
public class WarehouseFloorEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "floor_id")
    private Long floorId;

    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "floor_name", nullable = false)
    private StorageZone floorName;

    @Column(name = "description")
    private String description;

    // ---- Relations ----

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<BlockEntity> blocks;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<ZoneEntity> zones;

    // getters & setters
}
