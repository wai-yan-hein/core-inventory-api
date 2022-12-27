package cv.api.inv.service;

import cv.api.common.Util1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;

@Slf4j
@Service
@Transactional
public class ConvertServiceImpl implements ConverterService {
    @Autowired
    private ReportService reportService;

    @Override
    public void convertToUnicode() {
        convertVouStatus();
        convertStock();
        convertStockType();
        convertTrader();
        convertSale();
        convertPurchase();
        convertReturnIn();
        convertReturnOut();
        convertStockIO();
        convertTransfer();
    }

    private void convertVouStatus() {
        String sql = "select *\n" +
                "from vou_status\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String description = rs.getString("description");
                    if (Util1.isZGText(description)) {
                        rs.updateString("description", Util1.convertToUniCode(description));
                        rs.updateRow();
                    }
                }
                log.info("converted vou status.");
            }
        } catch (Exception e) {
            log.error("convertVouStatus : " + e.getMessage());
        }
    }

    private void convertStock() {
        String sql = "select *\n" +
                "from stock\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String stockName = rs.getString("stock_name");
                    if (Util1.isZGText(stockName)) {
                        rs.updateString("stock_name", Util1.convertToUniCode(stockName));
                        rs.updateRow();
                    }
                }
                log.info("converted stock.");
            }
        } catch (Exception e) {
            log.error("convertStock : " + e.getMessage());
        }
    }

    private void convertStockType() {
        String sql = "select *\n" +
                "from stock_type\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String typeName = rs.getString("stock_type_name");
                    if (Util1.isZGText(typeName)) {
                        rs.updateString("stock_type_name", Util1.convertToUniCode(typeName));
                        rs.updateRow();
                    }
                }
                log.info("converted stock type.");
            }
        } catch (Exception e) {
            log.error("convertStockType : " + e.getMessage());
        }
    }

    private void convertTrader() {
        String sql = "select *\n" +
                "from trader\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String typeName = rs.getString("trader_name");
                    if (Util1.isZGText(typeName)) {
                        rs.updateString("trader_name", Util1.convertToUniCode(typeName));
                        rs.updateRow();
                    }
                }
                log.info("converted trader.");
            }
        } catch (Exception e) {
            log.error("convertTrader : " + e.getMessage());
        }
    }

    private void convertSale() {
        String sql = "select *\n" +
                "from sale_his\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String remark = rs.getString("remark");
                    if (Util1.isZGText(remark)) {
                        rs.updateString("remark", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                }
                log.info("converted sale.");
            }
        } catch (Exception e) {
            log.error("convertSale : " + e.getMessage());
        }
    }

    private void convertPurchase() {
        String sql = "select *\n" +
                "from pur_his\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String remark = rs.getString("remark");
                    if (Util1.isZGText(remark)) {
                        rs.updateString("remark", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                }
                log.info("converted purchase.");
            }
        } catch (Exception e) {
            log.error("convert purchase : " + e.getMessage());
        }
    }

    private void convertReturnIn() {
        String sql = "select *\n" +
                "from ret_in_his\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String remark = rs.getString("remark");
                    if (Util1.isZGText(remark)) {
                        rs.updateString("remark", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                }
                log.info("converted return in.");
            }
        } catch (Exception e) {
            log.error("convert return in : " + e.getMessage());
        }
    }

    private void convertReturnOut() {
        String sql = "select *\n" +
                "from ret_out_his\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String remark = rs.getString("remark");
                    if (Util1.isZGText(remark)) {
                        rs.updateString("remark", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                }
                log.info("converted return out.");
            }
        } catch (Exception e) {
            log.error("convertReturnOut : " + e.getMessage());
        }
    }

    private void convertStockIO() {
        String sql = "select *\n" +
                "from stock_in_out\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String remark = rs.getString("remark");
                    String description = rs.getString("description");
                    if (Util1.isZGText(remark)) {
                        rs.updateString("remark", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                    if (Util1.isZGText(description)) {
                        rs.updateString("description", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                }
                log.info("converted stock io.");
            }
        } catch (Exception e) {
            log.error("convertStockIO : " + e.getMessage());
        }
    }

    private void convertTransfer() {
        String sql = "select *\n" +
                "from transfer_his\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String remark = rs.getString("remark");
                    if (Util1.isZGText(remark)) {
                        rs.updateString("remark", Util1.convertToUniCode(remark));
                        rs.updateRow();
                    }
                }
                log.info("converted transfer.");
            }
        } catch (Exception e) {
            log.error("convertTransfer : " + e.getMessage());
        }
    }
}
