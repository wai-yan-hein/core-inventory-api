/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Lenovo
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "stock_unit")
public class StockUnit implements java.io.Serializable {

    @Id
    @Column(name = "unit_code", unique = true, nullable = false, length = 10)
    private String unitCode;
    @Column(name = "unit_name", nullable = false, length = 45, unique = true)
    private String unitName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "comp_code")
    private String compCode;

    public StockUnit(String unitCode) {
        this.unitCode = unitCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockUnit stockUnit = (StockUnit) o;
        return unitCode != null && Objects.equals(unitCode, stockUnit.unitCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
