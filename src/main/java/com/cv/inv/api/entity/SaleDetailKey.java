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
 * @author Mg Kyaw Thura Aung
 */
@Embeddable
public class SaleDetailKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "sd_code")
    private String sdCode;

    public SaleDetailKey() {
    }

    public SaleDetailKey(String vouNo, String sdCode) {
        this.vouNo = vouNo;
        this.sdCode = sdCode;
    }

    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    public String getSdCode() {
        return sdCode;
    }

    public void setSdCode(String sdCode) {
        this.sdCode = sdCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.vouNo);
        hash = 79 * hash + Objects.hashCode(this.sdCode);
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
        final SaleDetailKey other = (SaleDetailKey) obj;
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
        return Objects.equals(this.sdCode, other.sdCode);
    }

}
