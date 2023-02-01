/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author SAI
 */
@Embeddable
public class SeqKey implements Serializable {

    private Integer macId;
    private String seqOption;
    private String period;
    private String compCode;

    @Column(name = "mac_id")
    public Integer getMacId() {
        return macId;
    }

    public void setMacId(Integer macId) {
        this.macId = macId;
    }

    @Column(name = "seq_option")
    public String getSeqOption() {
        return seqOption;
    }

    public void setSeqOption(String seqOption) {
        this.seqOption = seqOption;
    }

    @Column(name = "period")
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Column(name = "comp_code")
    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.macId);
        hash = 89 * hash + Objects.hashCode(this.seqOption);
        hash = 89 * hash + Objects.hashCode(this.period);
        hash = 89 * hash + Objects.hashCode(this.compCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SeqKey other = (SeqKey) obj;
        if (!Objects.equals(this.seqOption, other.seqOption)) {
            return false;
        }
        if (!Objects.equals(this.period, other.period)) {
            return false;
        }
        if (!Objects.equals(this.compCode, other.compCode)) {
            return false;
        }
        return Objects.equals(this.macId, other.macId);
    }


}
