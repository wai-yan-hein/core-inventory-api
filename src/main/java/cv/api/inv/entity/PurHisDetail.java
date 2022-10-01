/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "pur_his_detail")
public class PurHisDetail implements Serializable {

    @EmbeddedId
    private PurDetailKey pdKey;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code", referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code", referencedColumnName = "comp_code")
    })
    private Stock stock;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "std_wt", nullable = false)
    private Float stdWeight;
    @ManyToOne
    @JoinColumn(name = "pur_unit", nullable = false)
    private StockUnit purUnit;
    @Column(name = "avg_wt")
    private Float avgWeight;
    @Column(name = "avg_price")
    private Float avgPrice;
    @Column(name = "pur_price", nullable = false)
    private Float price;
    @Column(name = "pur_amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
