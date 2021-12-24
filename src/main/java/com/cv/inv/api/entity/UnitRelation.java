/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "unit_relation")
public class UnitRelation implements Serializable {

    @EmbeddedId
    private RelationKey unitKey;
    @Column(name = "factor")
    private Float factor;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    public RelationKey getUnitKey() {
        return unitKey;
    }

    public void setUnitKey(RelationKey unitKey) {
        this.unitKey = unitKey;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
