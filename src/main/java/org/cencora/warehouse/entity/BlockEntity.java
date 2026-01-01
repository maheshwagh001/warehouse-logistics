package org.cencora.warehouse.entity;
import jakarta.persistence.*;
import org.cencora.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "blocks")
public class BlockEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long blockId;

    @Column(name = "block_name", nullable = false, length = 50)
    private String blockName;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "current_filled")
    private Integer currentFilled = 0;

    @Column(name = "percentage_filled", precision = 5, scale = 2)
    private BigDecimal percentageFilled;

    // ---- Relations ----

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private WarehouseFloorEntity floor;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<ZoneEntity> zones;

    // getters & setters
}
