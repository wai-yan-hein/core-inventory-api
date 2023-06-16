/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wai yan
 */
@Embeddable
public class CurrencyKey implements Serializable {
    @Column(name = "cur_code")
    private String code;
    @Column(name = "comp_code")
    private String compCode;

}
