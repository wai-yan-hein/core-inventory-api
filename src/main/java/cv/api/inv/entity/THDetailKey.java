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
public class THDetailKey implements Serializable {

    @Column(name = "td_code", unique = true, nullable = false)
    private String tdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "dept_id")
    private Integer deptId;

    public THDetailKey(String tdCode, String vouNo, Integer deptId) {
        this.tdCode = tdCode;
        this.vouNo = vouNo;
        this.deptId = deptId;
    }

    public THDetailKey() {
    }
}
