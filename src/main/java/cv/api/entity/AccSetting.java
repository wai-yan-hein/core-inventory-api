/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
@Entity
@Table(name = "acc_setting")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccSetting implements Serializable {
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
}
