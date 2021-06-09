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
public class RetInCompoundKey implements Serializable {

    @Column(name = "ret_in_detail_id", unique = true, nullable = false)
    private String retInDetailId;
    @Column(name = "voucher_no")
    private String vouNo;

    public RetInCompoundKey() {
    }

    public RetInCompoundKey(String retInDetailId,String vouNo) {
        this.retInDetailId = retInDetailId;
        this.vouNo = vouNo;
    }

    public String getRetInDetailId() {
        return retInDetailId;
    }

    public void setRetInDetailId(String retInDetailId) {
        this.retInDetailId = retInDetailId;
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
        hash = 41 * hash + Objects.hashCode(this.retInDetailId);
        hash = 41 * hash + Objects.hashCode(this.vouNo);
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
        final RetInCompoundKey other = (RetInCompoundKey) obj;
        if (!Objects.equals(this.retInDetailId, other.retInDetailId)) {
            return false;
        }
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
       
        return true;
    }
    

}
