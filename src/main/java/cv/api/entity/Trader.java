/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "trader")
public class Trader {
    @EmbeddedId
    private TraderKey key;
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "address")
    private String address;
    @Column(name = "reg_code")
    private String regCode;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "active")
    private boolean active;
    @Column(name = "remark")
    private String remark;
    @Column(name = "rfid")
    private String rfId;
    @Column(name = "nrc")
    private String nrc;
    @Column(name = "mig_code")
    private String migCode;
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
    @Column(name = "credit_limit")
    private Integer creditLimit;
    @Column(name = "credit_days")
    private Integer creditDays;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "type")
    private String type;
    @Column(name = "cash_down")
    private boolean cashDown;
    @Column(name = "multi")
    private boolean multi;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "price_type")
    private String priceType;
    @Column(name = "group_code")
    private String groupCode;
    @Column(name = "account")
    private String account;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "credit_amt")
    private Float creditAmt;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "country_code")
    private String countryCode;
}
