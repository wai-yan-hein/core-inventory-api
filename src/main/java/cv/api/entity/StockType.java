/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "stock_type")
public class StockType {

    @EmbeddedId
    private StockTypeKey key;
    @Column(name = "stock_type_name")
    private String stockTypeName;
    @Column(name = "account_id")
    private String account;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "active")
    private boolean active;
    @Column(name = "finished_group")
    private boolean finishedGroup;
    @Column(name = "group_type")
    private int groupType;
}
