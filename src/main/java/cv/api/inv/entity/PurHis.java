/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "pur_his")
public class PurHis implements java.io.Serializable {

    @EmbeddedId
    private PurHisKey key;
    @Column(name = "trader_code")
    private String traderCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;
    @Column(name = "loc_code")
    private String locCode;
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
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "disc_p")
    private Float discP;
    @Column(name = "tax_p")
    private Float taxP;
    @Column(name = "tax_amt")
    private Float taxAmt;
    @Column(name = "reference")
    private String reference;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<PurHisDetail> listPD;
    @Transient
    private List<String> listDel;
    @Transient
    private List<String> location;

    public PurHis() {
    }

    public PurHis(Date updatedDate, List<String> location) {
        this.updatedDate = updatedDate;
        this.location = location;
    }
}
