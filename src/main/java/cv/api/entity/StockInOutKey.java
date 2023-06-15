/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
@Embeddable
public class StockInOutKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
}
