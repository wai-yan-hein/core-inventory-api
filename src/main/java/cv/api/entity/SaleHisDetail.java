/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "sale_his_detail")
public class SaleHisDetail {

    @EmbeddedId
    private SaleDetailKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Double qty;
    @Column(name = "sale_unit")
    private String unitCode;
    @Column(name = "sale_price", nullable = false)
    private Double price;
    @Column(name = "sale_amt", nullable = false)
    private Double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "std_weight")
    private Double stdWeight;
    @Column(name = "total_weight")
    private Double totalWeight;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
    @Transient
    private String relName;
    @Transient
    private String locName;
    @Transient
    private String traderName;
}
