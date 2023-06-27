/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import jakarta.persistence.*;

import java.io.Serializable;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @Column(name = "dept_id")
    private Integer deptId;
}
