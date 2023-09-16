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
public class SaleDetailKey implements Serializable {

    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "vou_no")
    private String vouNo;
}
