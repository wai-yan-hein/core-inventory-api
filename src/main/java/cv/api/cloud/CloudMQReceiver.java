package cv.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import java.text.DateFormat;
import java.util.List;

@Slf4j
@Component
public class CloudMQReceiver {
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private UnitRelationService relationService;
    @Autowired
    private TraderGroupService traderGroupService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private StockUnitService unitService;
    @Autowired
    private StockTypeService typeService;
    @Autowired
    private StockBrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private StockService stockService;
    @Autowired
    private OPHisService opHisService;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private PurHisService purHisService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private StockInOutService inOutService;
    @Autowired
    private TransferHisService transferHisService;
    @Autowired
    private ReportService service;
    @Autowired
    private JmsTemplate cloudMQTemplate;
    private final String SENT = "SENT";
    private final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();

    @JmsListener(destination = "${cloud.activemq.client.queue}")
    public void receivedMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String option = message.getString("OPTION");
        String data = message.getString("DATA");
        String senderQ = message.getString("SENDER_QUEUE");
        if (data != null) {
            try {
                String REC = "REC";
                switch (entity) {
                    case "VOU_STATUS" -> {
                        VouStatus vou = gson.fromJson(data, VouStatus.class);
                        switch (option) {
                            case "SENT" -> {
                                vou.setIntgUpdStatus(REC);
                                vouStatusService.save(vou);
                            }
                            case "RECEIVE" -> updateVouStatus(vou);
                            case "REQUEST" -> {
                                List<VouStatus> list = vouStatusService.getVouStatus(Util1.toDateStr(vou.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    log.info("data exists.");
                                }
                            }
                        }
                    }
                    case "RELATION" -> {
                        UnitRelation rel = gson.fromJson(data, UnitRelation.class);
                        switch (option) {
                            case "SENT" -> {
                                rel.setIntgUpdStatus(REC);
                                relationService.save(rel);
                            }
                            case "RECEIVE" -> updateUnitRelation(rel);
                            case "REQUEST" -> {
                                log.info(String.format("%s request received.", entity));
                                List<UnitRelation> list = relationService.getRelation(Util1.toDateStr(rel.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    log.info("data exists.");
                                }
                            }
                        }
                    }
                    case "TRADER_GROUP" -> {
                        TraderGroup obj = gson.fromJson(data, TraderGroup.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                traderGroupService.save(obj);
                            }
                            case "RECEIVE" -> updateTraderGroup(obj);
                        }
                    }
                    case "TRADER" -> {
                        Trader obj = gson.fromJson(data, Trader.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                traderService.saveTrader(obj);
                            }
                            case "RECEIVE" -> updateTrader(obj);
                            case "REQUEST" -> {
                                log.info(String.format("%s request received.", entity));
                                List<Trader> list = traderService.getTrader(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    log.info("data exists.");
                                }
                            }
                        }
                    }
                    case "UNIT" -> {
                        StockUnit obj = gson.fromJson(data, StockUnit.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                unitService.save(obj);
                            }
                            case "RECEIVE" -> updateUnit(obj);
                        }
                    }
                    case "STOCK_TYPE" -> {
                        StockType obj = gson.fromJson(data, StockType.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                typeService.save(obj);
                            }
                            case "RECEIVE" -> updateStockType(obj);
                        }
                    }
                    case "STOCK_BRAND" -> {
                        StockBrand obj = gson.fromJson(data, StockBrand.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                brandService.save(obj);
                            }
                            case "RECEIVE" -> updateBrand(obj);
                        }
                    }
                    case "STOCK_CATEGORY" -> {
                        Category obj = gson.fromJson(data, Category.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                categoryService.save(obj);
                            }
                            case "RECEIVE" -> updateCategory(obj);
                        }
                    }
                    case "SALEMAN" -> {
                        SaleMan obj = gson.fromJson(data, SaleMan.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                saleManService.save(obj);
                            }
                            case "RECEIVE" -> updateSaleMan(obj);
                        }
                    }
                    case "LOCATION" -> {
                        Location obj = gson.fromJson(data, Location.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                locationService.save(obj);
                            }
                            case "RECEIVE" -> updateLocation(obj);
                        }
                    }
                    case "STOCK" -> {
                        Stock obj = gson.fromJson(data, Stock.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                stockService.save(obj);
                            }
                            case "RECEIVE" -> updateStock(obj);
                        }
                    }
                    case "SALE" -> {
                        SaleHis obj = gson.fromJson(data, SaleHis.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                saleHisService.save(obj);
                            }
                            case "RECEIVE" -> updateSale(obj);
                        }
                    }
                    case "OPENING" -> {
                        OPHis obj = gson.fromJson(data, OPHis.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                opHisService.save(obj);
                            }
                            case "RECEIVE" -> updateOpening(obj);
                        }
                    }
                    case "PURCHASE" -> {
                        PurHis obj = gson.fromJson(data, PurHis.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                purHisService.save(obj);
                            }
                            case "RECEIVE" -> updatePurchase(obj);
                        }
                    }
                    case "RETURN_IN" -> {
                        RetInHis obj = gson.fromJson(data, RetInHis.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                retInService.save(obj);
                            }
                            case "RECEIVE" -> updateReturnIn(obj);
                        }
                    }
                    case "RETURN_OUT" -> {
                        RetOutHis obj = gson.fromJson(data, RetOutHis.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                retOutService.save(obj);
                            }
                            case "RECEIVE" -> updateReturnOut(obj);
                        }
                    }
                    case "TRANSFER" -> {
                        TransferHis obj = gson.fromJson(data, TransferHis.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                transferHisService.save(obj);
                            }
                            case "RECEIVE" -> updateTransfer(obj);
                        }
                    }
                    case "STOCK_IO" -> {
                        StockInOut obj = gson.fromJson(data, StockInOut.class);
                        switch (option) {
                            case "SENT" -> {
                                obj.setIntgUpdStatus(REC);
                                inOutService.save(obj);
                            }
                            case "RECEIVE" -> updateStockIO(obj);
                        }
                    }
                }
                if (option.equals("SENT")) {
                    sendMessage(senderQ, entity, data);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void sendMessage(String senderQ, String entity, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "RECEIVE");
            mm.setString("DATA", data);
            return mm;
        };
        cloudMQTemplate.send(senderQ, mc);
        log.info(String.format("%s received and sent to %s.", entity, senderQ));
    }

    private void updateVouStatus(VouStatus vou) throws Exception {
        VouStatusKey key = vou.getKey();
        String sql = "update vou_status set intg_upd_status ='" + SENT + "'\n"
                + "where code ='" + key.getCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update vou status.");
    }

    private void updateUnitRelation(UnitRelation rel) throws Exception {
        RelationKey key = rel.getKey();
        String sql = "update unit_relation set intg_upd_status ='" + SENT + "'\n"
                + "where rel_code ='" + key.getRelCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update unit.");
    }

    private void updateTraderGroup(TraderGroup obj) throws Exception {
        TraderGroupKey key = obj.getKey();
        String sql = "update trader_group set intg_upd_status ='" + SENT + "'\n"
                + "where group_code ='" + key.getGroupCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update trader group.");
    }

    private void updateTrader(Trader obj) throws Exception {
        TraderKey key = obj.getKey();
        String sql = "update trader set intg_upd_status ='" + SENT + "'\n"
                + "where code ='" + key.getCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update trader.");
    }

    private void updateUnit(StockUnit obj) throws Exception {
        StockUnitKey key = obj.getKey();
        String sql = "update stock_unit set intg_upd_status ='" + SENT + "'\n"
                + "where unit_code ='" + key.getUnitCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock unit.");
    }

    private void updateStockType(StockType obj) throws Exception {
        StockTypeKey key = obj.getKey();
        String sql = "update stock_type set intg_upd_status ='" + SENT + "'\n"
                + "where stock_type_code ='" + key.getStockTypeCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock type.");
    }

    private void updateBrand(StockBrand obj) throws Exception {
        StockBrandKey key = obj.getKey();
        String sql = "update stock_brand set intg_upd_status ='" + SENT + "'\n"
                + "where brand_code ='" + key.getBrandCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update brand");
    }

    private void updateCategory(Category obj) throws Exception {
        CategoryKey key = obj.getKey();
        String sql = "update category set intg_upd_status ='" + SENT + "'\n"
                + "where cat_code ='" + key.getCatCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update category");
    }

    private void updateLocation(Location obj) throws Exception {
        LocationKey key = obj.getKey();
        String sql = "update location set intg_upd_status ='" + SENT + "'\n"
                + "where loc_code ='" + key.getLocCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update location.");
    }

    private void updateSaleMan(SaleMan obj) throws Exception {
        SaleManKey key = obj.getKey();
        String sql = "update sale_man set intg_upd_status ='" + SENT + "'\n"
                + "where saleman_code ='" + key.getSaleManCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update saleman.");
    }

    private void updateStock(Stock obj) throws Exception {
        StockKey key = obj.getKey();
        String sql = "update stock set intg_upd_status ='" + SENT + "'\n"
                + "where stock_code ='" + key.getStockCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock.");
    }

    private void updateOpening(OPHis obj) throws Exception {
        OPHisKey key = obj.getKey();
        String sql = "update op_his set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update opening.");
    }
    private void updateSale(SaleHis obj) throws Exception {
        SaleHisKey key = obj.getKey();
        String sql = "update sale_his set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update sale.");
    }
    private void updatePurchase(PurHis obj) throws Exception {
        PurHisKey key = obj.getKey();
        String sql = "update pur_his set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update purchase.");
    }

    private void updateReturnIn(RetInHis obj) throws Exception {
        RetInHisKey key = obj.getKey();
        String sql = "update ret_in_his set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update return in.");
    }

    private void updateReturnOut(RetOutHis obj) throws Exception {
        RetOutHisKey key = obj.getKey();
        String sql = "update ret_out_his set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update return out.");
    }

    private void updateTransfer(TransferHis obj) throws Exception {
        TransferHisKey key = obj.getKey();
        String sql = "update transfer_his set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update transfer.");
    }

    private void updateStockIO(StockInOut obj) throws Exception {
        StockIOKey key = obj.getKey();
        String sql = "update stock_in_out set intg_upd_status ='" + SENT + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock io.");
    }
}
