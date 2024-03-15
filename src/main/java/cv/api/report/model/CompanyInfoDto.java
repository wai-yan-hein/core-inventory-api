package cv.api.report.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyInfoDto {
    private String compCode;
    private String compName;
    private String compAddress;
    private String compPhone;
    private String compEmail;
    private boolean sync;
    private String reportCompany;
    private String reportUrl;
}