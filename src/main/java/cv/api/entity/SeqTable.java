/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import jakarta.persistence.*;
import lombok.Data;


/**
 * @author wai yan
 */
@Data
@Entity
@Table(name = "seq_table")
public class SeqTable {
    @EmbeddedId
    private SeqKey key;
    @Column(name = "seq_no")
    private Integer seqNo;
}
