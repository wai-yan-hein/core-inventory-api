/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.view;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Lenovo
 */
@Data
@Entity
@Immutable
@Table(name = "v_return_in")
public class VReturnIn implements Serializable {

    @Id
    @Column(name = "rd_code")
    private String rdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "trader_code")
    private String traderCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "disc_p")
    private Float discountPrice;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "balance")
    private Float vouBalance;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "session_id")
    private Integer sessionId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "wt")
    private Float wt;
    @Column(name = "unit")
    private String unit;
    @Column(name = "price")
    private Float price;
    @Column(name = "amt")
    private Float amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "stock_name")
    private String stockName;
    @Column(name = "loc_name")
    private String locationName;
    @Column(name = "comp_name")
    private String compName;
    @Column(name = "comp_phone")
    private String compPhone;
    @Column(name = "comp_address")
    private String compAddress;
    @Column(name = "created_name")
    private String createdName;
    @Column(name = "updated_name")
    private String updatedName;
}
