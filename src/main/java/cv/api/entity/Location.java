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

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "location")
public class Location {
    @EmbeddedId
    private LocationKey key;
    @Column(name = "loc_name", nullable = false, length = 50, unique = true)
    private String locName;
    @Column(name = "parent")
    private String parentCode;
    @Column(name = "calc_stock")
    private boolean calcStock;
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
    @Column(name = "map_dept_id")
    private Integer mapDeptId;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "dept_code")
    private String deptCode;
}
