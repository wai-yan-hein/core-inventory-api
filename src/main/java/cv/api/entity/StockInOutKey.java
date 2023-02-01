/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
@Embeddable
public class StockInOutKey implements Serializable {

    @Column(name = "sd_code", unique = true, nullable = false)
    private String sdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "dept_id")
    private Integer deptId;

    public StockInOutKey(String sdCode, String vouNo, Integer deptId) {
        this.sdCode = sdCode;
        this.vouNo = vouNo;
        this.deptId = deptId;
    }

    public StockInOutKey() {
    }
}
