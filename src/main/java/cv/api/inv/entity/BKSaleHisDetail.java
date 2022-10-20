/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "bk_sale_his_detail")
public class BKSaleHisDetail implements java.io.Serializable {
    @EmbeddedId
    private BKSaleDetailKey sdKey;
    @Column(name = "log_id")
    private Long logId;
    @Column(name = "stock_code")
    private String stockCode;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "sale_unit")
    private String unitCode;
    @Column(name = "sale_price", nullable = false)
    private Float price;
    @Column(name = "sale_amt", nullable = false)
    private Float amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
}
