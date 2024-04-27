package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
@Builder
@Data
public class TransferHisKey {
    private String vouNo;
    private String compCode;
}
