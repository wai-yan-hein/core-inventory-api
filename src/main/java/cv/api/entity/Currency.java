/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;

/**
 * @author WSwe
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "currency")
@Data
public class Currency implements java.io.Serializable {
    @Id
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "cur_name")
    private String currencyName;
    @Column(name = "cur_symbol")
    private String currencySymbol;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_dt", columnDefinition = "TIMESTAMP")
    private Date createdDt;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_dt", columnDefinition = "TIMESTAMP")
    private Date updatedDt;
}
