/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lenevo
 */
@Data
@Embeddable
public class CompoundKey implements Serializable {

    @Column(name = "machine_name", nullable = false, length = 50)
    private String machineName;
    @Column(name = "vou_type", nullable = false, length = 15)
    private String vouType;
    @Column(name = "vou_period", nullable = false, length = 15)
    private String period;

}
