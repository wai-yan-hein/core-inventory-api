/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "sale_his")
public class SaleHis {

    @EmbeddedId
    private SaleHisKey key;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "saleman_code")
    private String saleManCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "credit_term")
    private Date creditTerm;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
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
    private Float taxPercent;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "vou_balance")
    private Float balance;
    @Temporal(TemporalType.DATE)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "reg_code")
    private String regionCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "order_no")
    private String orderNo;
    @Column(name = "project_no")
    private String projectNo;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<SaleHisDetail> listSH;
    @Transient
    private List<SaleDetailKey> listDel;
    @Transient
    private boolean backup;
    @Transient
    private List<String> location;

    public SaleHis() {
    }

    public SaleHis(Date updatedDate, List<String> location) {
        this.updatedDate = updatedDate;
        this.location = location;
    }
}