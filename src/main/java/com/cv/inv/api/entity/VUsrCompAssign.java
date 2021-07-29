/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_usr_comp_assign")
public class VUsrCompAssign implements java.io.Serializable {

    private UsrCompRoleKey key;
    private String compName;
    private Date finicialPeriodFrom;
    private Date finicialPeriodTo;

    @EmbeddedId
    public UsrCompRoleKey getKey() {
        return key;
    }

    public void setKey(UsrCompRoleKey key) {
        this.key = key;
    }

    @Column(name = "name")
    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finicial_period_from")
    public Date getFinicialPeriodFrom() {
        return finicialPeriodFrom;
    }

    public void setFinicialPeriodFrom(Date finicialPeriodFrom) {
        this.finicialPeriodFrom = finicialPeriodFrom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finicial_period_to")
    public Date getFinicialPeriodTo() {
        return finicialPeriodTo;
    }

    public void setFinicialPeriodTo(Date finicialPeriodTo) {
        this.finicialPeriodTo = finicialPeriodTo;
    }
}
