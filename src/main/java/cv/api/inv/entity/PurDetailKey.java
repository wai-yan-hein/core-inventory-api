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
public class PurDetailKey implements Serializable {

    @Column(name = "vou_no", length = 15)
    private String vouNo;
    @Column(name = "pd_code", length = 20)
    private String pdCode;

    public PurDetailKey() {
    }

    public PurDetailKey(String vouNo, String pdCode) {
        this.vouNo = vouNo;
        this.pdCode = pdCode;
    }

}
