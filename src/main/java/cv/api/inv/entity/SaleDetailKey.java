/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
@Embeddable
public class SaleDetailKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "sd_code")
    private String sdCode;
    @Column(name = "dept_id")
    private Integer deptId;

    public SaleDetailKey(String vouNo, String sdCode, Integer deptId) {
        this.vouNo = vouNo;
        this.sdCode = sdCode;
        this.deptId = deptId;
    }

    public SaleDetailKey() {
    }
}
