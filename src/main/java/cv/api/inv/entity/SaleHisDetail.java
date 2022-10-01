/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "sale_his_detail")
public class SaleHisDetail implements java.io.Serializable {

    @EmbeddedId
    private SaleDetailKey sdKey;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code",referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code",referencedColumnName = "comp_code")
    })
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumn(name = "sale_unit", nullable = false)
    private StockUnit saleUnit;
    @Column(name = "sale_price", nullable = false)
    private Float price;
    @Column(name = "sale_amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "sale_wt")
    private Float saleWt;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
