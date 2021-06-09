/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class PurDetailKey implements Serializable {

    @Column(name = "vou_id", length = 15)
    private String vouId;
    @Column(name = "pur_detail_code", length = 20)
    private String purDetailId;

    public PurDetailKey() {
    }

    public String getVouId() {
        return vouId;
    }

    public void setVouId(String vouId) {
        this.vouId = vouId;
    }

    public String getPurDetailId() {
        return purDetailId;
    }

    public void setPurDetailId(String purDetailId) {
        this.purDetailId = purDetailId;
    }

    public PurDetailKey(String vouId, String purDetailId) {
        this.vouId = vouId;
        this.purDetailId = purDetailId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.vouId);
        hash = 67 * hash + Objects.hashCode(this.purDetailId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PurDetailKey other = (PurDetailKey) obj;
        if (!Objects.equals(this.vouId, other.vouId)) {
            return false;
        }
        return Objects.equals(this.purDetailId, other.purDetailId);
    }
    
}
