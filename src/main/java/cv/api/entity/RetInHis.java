/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author WSwe
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "ret_in_his")
public class RetInHis implements java.io.Serializable {

    @EmbeddedId
    private RetInHisKey key;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "deleted")
    private boolean deleted;
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
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "disc_p")
    private Float discP;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "project_no")
    private String projectNo;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<RetInHisDetail> listRD;
    @Transient
    private List<RetInKey> listDel;
    @Transient
    private List<String> location;
//    public RetInHis() {
//    }
//
//    public RetInHis(Date updatedDate, List<String> location) {
//        this.updatedDate = updatedDate;
//        this.location = location;
//    }
}
