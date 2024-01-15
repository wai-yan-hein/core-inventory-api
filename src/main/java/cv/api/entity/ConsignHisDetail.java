/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

/**
 * @author pann
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "consign_his_detail")
public class ConsignHisDetail {

    @EmbeddedId
    private ConsignHisDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "wet")
    private Double wet;
    @Column(name = "bag")
    private Double bag;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "rice")
    private Double rice;
    @Column(name = "price")
    private Double price;
    @Column(name = "amount")
    private Double amount;
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
}
