/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author WSwe
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "ret_out_his")
public class RetOutHis implements java.io.Serializable {

    @Id
    @Column(name = "vou_no", unique = true, nullable = false, length = 15)
    private String vouNo;
    @ManyToOne
    @JoinColumn(name = "trader_code")
    private Trader trader;
    @Temporal(TemporalType.DATE)
    @Column(name = "vou_date")
    private Date vouDate;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "balance")
    private Float balance;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private Integer session;
    @ManyToOne
    @JoinColumn(name = "cur_code")
    private Currency currency;
    @Column(name = "disc_p")
    private Float discP;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "comp_code")
    private String compCode;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<RetOutHisDetail> listRD;
    @Transient
    private List<String> listDel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RetOutHis retOutHis = (RetOutHis) o;
        return vouNo != null && Objects.equals(vouNo, retOutHis.vouNo);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
