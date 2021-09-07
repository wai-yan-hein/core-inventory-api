/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.view;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.annotations.Immutable;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Immutable
@Table(name = "v_sale")
public class VSale implements Serializable {

    @Id
    @Column(name = "sd_code")
    private String sdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "saleman_code")
    private String saleManCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "vou_date")
    private Date vouDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "credit_term")
    private Date creditTerm;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "grand_total")
    private Float grandTotal;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "disc_p")
    private Float discountPrice;
    @Column(name = "tax_amt")
    private Float taxAmt;
    @Column(name = "tax_p")
    private Float taxPrice;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "vou_balance")
    private Float vouBalance;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "address")
    private String address;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "reg_code")
    private String regionCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "session_id")
    private Integer sessionId;
    @Column(name = "stock_code")
    private String stockCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expiredDate;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "sale_wt")
    private Float saleWt;
    @Column(name = "sale_unit")
    private String saleUnit;
    @Column(name = "sale_price")
    private Float salePrice;
    @Column(name = "sale_amt")
    private Float saleAmount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "saleman_name")
    private String saleManName;
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
}
