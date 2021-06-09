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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class StockBalanceTmpKey implements Serializable {

    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @Column(name = "loc_id", nullable = false)
    private Integer locId;
    @Column(name = "mac_id", nullable = false)
    private Integer machineId;

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Integer getLocId() {
        return locId;
    }

    public void setLocId(Integer locId) {
        this.locId = locId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.stock);
        hash = 23 * hash + Objects.hashCode(this.locId);
        hash = 23 * hash + Objects.hashCode(this.machineId);
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
        final StockBalanceTmpKey other = (StockBalanceTmpKey) obj;
        if (!Objects.equals(this.stock, other.stock)) {
            return false;
        }
        if (!Objects.equals(this.locId, other.locId)) {
            return false;
        }
        return Objects.equals(this.machineId, other.machineId);
    }
    

}
