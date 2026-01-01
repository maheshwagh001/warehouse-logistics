package org.cencora.warehouse.entity;
import jakarta.persistence.*;
import org.cencora.common.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "warehouses")
public class WarehouseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "latitude", precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "contact_person", length = 255)
    private String contactPerson;

    @Column(name = "contact_number", length = 50)
    private String contactNumber;

    // ===== future mappings (to be added next) =====
     @OneToMany(mappedBy = "warehouse")
     private List<WarehouseFloorEntity> floors;

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<WarehouseFloorEntity> getFloors() {
        return floors;
    }

    public void setFloors(List<WarehouseFloorEntity> floors) {
        this.floors = floors;
    }
}
