/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "stock_in_out")
public class StockInOut implements Serializable {

    @EmbeddedId
    private StockIOKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "description")
    private String description;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "vou_status")
    private String vouStatusCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "vou_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<StockInOutDetail> listSH;
    @Transient
    private List<StockInOutKey> listDel;
    @Transient
    private List<LocationKey> keys;
}
