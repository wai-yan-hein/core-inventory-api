/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

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
@Table(name = "unit_relation_detail")
public class UnitRelationDetail implements Serializable {

    @EmbeddedId
    private UnitRelationDetailKey key;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "smallest_qty")
    private Float smallestQty;
}
