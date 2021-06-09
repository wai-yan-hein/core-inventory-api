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
 * @author lenovo
 */
@Embeddable
public class RetOutCompoundKey implements Serializable {

    @Column(name = "ret_out_detail_id", unique = true, nullable = false)
    private String retOutDetailId;
    @Column(name = "voucher_no")
    private String vouNo;

    public RetOutCompoundKey() {
    }

    public RetOutCompoundKey(String retOutDetailId,String vouNo) {
        this.retOutDetailId = retOutDetailId;
        this.vouNo = vouNo;
    }

    public String getRetOutDetailId() {
        return retOutDetailId;
    }

    public void setRetOutDetailId(String retOutDetailId) {
        this.retOutDetailId = retOutDetailId;
    }

    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.retOutDetailId);
        hash = 59 * hash + Objects.hashCode(this.vouNo);
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
        final RetOutCompoundKey other = (RetOutCompoundKey) obj;
        if (!Objects.equals(this.retOutDetailId, other.retOutDetailId)) {
            return false;
        }
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
        return true;
    }
    
}
