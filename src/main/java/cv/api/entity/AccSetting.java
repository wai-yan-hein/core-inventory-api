/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Data
@Entity
@Table(name = "acc_setting")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccSetting {
    @EmbeddedId
    @Column(name = "type")
    private AccKey key;
    @Column(name = "source_acc")
    private String sourceAcc;
    @Column(name = "pay_acc")
    private String payAcc;
    @Column(name = "dis_acc")
    private String discountAcc;
    @Column(name = "bal_acc")
    private String balanceAcc;
    @Column(name = "tax_acc")
    private String taxAcc;
    @Column(name = "comm_acc")
    private String commAcc;
    @Column(name = "dep_code")
    private String deptCode;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
}
