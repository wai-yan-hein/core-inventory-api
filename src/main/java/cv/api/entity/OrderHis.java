/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "order_his")
public class OrderHis {

    @EmbeddedId
    private OrderHisKey key;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "saleman_code")
    private String saleManCode;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "credit_term", columnDefinition = "TIMESTAMP")
    private LocalDateTime creditTerm;
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
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "order_status")
    private String orderStatus;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<OrderHisDetail> listSH;
    @Transient
    private List<OrderDetailKey> listDel;
    @Transient
    private boolean backup;
    @Transient
    private List<String> location;
}
