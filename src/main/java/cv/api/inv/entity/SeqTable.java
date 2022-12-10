/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author wai yan
 */
@Entity
@Table(name = "seq_table")
public class SeqTable implements java.io.Serializable {


    private Integer seqNo;
    private SeqKey key;

    @EmbeddedId
    public SeqKey getKey() {
        return key;
    }

    public void setKey(SeqKey key) {
        this.key = key;
    }


    @Column(name = "seq_no")
    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }


}
