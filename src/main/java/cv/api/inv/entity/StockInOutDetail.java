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
import java.io.Serializable;
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
@Table(name = "stock_in_out_detail")
public class StockInOutDetail implements Serializable {

    @EmbeddedId
    private StockInOutKey ioKey;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code",referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code",referencedColumnName = "comp_code")
    })
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "in_qty")
    private Float inQty;
    @Column(name = "in_wt")
    private Float inWt;
    @ManyToOne
    @JoinColumn(name = "in_unit")
    private StockUnit inUnit;
    @Column(name = "out_qty")
    private Float outQty;
    @Column(name = "out_wt")
    private Float outWt;
    @ManyToOne
    @JoinColumn(name = "out_unit")
    private StockUnit outUnit;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "cost_price")
    private Float costPrice;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
