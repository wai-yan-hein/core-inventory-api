/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author WSwe
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "machine_info")
public class MachineInfo implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mac_id", unique = true, nullable = false)
    private Integer machineId;
    @Column(name = "machine_name", unique = true, nullable = false)
    private String machineName;
    @Column(name = "machine_ip")
    private String ipAddress;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime regDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;

    public MachineInfo() {
    }

}
