package cv.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cv.api.common.Util1;
import cv.api.config.ActiveMqCondition;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import cv.api.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
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
@Conditional(ActiveMqCondition.class)
public class CloudMQReceiver {
    private final String SAVE = "SAVE";
    private final String REC = "REC";
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();
    @Value("${cloud.activemq.listen.queue}")
    private String listenQ;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private UnitRelationService relationService;
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
    @Autowired
    private UserRepo userRepo;

    private void responseSetup(String entity, String distQ, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("OPTION", "RESPONSE_SETUP");
            mm.setString("DATA", data);
            return mm;
        };
        if (distQ != null) {
            cloudMQTemplate.send(distQ, mc);
            log.info("responseSetup : " + entity);
        }
    }

    private void responseTran(String entity, String distQ, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "RESPONSE_TRAN");
            mm.setString("DATA", data);
            return mm;
        };
        if (distQ != null) {
            cloudMQTemplate.send(distQ, mc);
            log.info("responseTran : " + entity);
        }
    }

    private void save(VouStatus obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        vouStatusService.save(obj);
    }

    private void save(UnitRelation obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        relationService.save(obj);
    }

    private void save(Trader obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        traderService.saveTrader(obj);
    }

    private void save(StockUnit obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        unitService.save(obj);
    }

    private void save(StockType obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        typeService.save(obj);
    }

    private void save(StockBrand obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        brandService.save(obj);
    }

    private void save(Category obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        categoryService.save(obj);
    }

    private void save(SaleMan obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        saleManService.save(obj);
    }

    private void save(Location obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        locationService.save(obj);
    }

    private void save(Stock obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        stockService.save(obj);
    }

    @JmsListener(destination = "INV_MSG", containerFactory = "topicContainerFactory")
    public void receivedTopicMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String option = message.getString("OPTION");
        String data = message.getString("DATA");
        String senderQ = message.getString("SENDER_QUEUE");
        String serverQ = userRepo.getProperty("cloud.activemq.server.queue");
        if (senderQ.equals(serverQ)) {
            if (data != null) {
                try {
                    log.info(String.format("receivedMessage : %s - %s - %s", entity, option, senderQ));
                    switch (option) {
                        case "SETUP":
                            switch (entity) {
                                case "VOU_STATUS" -> {
                                    VouStatus obj = gson.fromJson(data, VouStatus.class);
                                    save(obj);
                                }
                                case "RELATION" -> {
                                    UnitRelation obj = gson.fromJson(data, UnitRelation.class);
                                    save(obj);
                                }
                                case "TRADER" -> {
                                    Trader obj = gson.fromJson(data, Trader.class);
                                    save(obj);
                                }
                                case "UNIT" -> {
                                    StockUnit obj = gson.fromJson(data, StockUnit.class);
                                    save(obj);
                                }
                                case "STOCK_TYPE" -> {
                                    StockType obj = gson.fromJson(data, StockType.class);
                                    save(obj);
                                }
                                case "STOCK_BRAND" -> {
                                    StockBrand obj = gson.fromJson(data, StockBrand.class);
                                    save(obj);
                                }
                                case "STOCK_CATEGORY" -> {
                                    Category obj = gson.fromJson(data, Category.class);
                                    save(obj);
                                }
                                case "SALEMAN" -> {
                                    SaleMan obj = gson.fromJson(data, SaleMan.class);
                                    save(obj);
                                }
                                case "LOCATION" -> {
                                    Location obj = gson.fromJson(data, Location.class);
                                    save(obj);
                                }
                                case "STOCK" -> {
                                    Stock obj = gson.fromJson(data, Stock.class);
                                    save(obj);
                                }
                            }
                        case "NOTIFICATION":
                            log.info("test");
                    }
                } catch (Exception e) {
                    log.error(String.format("%s : %s", entity, e.getMessage()));
                }
            }
        }

    }

    @JmsListener(destination = "${cloud.activemq.listen.queue}", containerFactory = "queueContainerFactory")
    public void receivedMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String option = message.getString("OPTION");
        String data = message.getString("DATA");
        String senderQ = message.getString("SENDER_QUEUE");
        if (data != null) {
            try {
                log.info(String.format("receivedMessage : %s - %s - %s", entity, option, senderQ));
                String REC = "REC";
                String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
                switch (entity) {
                    case "VOU_STATUS" -> {
                        VouStatus obj = gson.fromJson(data, VouStatus.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateVouStatus(obj);
                            case "REQUEST_SETUP" -> {
                                List<VouStatus> list = vouStatusService.getVouStatus(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "RELATION" -> {
                        UnitRelation obj = gson.fromJson(data, UnitRelation.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateUnitRelation(obj);
                            case "REQUEST_SETUP" -> {
                                List<UnitRelation> list = relationService.getRelation(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "TRADER" -> {
                        Trader obj = gson.fromJson(data, Trader.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateTrader(obj);
                            case "REQUEST_SETUP" -> {
                                List<Trader> list = traderService.getTrader(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "UNIT" -> {
                        StockUnit obj = gson.fromJson(data, StockUnit.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateUnit(obj);
                            case "REQUEST_SETUP" -> {
                                List<StockUnit> list = unitService.getUnit(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "STOCK_TYPE" -> {
                        StockType obj = gson.fromJson(data, StockType.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateStockType(obj);
                            case "REQUEST_SETUP" -> {
                                List<StockType> list = typeService.getStockType(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "STOCK_BRAND" -> {
                        StockBrand obj = gson.fromJson(data, StockBrand.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateBrand(obj);
                            case "REQUEST_SETUP" -> {
                                List<StockBrand> list = brandService.getBrand(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "STOCK_CATEGORY" -> {
                        Category obj = gson.fromJson(data, Category.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateCategory(obj);
                            case "REQUEST_SETUP" -> {
                                List<Category> list = categoryService.getCategory(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "SALEMAN" -> {
                        SaleMan obj = gson.fromJson(data, SaleMan.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> saleManService.save(obj);
                            case "RECEIVE" -> updateSaleMan(obj);
                            case "REQUEST_SETUP" -> {
                                List<SaleMan> list = saleManService.getSaleMan(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "LOCATION" -> {
                        Location obj = gson.fromJson(data, Location.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateLocation(obj);
                            case "REQUEST_SETUP" -> {
                                List<Location> list = locationService.getLocation(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "STOCK" -> {
                        Stock obj = gson.fromJson(data, Stock.class);
                        switch (option) {
                            case "SAVE", "RESPONSE_SETUP" -> save(obj);
                            case "RECEIVE" -> updateStock(obj);
                            case "REQUEST_SETUP" -> {
                                List<Stock> list = stockService.getStock(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseSetup(entity, senderQ, gson.toJson(v)));
                                }
                            }
                        }
                    }
                    case "SALE" -> {
                        SaleHis obj = gson.fromJson(data, SaleHis.class);
                        if (obj.getKey() != null) {
                            obj.getKey().setDeptId(userRepo.getDeptId());
                        }
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                saleHisService.save(obj);
                            }
                            case "RECEIVE" -> updateSale(obj);
                            case "DELETE" -> saleHisService.delete(obj.getKey());
                            case "RESTORE" -> saleHisService.restore(obj.getKey());
                            case "REQUEST_TRAN" -> {
                                List<SaleHis> list = saleHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> {
                                saleHisService.save(obj);
                            }
                        }
                    }
                    case "OPENING" -> {
                        OPHis obj = gson.fromJson(data, OPHis.class);
                        if (obj.getKey() != null) {
                            obj.getKey().setDeptId(userRepo.getDeptId());
                        }
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                opHisService.save(obj);
                            }
                            case "RECEIVE" -> updateOpening(obj);
                            case "REQUEST_TRAN" -> {
                                List<OPHis> list = opHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getKeys());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> {
                                opHisService.save(obj);
                            }
                        }
                    }
                    case "PURCHASE" -> {
                        PurHis obj = gson.fromJson(data, PurHis.class);
                        if (obj.getKey() != null) {
                            obj.getKey().setDeptId(userRepo.getDeptId());
                        }
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                purHisService.save(obj);
                            }
                            case "RECEIVE" -> updatePurchase(obj);
                            case "REQUEST_TRAN" -> {
                                List<PurHis> list = purHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getKeys());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> purHisService.save(obj);
                        }
                    }
                    case "RETURN_IN" -> {
                        RetInHis obj = gson.fromJson(data, RetInHis.class);
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                retInService.save(obj);
                            }
                            case "RECEIVE" -> updateReturnIn(obj);
                            case "REQUEST_TRAN" -> {
                                List<RetInHis> list = retInService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getKeys());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> retInService.save(obj);
                        }
                    }
                    case "RETURN_OUT" -> {
                        RetOutHis obj = gson.fromJson(data, RetOutHis.class);
                        if (obj.getKey() != null) {
                            obj.getKey().setDeptId(userRepo.getDeptId());
                        }
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                retOutService.save(obj);
                            }
                            case "RECEIVE" -> updateReturnOut(obj);
                            case "REQUEST_TRAN" -> {
                                List<RetOutHis> list = retOutService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getKeys());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> retOutService.save(obj);
                        }
                    }
                    case "TRANSFER" -> {
                        TransferHis obj = gson.fromJson(data, TransferHis.class);
                        if (obj.getKey() != null) {
                            obj.getKey().setDeptId(userRepo.getDeptId());
                        }
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                transferHisService.save(obj);
                            }
                            case "RECEIVE" -> updateTransfer(obj);
                            case "DELETE" -> transferHisService.delete(obj.getKey());
                            case "RESTORE" -> transferHisService.restore(obj.getKey());
                            case "REQUEST_TRAN" -> {
                                List<TransferHis> list = transferHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> {
                                transferHisService.save(obj);
                            }

                        }
                    }
                    case "STOCK_IO" -> {
                        StockInOut obj = gson.fromJson(data, StockInOut.class);
                        switch (option) {
                            case "SAVE" -> {
                                obj.setIntgUpdStatus(REC);
                                inOutService.save(obj);
                            }
                            case "RECEIVE" -> updateStockIO(obj);
                            case "REQUEST_TRAN" -> {
                                List<StockInOut> list = inOutService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getKeys());
                                if (!list.isEmpty()) {
                                    list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                                }
                            }
                            case "RESPONSE_TRAN" -> inOutService.save(obj);
                        }
                    }
                }
                if (option.equals("SAVE")) {
                    sendReceiveMessage(senderQ, entity, data);
                }

            } catch (Exception e) {
                log.error(String.format("%s : %s", entity, e.getMessage()));
            }
        }
    }

    private void sendReceiveMessage(String senderQ, String entity, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("OPTION", "RECEIVE");
            mm.setString("DATA", data);
            return mm;
        };
        cloudMQTemplate.send(senderQ, mc);
        log.info(String.format("%s received and sent to %s.", entity, senderQ));
    }

    private void updateVouStatus(VouStatus vou) throws Exception {
        VouStatusKey key = vou.getKey();
        String sql = "update vou_status set intg_upd_status ='" + SAVE + "'\n"
                + "where code ='" + key.getCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update vou status.");
    }

    private void updateUnitRelation(UnitRelation rel) throws Exception {
        RelationKey key = rel.getKey();
        String sql = "update unit_relation set intg_upd_status ='" + SAVE + "'\n"
                + "where rel_code ='" + key.getRelCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update unit.");
    }

    private void updateTraderGroup(TraderGroup obj) throws Exception {
        TraderGroupKey key = obj.getKey();
        String sql = "update trader_group set intg_upd_status ='" + SAVE + "'\n"
                + "where group_code ='" + key.getGroupCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update trader group.");
    }

    private void updateTrader(Trader obj) throws Exception {
        TraderKey key = obj.getKey();
        String sql = "update trader set intg_upd_status ='" + SAVE + "'\n"
                + "where code ='" + key.getCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update trader.");
    }

    private void updateUnit(StockUnit obj) throws Exception {
        StockUnitKey key = obj.getKey();
        String sql = "update stock_unit set intg_upd_status ='" + SAVE + "'\n"
                + "where unit_code ='" + key.getUnitCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock unit.");
    }

    private void updateStockType(StockType obj) throws Exception {
        StockTypeKey key = obj.getKey();
        String sql = "update stock_type set intg_upd_status ='" + SAVE + "'\n"
                + "where stock_type_code ='" + key.getStockTypeCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock type.");
    }

    private void updateBrand(StockBrand obj) throws Exception {
        StockBrandKey key = obj.getKey();
        String sql = "update stock_brand set intg_upd_status ='" + SAVE + "'\n"
                + "where brand_code ='" + key.getBrandCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update brand");
    }

    private void updateCategory(Category obj) throws Exception {
        CategoryKey key = obj.getKey();
        String sql = "update category set intg_upd_status ='" + SAVE + "'\n"
                + "where cat_code ='" + key.getCatCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update category");
    }

    private void updateLocation(Location obj) throws Exception {
        LocationKey key = obj.getKey();
        String sql = "update location set intg_upd_status ='" + SAVE + "'\n"
                + "where loc_code ='" + key.getLocCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update location.");
    }

    private void updateSaleMan(SaleMan obj) throws Exception {
        SaleManKey key = obj.getKey();
        String sql = "update sale_man set intg_upd_status ='" + SAVE + "'\n"
                + "where saleman_code ='" + key.getSaleManCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update saleman.");
    }

    private void updateStock(Stock obj) throws Exception {
        StockKey key = obj.getKey();
        String sql = "update stock set intg_upd_status ='" + SAVE + "'\n"
                + "where stock_code ='" + key.getStockCode() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock.");
    }

    private void updateOpening(OPHis obj) throws Exception {
        OPHisKey key = obj.getKey();
        String sql = "update op_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update opening.");
    }

    private void updateSale(SaleHis obj) throws Exception {
        SaleHisKey key = obj.getKey();
        String sql = "update sale_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update sale.");
    }

    private void updatePurchase(PurHis obj) throws Exception {
        PurHisKey key = obj.getKey();
        String sql = "update pur_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update purchase.");
    }

    private void updateReturnIn(RetInHis obj) throws Exception {
        RetInHisKey key = obj.getKey();
        String sql = "update ret_in_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update return in.");
    }

    private void updateReturnOut(RetOutHis obj) throws Exception {
        RetOutHisKey key = obj.getKey();
        String sql = "update ret_out_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update return out.");
    }

    private void updateTransfer(TransferHis obj) throws Exception {
        TransferHisKey key = obj.getKey();
        String sql = "update transfer_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update transfer.");
    }

    private void updateStockIO(StockInOut obj) throws Exception {
        StockIOKey key = obj.getKey();
        String sql = "update stock_in_out set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        service.executeSql(sql);
        log.info("update stock io.");
    }
}
