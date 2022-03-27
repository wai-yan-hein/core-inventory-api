/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author wai yan
 */
@Data
@Entity
@Table(name = "ret_out_his_detail")
public class RetOutHisDetail implements java.io.Serializable {

    @EmbeddedId
    private RetOutKey roKey;
    @ManyToOne
    @JoinColumn(name = "stock_code", nullable = false)
    private Stock stock;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumn(name = "unit", nullable = false)
    private StockUnit unit;
    @Column(name = "price", nullable = false)
    private Float price;
    @Column(name = "amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "wt")
    private Float wt;

}
