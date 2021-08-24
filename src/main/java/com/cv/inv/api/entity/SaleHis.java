/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Data
@Table(name = "sale_his")
public class SaleHis implements java.io.Serializable {

    @Id
    @Column(name = "vou_no", unique = true, nullable = false, length = 20)
    private String vouNo;
    @ManyToOne
    @JoinColumn(name = "trader_code")
    private Trader trader;
    @ManyToOne
    @JoinColumn(name = "saleman_code")
    private SaleMan saleMan;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sale_date")
    private Date saleDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "credit_term")
    private Date creditTerm;
    @ManyToOne
    @JoinColumn(name = "cur_code")
    private Currency currency;
    @Column(name = "remark")
    private String remark;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "grand_total")
    private Float grandTotal;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "disc_p")
    private Float discP;
    @Column(name = "tax_amt")
    private Float taxAmt;
    @Column(name = "tax_p")
    private Float taxP;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "vou_balance")
    private Float balance;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @Column(name = "session_id")
    private Integer session;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "order_code")
    private String orderCode;
    @ManyToOne
    @JoinColumn(name = "reg_code")
    private Region region;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "comp_code")
    private String compCode;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<SaleHisDetail> listSH;
    @Transient
    private List<String> listDel;

}
