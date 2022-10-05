/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Embeddable
public class RetOutKey implements Serializable {

    @Column(name = "rd_code", unique = true, nullable = false)
    private String rdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "dept_id")
    private Integer deptId;

    public RetOutKey(String rdCode, String vouNo, Integer deptId) {
        this.rdCode = rdCode;
        this.vouNo = vouNo;
        this.deptId = deptId;
    }

    public RetOutKey() {
    }
}
