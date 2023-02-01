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
public class PurDetailKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "pd_code")
    private String pdCode;
    @Column(name = "dept_id")
    private Integer deptId;

    public PurDetailKey(String vouNo, String pdCode, Integer deptId) {
        this.vouNo = vouNo;
        this.pdCode = pdCode;
        this.deptId = deptId;
    }

    public PurDetailKey() {
    }
}
