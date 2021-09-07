package com.cv.inv.api.view;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "v_purchase")
public class VPurchase implements Serializable {
    @Id
    @Column(name = "pd_code")
    private String pdCode;
    @Column(name = "balance")
    private Float balance;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Column(name = "pur_exp_total")
    private Float purExpTotal;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "vou_date")
    @Temporal(TemporalType.DATE)
    private Date vouDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "disc_p")
    private Float discountPrice;
    @Column(name = "tax_p")
    private Float taxPrice;
    @Column(name = "tax_amt")
    private Float taxAmount;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "exp_date")
    @Temporal(TemporalType.DATE)
    private Date expDate;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "std_wt")
    private Float stdWt;
    @Column(name = "avg_price")
    private Float avgPrice;
    @Column(name = "pur_unit")
    private String purUnit;
    @Column(name = "avg_wt")
    private Float avgWt;
    @Column(name = "pur_price")
    private Float purPrice;
    @Column(name = "pur_amt")
    private Float purAmount;
    @Column(name = "loc_code")
    private String locationCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "created_name")
    private String createdName;
    @Column(name = "updated_name")
    private String updatedName;
    @Column(name = "cur_name")
    private String curName;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "loc_name")
    private String locationName;
    @Column(name = "comp_name")
    private String compName;
    @Column(name = "comp_phone")
    private String compPhone;
    @Column(name = "comp_address")
    private String compAddress;
    @Column(name = "stock_name")
    private String stockName;
}
