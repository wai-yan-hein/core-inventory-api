/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wai yan
 */
@Data
@Entity
@Table(name = "sale_man")
public class SaleMan implements Serializable {
    @Id
    @Column(name = "saleman_code", unique = true, nullable = false)
    private String saleManCode;
    @Column(name = "saleman_name", unique = true, nullable = false)
    private String saleManName;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "phone")
    private String phone;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
}
