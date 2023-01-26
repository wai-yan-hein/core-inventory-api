package cv.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cv.api.common.Util1;
import cv.api.config.ActiveMqCondition;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import cv.api.repo.AccountRepo;
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
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
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
    @Autowired
    private AccountRepo accountRepo;


    @JmsListener(destination = "INV_MSG", containerFactory = "topicContainerFactory")
    public void receivedTopicMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String option = message.getString("OPTION");
        String data = message.getString("DATA");
        String senderQ = message.getString("SENDER_QUEUE");
        String serverQ = userRepo.getProperty("cloud.activemq.inventory.server.queue");
        if (serverQ.equals(listenQ)) {
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
        byte[] file = message.getBytes("DATA_FILE");
        String path = "temp" + File.separator;
        try {
            log.info(String.format("receivedMessage : %s - %s - %s", entity, option, senderQ));
            String REC = "REC";
            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            switch (entity) {
                case "SALE" -> {
                    SaleHis obj = gson.fromJson(data, SaleHis.class);
                    if (obj.getKey() != null) {
                        obj.getKey().setDeptId(userRepo.getDeptId());
                    }
                    switch (option) {
                        case "SAVE" -> save(obj);
                        case "RECEIVE" -> {
                            updateSale(obj);
                            log.info("sale voucher successfully delivered to server : " + obj.getKey().getVouNo());
                        }
                        case "DELETE" -> saleHisService.delete(obj.getKey());
                        case "TRUNCATE" -> saleHisService.truncate(obj.getKey());
                        case "RESTORE" -> saleHisService.restore(obj.getKey());
                    }
                }
                case "PURCHASE" -> {
                    PurHis obj = gson.fromJson(data, PurHis.class);
                    if (obj.getKey() != null) {
                        obj.getKey().setDeptId(userRepo.getDeptId());
                    }
                    switch (option) {
                        case "SAVE" -> save(obj);
                        case "RECEIVE" -> {
                            updatePurchase(obj);
                            log.info("purchase voucher successfully delivered to server : " + obj.getKey().getVouNo());
                        }
                        case "DELETE" -> purHisService.delete(obj.getKey());
                    }
                }
                case "RETURN_IN" -> {
                    RetInHis obj = gson.fromJson(data, RetInHis.class);
                    if (obj.getKey() != null) {
                        obj.getKey().setDeptId(userRepo.getDeptId());
                    }
                    switch (option) {
                        case "SAVE" -> save(obj);
                        case "RECEIVE" -> {
                            updateReturnIn(obj);
                            log.info("return in voucher successfully delivered to server : " + obj.getKey().getVouNo());
                        }
                        case "DELETE" -> retInService.delete(obj.getKey());
                        case "TRUNCATE" -> retInService.truncate(obj.getKey());
                        case "RESTORE" -> retInService.restore(obj.getKey());
                    }
                }
                case "RETURN_OUT" -> {
                    RetOutHis obj = gson.fromJson(data, RetOutHis.class);
                    if (obj.getKey() != null) {
                        obj.getKey().setDeptId(userRepo.getDeptId());
                    }
                    switch (option) {
                        case "SAVE" -> save(obj);
                        case "RECEIVE" -> {
                            updateReturnOut(obj);
                            log.info("return out voucher successfully delivered to server : " + obj.getKey().getVouNo());
                        }
                        case "DELETE" -> retOutService.delete(obj.getKey());

                    }
                }
                case "TRANSFER" -> {
                    TransferHis obj = gson.fromJson(data, TransferHis.class);
                    if (obj.getKey() != null) {
                        obj.getKey().setDeptId(userRepo.getDeptId());
                    }
                    switch (option) {
                        case "SAVE" -> {
                            save(obj);
                        }
                        case "RECEIVE" -> {
                            updateTransfer(obj);
                            log.info("transfer voucher successfully delivered to server : " + obj.getKey().getVouNo());
                        }
                        case "DELETE" -> transferHisService.delete(obj.getKey());
                        case "TRUNCATE" -> transferHisService.truncate(obj.getKey());
                        case "RESTORE" -> transferHisService.restore(obj.getKey());
                    }
                }
                case "STOCK_IO" -> {
                    StockInOut obj = gson.fromJson(data, StockInOut.class);
                    switch (option) {
                        case "SAVE" -> {
                            obj.setIntgUpdStatus(REC);
                            inOutService.save(obj);
                        }
                        case "RECEIVE" -> {
                            updateStockIO(obj);
                            log.info("stock io voucher successfully delivered to server : " + obj.getKey().getVouNo());
                        }
                        case "REQUEST_TRAN" -> {
                            List<StockInOut> list = inOutService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getKeys());
                            if (!list.isEmpty()) {
                                list.forEach(v -> responseTran(entity, senderQ, gson.toJson(v)));
                            }
                        }
                        case "RESPONSE_TRAN" -> inOutService.save(obj);
                    }
                }
                case "FILE" -> {
                    Reader reader = null;
                    if (file != null) {
                        Util1.extractZipToJson(file, path + option);
                        reader = Files.newBufferedReader(Paths.get(path + option.concat(".json")));
                    }
                    switch (option) {
                        case "VOU_STATUS_REQUEST" -> {
                            VouStatus obj = gson.fromJson(data, VouStatus.class);
                            List<VouStatus> list = vouStatusService.getVouStatus(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("VOU_STATUS_RESPONSE", list, senderQ);
                            }
                        }
                        case "VOU_STATUS_RESPONSE" -> {
                            assert reader != null;
                            List<VouStatus> list = gson.fromJson(reader, new TypeToken<ArrayList<VouStatus>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("vou status list size : " + list.size());
                                for (VouStatus obj : list) {
                                    save(obj);
                                }
                                log.info("vou status done.");
                            }
                        }
                        case "RELATION_REQUEST" -> {
                            UnitRelation obj = gson.fromJson(data, UnitRelation.class);
                            List<UnitRelation> list = relationService.getRelation(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("RELATION_RESPONSE", list, senderQ);
                            }
                        }
                        case "RELATION_RESPONSE" -> {
                            assert reader != null;
                            List<UnitRelation> list = gson.fromJson(reader, new TypeToken<ArrayList<UnitRelation>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("relation list size : " + list.size());
                                for (UnitRelation obj : list) {
                                    save(obj);
                                }
                                log.info("relation done.");
                            }
                        }
                        case "TRADER_REQUEST" -> {
                            Trader obj = gson.fromJson(data, Trader.class);
                            List<Trader> list = traderService.getTrader(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("TRADER_RESPONSE", list, senderQ);
                            }
                        }
                        case "TRADER_RESPONSE" -> {
                            assert reader != null;
                            List<Trader> list = gson.fromJson(reader, new TypeToken<ArrayList<Trader>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("trader list size : " + list.size());
                                for (Trader obj : list) {
                                    save(obj);
                                }
                                log.info("trader done.");
                            }
                        }
                        case "UNIT_REQUEST" -> {
                            StockUnit obj = gson.fromJson(data, StockUnit.class);
                            List<StockUnit> list = unitService.getUnit(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("UNIT_RESPONSE", list, senderQ);
                            }
                        }
                        case "UNIT_RESPONSE" -> {
                            assert reader != null;
                            List<StockUnit> list = gson.fromJson(reader, new TypeToken<ArrayList<StockUnit>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("unit list size : " + list.size());
                                for (StockUnit obj : list) {
                                    save(obj);
                                }
                                log.info("unit done.");
                            }
                        }
                        case "STOCK_TYPE_REQUEST" -> {
                            StockType obj = gson.fromJson(data, StockType.class);
                            List<StockType> list = typeService.getStockType(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("STOCK_TYPE_RESPONSE", list, senderQ);
                            }
                        }
                        case "STOCK_TYPE_RESPONSE" -> {
                            assert reader != null;
                            List<StockType> list = gson.fromJson(reader, new TypeToken<ArrayList<StockType>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("stock type list size : " + list.size());
                                for (StockType obj : list) {
                                    save(obj);
                                }
                                log.info("stock type done.");
                            }
                        }
                        case "STOCK_CATEGORY_REQUEST" -> {
                            Category obj = gson.fromJson(data, Category.class);
                            List<Category> list = categoryService.getCategory(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("STOCK_CATEGORY_RESPONSE", list, senderQ);
                            }
                        }
                        case "STOCK_CATEGORY_RESPONSE" -> {
                            assert reader != null;
                            List<Category> list = gson.fromJson(reader, new TypeToken<ArrayList<Category>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("stock category list size : " + list.size());
                                for (Category obj : list) {
                                    save(obj);
                                }
                                log.info("stock category done.");
                            }
                        }
                        case "STOCK_BRAND_REQUEST" -> {
                            StockBrand obj = gson.fromJson(data, StockBrand.class);
                            List<StockBrand> list = brandService.getBrand(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("STOCK_BRAND_RESPONSE", list, senderQ);
                            }
                        }
                        case "STOCK_BRAND_RESPONSE" -> {
                            assert reader != null;
                            List<StockBrand> list = gson.fromJson(reader, new TypeToken<ArrayList<StockBrand>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("stock brand list size : " + list.size());
                                for (StockBrand obj : list) {
                                    save(obj);
                                }
                                log.info("stock brand done.");
                            }
                        }
                        case "SALEMAN_REQUEST" -> {
                            SaleMan obj = gson.fromJson(data, SaleMan.class);
                            List<SaleMan> list = saleManService.getSaleMan(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("SALEMAN_RESPONSE", list, senderQ);
                            }
                        }
                        case "SALEMAN_RESPONSE" -> {
                            assert reader != null;
                            List<SaleMan> list = gson.fromJson(reader, new TypeToken<ArrayList<SaleMan>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("sale man list size : " + list.size());
                                for (SaleMan obj : list) {
                                    save(obj);
                                }
                                log.info("sale man done.");
                            }
                        }
                        case "LOCATION_REQUEST" -> {
                            Location obj = gson.fromJson(data, Location.class);
                            List<Location> list = locationService.getLocation(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("LOCATION_RESPONSE", list, senderQ);
                            }
                        }
                        case "LOCATION_RESPONSE" -> {
                            assert reader != null;
                            List<Location> list = gson.fromJson(reader, new TypeToken<ArrayList<Location>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("location list size : " + list.size());
                                for (Location obj : list) {
                                    save(obj);
                                }
                                log.info("location done.");
                            }
                        }
                        case "STOCK_REQUEST" -> {
                            Stock obj = gson.fromJson(data, Stock.class);
                            List<Stock> list = stockService.getStock(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat));
                            if (!list.isEmpty()) {
                                responseFile("STOCK_RESPONSE", list, senderQ);
                            }
                        }
                        case "STOCK_RESPONSE" -> {
                            assert reader != null;
                            List<Stock> list = gson.fromJson(reader, new TypeToken<ArrayList<Stock>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("stock list size : " + list.size());
                                for (Stock obj : list) {
                                    save(obj);
                                }
                                log.info("stock done.");
                            }
                        }
                        case "SALE_REQUEST" -> {
                            SaleHis obj = gson.fromJson(data, SaleHis.class);
                            List<SaleHis> list = saleHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                            if (!list.isEmpty()) {
                                responseFile("SALE_RESPONSE", list, senderQ);
                            }
                        }
                        case "SALE_RESPONSE" -> {
                            assert reader != null;
                            List<SaleHis> list = gson.fromJson(reader, new TypeToken<ArrayList<SaleHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("sale list size : " + list.size());
                                for (SaleHis obj : list) {
                                    save(obj);
                                }
                                log.info("sale done.");
                            }
                        }
                        case "SALE_UPLOAD" -> {
                            assert reader != null;
                            List<SaleHis> objList = new ArrayList<>();
                            List<SaleHis> list = gson.fromJson(reader, new TypeToken<ArrayList<SaleHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("sale upload list size : " + list.size() + " from " + senderQ);
                                for (SaleHis obj : list) {
                                    save(obj);
                                    SaleHis sh = new SaleHis();
                                    sh.setKey(obj.getKey());
                                    objList.add(sh);
                                }
                                log.info("sale upload done from " + senderQ);
                                responseFile("SALE_RECEIVED", objList, senderQ);
                            }
                        }
                        case "SALE_RECEIVED" -> {
                            assert reader != null;
                            List<SaleHis> list = gson.fromJson(reader, new TypeToken<ArrayList<SaleHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                list.forEach(this::updateSale);
                                log.info("sale voucher successfully sent to " + senderQ + " : " + list.size());
                            }
                        }
                        case "PURCHASE_REQUEST" -> {
                            PurHis obj = gson.fromJson(data, PurHis.class);
                            List<PurHis> list = purHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                            if (!list.isEmpty()) {
                                responseFile("PURCHASE_RESPONSE", list, senderQ);
                            }
                        }
                        case "PURCHASE_RESPONSE" -> {
                            assert reader != null;
                            List<PurHis> list = gson.fromJson(reader, new TypeToken<ArrayList<PurHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("purchase list size : " + list.size());
                                for (PurHis obj : list) {
                                    save(obj);
                                }
                                log.info("purchase done.");
                            }
                        }
                        case "PURCHASE_UPLOAD" -> {
                            assert reader != null;
                            List<PurHis> objList = new ArrayList<>();
                            List<PurHis> list = gson.fromJson(reader, new TypeToken<ArrayList<PurHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("purchase upload list size : " + list.size() + " from " + senderQ);
                                for (PurHis obj : list) {
                                    save(obj);
                                    PurHis sh = new PurHis();
                                    sh.setKey(obj.getKey());
                                    objList.add(sh);
                                }
                                log.info("purchase upload done from " + senderQ);
                                responseFile("PURCHASE_RECEIVED", objList, senderQ);
                            }
                        }
                        case "PURCHASE_RECEIVED" -> {
                            assert reader != null;
                            List<PurHis> list = gson.fromJson(reader, new TypeToken<ArrayList<PurHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                list.forEach(this::updatePurchase);
                                log.info("purchase voucher successfully delivered to server : " + list.size());
                            }
                        }
                        case "TRANSFER_REQUEST" -> {
                            TransferHis obj = gson.fromJson(data, TransferHis.class);
                            List<TransferHis> list = transferHisService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                            if (!list.isEmpty()) {
                                responseFile("TRANSFER_RESPONSE", list, senderQ);
                            }
                        }
                        case "TRANSFER_RESPONSE" -> {
                            assert reader != null;
                            List<TransferHis> list = gson.fromJson(reader, new TypeToken<ArrayList<TransferHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("transfer list size : " + list.size());
                                for (TransferHis obj : list) {
                                    save(obj);
                                }
                                log.info("transfer done.");
                            }
                        }
                        case "TRANSFER_UPLOAD" -> {
                            assert reader != null;
                            List<TransferHis> objList = new ArrayList<>();
                            List<TransferHis> list = gson.fromJson(reader, new TypeToken<ArrayList<TransferHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("transfer upload list size : " + list.size() + " from " + senderQ);
                                for (TransferHis obj : list) {
                                    save(obj);
                                    TransferHis sh = new TransferHis();
                                    sh.setKey(obj.getKey());
                                    objList.add(sh);
                                }
                                log.info("transfer upload done from " + senderQ);
                                responseFile("TRANSFER_RECEIVED", objList, senderQ);
                            }
                        }
                        case "TRANSFER_RECEIVED" -> {
                            assert reader != null;
                            List<TransferHis> list = gson.fromJson(reader, new TypeToken<ArrayList<TransferHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                list.forEach(this::updateTransfer);
                                log.info("transfer out voucher successfully delivered to server : " + list.size());

                            }
                        }
                        case "RETURN_IN_REQUEST" -> {
                            RetInHis obj = gson.fromJson(data, RetInHis.class);
                            List<RetInHis> list = retInService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                            if (!list.isEmpty()) {
                                responseFile("RETURN_IN_RESPONSE", list, senderQ);
                            }
                        }
                        case "RETURN_IN_RESPONSE" -> {
                            assert reader != null;
                            List<RetInHis> list = gson.fromJson(reader, new TypeToken<ArrayList<RetInHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("return in list size : " + list.size());
                                for (RetInHis obj : list) {
                                    save(obj);
                                }
                                log.info("return in  done.");
                            }
                        }
                        case "RETURN_IN_UPLOAD" -> {
                            assert reader != null;
                            List<RetInHis> objList = new ArrayList<>();
                            List<RetInHis> list = gson.fromJson(reader, new TypeToken<ArrayList<RetInHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("return in upload list size : " + list.size() + " from " + senderQ);
                                for (RetInHis obj : list) {
                                    save(obj);
                                    RetInHis sh = new RetInHis();
                                    sh.setKey(obj.getKey());
                                    objList.add(sh);
                                }
                                log.info("return in upload done from " + senderQ);
                                responseFile("RETURN_IN_RECEIVED", objList, senderQ);
                            }
                        }
                        case "RETURN_IN_RECEIVED" -> {
                            assert reader != null;
                            List<RetInHis> list = gson.fromJson(reader, new TypeToken<ArrayList<RetInHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                list.forEach(this::updateReturnIn);
                                log.info("return in voucher successfully delivered to server : " + list.size());
                            }
                        }
                        case "RETURN_OUT_REQUEST" -> {
                            RetOutHis obj = gson.fromJson(data, RetOutHis.class);
                            List<RetOutHis> list = retOutService.search(Util1.toDateStr(obj.getUpdatedDate(), dateTimeFormat), obj.getLocation());
                            if (!list.isEmpty()) {
                                responseFile("RETURN_OUT_RESPONSE", list, senderQ);
                            }
                        }
                        case "RETURN_OUT_RESPONSE" -> {
                            assert reader != null;
                            List<RetOutHis> list = gson.fromJson(reader, new TypeToken<ArrayList<RetOutHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("return out list size : " + list.size());
                                for (RetOutHis obj : list) {
                                    save(obj);
                                }
                                log.info("return out  done.");
                            }
                        }
                        case "RETURN_OUT_UPLOAD" -> {
                            assert reader != null;
                            List<RetOutHis> objList = new ArrayList<>();
                            List<RetOutHis> list = gson.fromJson(reader, new TypeToken<ArrayList<RetOutHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("return out upload list size : " + list.size() + " from " + senderQ);
                                for (RetOutHis obj : list) {
                                    save(obj);
                                    RetOutHis sh = new RetOutHis();
                                    sh.setKey(obj.getKey());
                                    objList.add(sh);
                                }
                                log.info("return out upload done from " + senderQ);
                                responseFile("RETURN_OUT_RECEIVED", objList, senderQ);
                            }
                        }
                        case "RETURN_OUT_RECEIVED" -> {
                            assert reader != null;
                            List<RetOutHis> list = gson.fromJson(reader, new TypeToken<ArrayList<RetOutHis>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                list.forEach(this::updateReturnOut);
                                log.info("return out voucher successfully delivered to server : " + list.size());

                            }
                        }
                        case "STOCK_IO_UPLOAD" -> {
                            assert reader != null;
                            List<StockInOut> objList = new ArrayList<>();
                            List<StockInOut> list = gson.fromJson(reader, new TypeToken<ArrayList<StockInOut>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                log.info("stock in out upload list size : " + list.size() + " from " + senderQ);
                                for (StockInOut obj : list) {
                                    save(obj);
                                    StockInOut sh = new StockInOut();
                                    sh.setKey(obj.getKey());
                                    objList.add(sh);
                                }
                                log.info("stock in out upload done from " + senderQ);
                                responseFile("STOCK_IO_RECEIVED", objList, senderQ);
                            }
                        }
                        case "STOCK_IO_RECEIVED" -> {
                            assert reader != null;
                            List<StockInOut> list = gson.fromJson(reader, new TypeToken<ArrayList<StockInOut>>() {
                            }.getType());
                            if (!list.isEmpty()) {
                                list.forEach(this::updateStockIO);
                                log.info("stock in out voucher successfully delivered to server : " + list.size());

                            }
                        }
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

    private void updateSale(SaleHis obj) {
        SaleHisKey key = obj.getKey();
        Integer deptId = userRepo.getDeptId();
        String sql = "update sale_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + deptId + "";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("updateSale : " + e.getMessage());
        }
    }

    private void updatePurchase(PurHis obj) {
        PurHisKey key = obj.getKey();
        Integer deptId = userRepo.getDeptId();
        String sql = "update pur_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + deptId + "";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("updatePurchase : " + e.getMessage());
        }
    }

    private void updateReturnIn(RetInHis obj) {
        RetInHisKey key = obj.getKey();
        Integer deptId = userRepo.getDeptId();
        String sql = "update ret_in_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + deptId + "";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("updateReturnIn : " + e.getMessage());
        }
    }

    private void updateReturnOut(RetOutHis obj) {
        RetOutHisKey key = obj.getKey();
        Integer deptId = userRepo.getDeptId();
        String sql = "update ret_out_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + deptId + "";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("updateReturnOut : " + e.getMessage());
        }
    }

    private void updateStockIO(StockInOut obj) {
        StockIOKey key = obj.getKey();
        Integer deptId = userRepo.getDeptId();
        String sql = "update stock_in_out set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + deptId + "";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("updateStockIO : " + e.getMessage());
        }
    }

    private void updateTransfer(TransferHis obj) {
        TransferHisKey key = obj.getKey();
        Integer deptId = userRepo.getDeptId();
        String sql = "update transfer_his set intg_upd_status ='" + SAVE + "'\n"
                + "where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + deptId + "";
        try {
            service.executeSql(sql);
        } catch (Exception e) {
            log.error("updateTransfer : " + e.getMessage());
        }
    }

    private void responseFile(String option, Object data, String queue) {
        String path = String.format("temp%s%s", File.separator, option + ".json");
        try {
            Util1.writeJsonFile(data, path);
            byte[] file = Util1.zipJsonFile(path);
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", "FILE");
                mm.setString("OPTION", option);
                mm.setBytes("DATA_FILE", file);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (IOException e) {
            log.error("File Message : " + e.getMessage());
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
        traderService.saveTrader(obj);
        accountRepo.sendTrader(obj);
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

    private void save(SaleHis obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        obj.setVouLock(true);
        saleHisService.save(obj);
    }

    private void save(TransferHis obj) {
        obj.setIntgUpdStatus(REC);
        obj.setVouLock(true);
        transferHisService.save(obj);
    }

    private void save(RetInHis obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        obj.setVouLock(true);
        retInService.save(obj);
    }

    private void save(RetOutHis obj) {
        obj.setIntgUpdStatus(REC);
        obj.setVouLock(true);
        retOutService.save(obj);
    }

    private void save(PurHis obj) {
        obj.setIntgUpdStatus(REC);
        obj.setVouLock(true);
        purHisService.save(obj);
    }

    private void save(StockInOut obj) {
        obj.getKey().setDeptId(userRepo.getDeptId());
        obj.setIntgUpdStatus(REC);
        inOutService.save(obj);
    }
}
