/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "location")
public class Location implements java.io.Serializable {
    @EmbeddedId
    private LocationKey key;
    @Column(name = "loc_name", nullable = false, length = 50, unique = true)
    private String locName;
    @Column(name = "parent")
    private String parentCode;
    @Column(name = "calc_stock")
    private boolean calcStock;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
}
