package cv.api.dms;


import cv.api.entity.Trader;
import cv.api.entity.TraderKey;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TraderDMSDto {
    private String traderCode;
    private String compCode;
    private String userCode;
    private String traderName;
    private String contactPerson;
    private String contactEmail;
    private String phoneNo;
    private String address;
    private Boolean deleted;
    private Boolean active;
    private String imageUrl;
    private String agentCode;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String agentUsrCode;
    private String agentName;
    private String intgUpdStatus;

    public Trader toTraderInv() {
        return Trader.builder()
                .key(TraderKey.builder()
                        .code(getTraderCode())
                        .compCode(getCompCode())
                        .build())
                .userCode(getUserCode())
                .traderName(getTraderName())
                .contactPerson(getContactPerson())
                .email(getContactEmail())
                .phone(getPhoneNo())
                .address(getAddress())
                .deleted(getDeleted())
                .active(getActive())
                .deptId(1)
                .type("CUS")
                .createdDate(getCreatedDate())
                .updatedDate(getUpdatedDate())
                .cashDown(false)
                .multi(false)
                .build();
    }


}

