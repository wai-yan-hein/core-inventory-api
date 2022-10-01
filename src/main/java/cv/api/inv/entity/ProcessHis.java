package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "process_his")
public class ProcessHis {
    @EmbeddedId
    private ProcessHisKey key;
    //@ManyToOne
    //@JoinColumns({
            //@JoinColumn(name = "stock_code"),
            //@JoinColumn(name = "comp_code",referencedColumnName = "comp_code",insertable = false,updatable = false)
    //})
    //private Stock stock;
    //@JoinColumns({
            //@JoinColumn(name = "pt_code"),
            //@JoinColumn(name = "comp_code",referencedColumnName = "comp_code",insertable = false,updatable = false)
    //})
    //private ProcessType processType;
}
