package com.cv.inv.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "op_his")
public class OPHis implements java.io.Serializable {
    @Id
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "op_date")
    @Temporal(TemporalType.DATE)
    private Date vouDate;
    @Column(name = "remark")
    private String remark;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "op_amt")
    private float opAmt;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "comp_code")
    private String compCode;
    @Transient
    private List<OPHisDetail> detailList;
    @Transient
    private List<String> listDel;
    @Transient
    private String status = "STATUS";

}
