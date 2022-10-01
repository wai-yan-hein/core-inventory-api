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

import javax.persistence.*;
import java.util.Date;

/**
 * @author wai yan
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "stock")
public class Stock implements java.io.Serializable {

    @EmbeddedId
    private StockKey key;
    @Column(name = "active")
    private boolean active;
    @ManyToOne
    @JoinColumn(name = "brand_code")
    private StockBrand brand;
    @Column(name = "stock_name")
    private String stockName;
    @ManyToOne
    @JoinColumn(name = "category_code")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "stock_type_code")
    private StockType stockType;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "short_name")
    private String shortName;
    @Column(name = "pur_wt")
    private Float purWeight;
    @Column(name = "pur_price")
    private Float purPrice;
    @ManyToOne
    @JoinColumn(name = "pur_unit")
    private StockUnit purUnit;
    @Column(name = "sale_wt")
    private Float saleWeight;
    @ManyToOne
    @JoinColumn(name = "sale_unit")
    private StockUnit saleUnit;
    @Temporal(TemporalType.DATE)
    @Column(name = "licence_exp_date")
    private Date expireDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "sale_price_n")
    private Float salePriceN;
    @Column(name = "sale_price_a")
    private Float salePriceA;
    @Column(name = "sale_price_b")
    private Float salePriceB;
    @Column(name = "sale_price_c")
    private Float salePriceC;
    @Column(name = "sale_price_d")
    private Float salePriceD;
    @Column(name = "sale_price_e")
    private Float salePriceE;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "mig_code")
    private String migCode;
    @Column(name = "user_code")
    private String userCode;
    @ManyToOne
    @JoinColumn(name = "rel_code")
    private UnitRelation unitRelation;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "calculate")
    private boolean calculate;


    public Stock(StockKey key) {
        this.key = key;
    }
}
