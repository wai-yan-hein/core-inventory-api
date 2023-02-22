package cv.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cv.api.common.Util1;
import cv.api.config.ActiveMqCondition;
import cv.api.entity.*;
import cv.api.model.Department;
import cv.api.repo.UserRepo;
import cv.api.service.*;
import cv.api.tray.AppTray;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQQueueReceiver;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Conditional(ActiveMqCondition.class)
public class CloudMQSender {
    private final Gson gson = new GsonBuilder()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();
    @Value("${cloud.activemq.listen.queue}")
    private String listenQ;
    @Autowired
    private JmsTemplate cloudMQTemplate;
    @Autowired
    private JmsTemplate topicSender;
    //service
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private UnitRelationService relationService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private StockUnitService unitService;
    @Autowired
    private StockTypeService stockTypeService;
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
    private SaleHisService saleHisService;
    @Autowired
    private PurHisService purHisService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private TransferHisService transferHisService;
    @Autowired
    private StockInOutService inOutService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CloudMQReceiver receiver;
    private final HashMap<String, String> hmQueue = new HashMap<>();
    private boolean client;
    private String serverQ;
    private boolean progress = false;

    @Scheduled(fixedRate = 60 * 1000)
    private void uploadToServer() {
        initQueue();
        client = Util1.getBoolean(userRepo.getProperty("cloud.upload.server"));
        serverQ = userRepo.getProperty("cloud.activemq.inventory.server.queue");
        if (client) {
            log.info("ActiveMQ Server Q : " + serverQ);
            checkOnline();
            if (receiver.isOnline()) {
                if (!progress) {
                    progress = true;
                    uploadTransaction();
                    downloadSetup();
                    downloadTransaction();
                    progress = false;
                }
            } else {
                log.info("server is offline.");
            }
        }
    }

    private void checkOnline() {
        receiver.setOnline(false);
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", "CONNECTION");
            mm.setString("OPTION", "CONNECTION");
            return mm;
        };
        cloudMQTemplate.send(serverQ, mc);
        sleep();
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void initQueue() {
        if (hmQueue.isEmpty()) {
            List<Department> listDep = userRepo.getDepartment();
            HashMap<Integer, String> hmDep = new HashMap<>();
            for (Department d : listDep) {
                hmDep.put(d.getDeptId(), d.getInventoryQ());
            }
            List<Location> list = locationService.findAll();
            if (!list.isEmpty()) {
                for (Location l : list) {
                    String locCode = l.getKey().getLocCode();
                    Integer deptId = l.getMapDeptId();
                    hmQueue.put(locCode, hmDep.get(deptId));
                }
            }
        }
    }

    private void saveMessage(String entity, String data, String queue) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "SAVE");
                mm.setString("DATA", data);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (Exception e) {
            log.error(String.format("saveMessage : %s : %s", entity, e.getMessage()));
        }
    }

    private void uploadFile(String option, Object data, String queue) {
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
            log.error(String.format("uploadFile : %s : %s", option, e.getMessage()));
        }
    }

    private void deleteMessage(String entity, String data, String queue) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "DELETE");
                mm.setString("DATA", data);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (Exception e) {
            log.error(String.format("deleteMessage : %s : %s", entity, e.getMessage()));
        }
    }

    private void truncateMessage(String entity, String data, String queue) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "TRUNCATE");
                mm.setString("DATA", data);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (Exception e) {
            log.error(String.format("truncateMessage : %s : %s", entity, e.getMessage()));

        }
    }

    private void restoreMessage(String entity, String data, String queue) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "RESTORE");
                mm.setString("DATA", data);
                return mm;
            };
            if (queue != null) {
                cloudMQTemplate.send(queue, mc);
            }
        } catch (Exception e) {
            log.error(String.format("restoreMessage : %s : %s", entity, e.getMessage()));
        }
    }

    private void destroyQ(String queue) {
        try {
            if (cloudMQTemplate != null) {
                ConnectionFactory factory = cloudMQTemplate.getConnectionFactory();
                if (factory != null) {
                    Connection connection = factory.createConnection();
                    if (connection instanceof ActiveMQConnection con) {
                        con.destroyDestination(new ActiveMQQueue(queue));
                    }

                }
            }
        } catch (JMSException e) {
            log.error("destroyQ : " + e.getMessage());
        }
    }

    private void sendTopicMessage(String entity, String data) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", entity);
                mm.setString("OPTION", "SETUP");
                mm.setString("DATA", data);
                return mm;
            };
            if (topicSender != null) {
                topicSender.send(mc);
            }
        } catch (Exception e) {
            log.error(String.format("requestFile : %s : %s", entity, e.getMessage()));
        }
    }

    private void requestFile(String option, String date) {
        try {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("SENDER_QUEUE", listenQ);
                mm.setString("ENTITY", "FILE");
                mm.setString("OPTION", option + "_REQUEST");
                mm.setString("DATA", date);
                return mm;
            };
            if (serverQ != null) {
                cloudMQTemplate.send(serverQ, mc);
            }
        } catch (Exception e) {
            log.error(String.format("requestFile : %s : %s", option, e.getMessage()));
        }
    }


    private void uploadSetup() {
        uploadVouStatus();
        uploadUnitRelation();
        uploadTrader();
        uploadStockUnit();
        uploadStockType();
        uploadStockBrand();
        uploadSaleMan();
        uploadCategory();
        uploadLocation();
        uploadStock();
    }

    private void downloadSetup() {
        requestFile("VOU_STATUS", gson.toJson(new VouStatus(vouStatusService.getMaxDate())));
        requestFile("RELATION", gson.toJson(new UnitRelation(relationService.getMaxDate())));
        requestFile("TRADER", gson.toJson(new Trader(traderService.getMaxDate())));
        requestFile("SALEMAN", gson.toJson(new SaleMan(saleManService.getMaxDate())));
        requestFile("UNIT", gson.toJson(new StockUnit(unitService.getMaxDate())));
        requestFile("STOCK_TYPE", gson.toJson(new StockType(stockTypeService.getMaxDate())));
        requestFile("STOCK_BRAND", gson.toJson(new StockBrand(brandService.getMaxDate())));
        requestFile("STOCK_CATEGORY", gson.toJson(new Category(categoryService.getMaxDate())));
        requestFile("LOCATION", gson.toJson(new Location(locationService.getMaxDate())));
        requestFile("STOCK", gson.toJson(new Stock(stockService.getMaxDate())));
    }

    private void downloadTransaction() {
        List<String> location = userRepo.getLocation();
        //requestTran("OPENING", gson.toJson(new OPHis(opHisService.getMaxDate(), keys)));
        requestFile("SALE", gson.toJson(new SaleHis(saleHisService.getMaxDate(), location)));
        requestFile("PURCHASE", gson.toJson(new PurHis(purHisService.getMaxDate(), location)));
        requestFile("RETURN_IN", gson.toJson(new RetInHis(retInService.getMaxDate(), location)));
        requestFile("RETURN_OUT", gson.toJson(new RetOutHis(retOutService.getMaxDate(), location)));
        //requestTran("STOCK_IO", gson.toJson(new StockInOut(inOutService.getMaxDate(), keys)));
        requestFile("TRANSFER", gson.toJson(new TransferHis(transferHisService.getMaxDate(), location)));
    }

    private void uploadTransaction() {
        uploadStockInOut();
        uploadSale();
        uploadPurchase();
        uploadReturnIn();
        uploadReturnOut();
        uploadTransfer();
    }

    private void uploadSale() {
        List<SaleHis> list = saleHisService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload sale : " + list.size());
            uploadFile("SALE_UPLOAD", list, serverQ);
        }
    }

    public void send(SaleHis sh) {
        if (sh != null) {
            saveMessage("SALE", gson.toJson(sh), getQueue(sh));
        }
    }

    public void checkLocationAndTruncate(SaleHis sh) {
        SaleHis old = saleHisService.findById(sh.getKey());
        if (old != null) {
            if (!sh.getLocCode().equals(old.getLocCode())) {
                truncateMessage("SALE", gson.toJson(old), getQueue(old));
            }
        }
    }

    public void delete(SaleHisKey key) {
        SaleHis obj = new SaleHis();
        obj.setKey(key);
        deleteMessage("SALE", gson.toJson(obj), getQueue(saleHisService.findById(key)));
    }

    public void restore(SaleHisKey key) {
        SaleHis obj = new SaleHis();
        obj.setKey(key);
        restoreMessage("SALE", gson.toJson(obj), getQueue(saleHisService.findById(key)));
    }

    private String getQueue(SaleHis sh) {
        return client ? serverQ : hmQueue.get(sh.getLocCode());
    }

    private void uploadPurchase() {
        List<PurHis> list = purHisService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload purchase : " + list.size());
            uploadFile("PURCHASE_UPLOAD", list, serverQ);
        }
    }

    public void send(PurHis sh) {
        if (sh != null) {
            String queue = client ? serverQ : hmQueue.get(sh.getLocCode());
            saveMessage("PURCHASE", gson.toJson(sh), queue);
        }
    }

    public void delete(PurHisKey key) {
        PurHis obj = new PurHis();
        obj.setKey(key);
        deleteMessage("PURCHASE", gson.toJson(obj), getQueue(purHisService.findById(key)));
    }

    public void restore(PurHisKey key) {
        PurHis obj = new PurHis();
        obj.setKey(key);
        restoreMessage("PURCHASE", gson.toJson(obj), getQueue(purHisService.findById(key)));
    }

    private String getQueue(PurHis sh) {
        return client ? serverQ : hmQueue.get(sh.getLocCode());
    }

    private void uploadReturnIn() {
        List<RetInHis> list = retInService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload return in : " + list.size());
            uploadFile("RETURN_IN_UPLOAD", list, serverQ);
        }
    }

    public void send(RetInHis rin) {
        if (rin != null) saveMessage("RETURN_IN", gson.toJson(rin), getQueue(rin));
    }

    public void delete(RetInHisKey key) {
        RetInHis obj = new RetInHis();
        obj.setKey(key);
        deleteMessage("RETURN_IN", gson.toJson(obj), getQueue(retInService.findById(key)));
    }

    public void restore(RetInHisKey key) {
        RetInHis obj = new RetInHis();
        obj.setKey(key);
        restoreMessage("RETURN_IN", gson.toJson(obj), getQueue(retInService.findById(key)));
    }

    private String getQueue(RetInHis sh) {
        return client ? serverQ : hmQueue.get(sh.getLocCode());
    }

    public void uploadReturnOut() {
        List<RetOutHis> list = retOutService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload return out : " + list.size());
            uploadFile("RETURN_OUT_UPLOAD", list, serverQ);
        }
    }

    public void send(RetOutHis obj) {
        if (obj != null) {
            saveMessage("RETURN_OUT", gson.toJson(obj), getQueue(obj));
        }
    }

    public void delete(RetOutHisKey key) {
        RetOutHis obj = new RetOutHis();
        obj.setKey(key);
        deleteMessage("RETURN_OUT", gson.toJson(obj), getQueue(retOutService.findById(key)));
    }

    public void restore(RetOutHisKey key) {
        RetOutHis obj = new RetOutHis();
        obj.setKey(key);
        restoreMessage("RETURN_OUT", gson.toJson(obj), getQueue(retOutService.findById(key)));
    }

    private String getQueue(RetOutHis sh) {
        return client ? serverQ : hmQueue.get(sh.getLocCode());
    }


    private void uploadTransfer() {
        List<TransferHis> list = transferHisService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload transfer : " + list.size());
            uploadFile("TRANSFER_UPLOAD", list, serverQ);
        }
    }

    public void send(TransferHis th) {
        saveMessage("TRANSFER", gson.toJson(th), getQueue(th));
    }

    public void delete(TransferHisKey key) {
        TransferHis obj = new TransferHis();
        obj.setKey(key);
        deleteMessage("TRANSFER", gson.toJson(obj), getQueue(transferHisService.findById(key)));
    }

    public void checkLocationAndTruncate(TransferHis obj) {
        TransferHis old = transferHisService.findById(obj.getKey());
        if (old != null) {
            if (!obj.getLocCodeFrom().equals(old.getLocCodeFrom()) || !obj.getLocCodeTo().equals(old.getLocCodeTo())) {
                truncateMessage("TRANSFER", gson.toJson(old), getQueue(old));
            }
        }
    }

    public void restore(TransferHisKey key) {
        TransferHis obj = new TransferHis();
        obj.setKey(key);
        restoreMessage("TRANSFER", gson.toJson(obj), getQueue(transferHisService.findById(key)));
    }

    private void uploadStockInOut() {
        List<StockInOut> list = inOutService.unUpload(Util1.toDateStr(Util1.getSyncDate(), "yyyy-MM-dd"));
        if (!list.isEmpty()) {
            log.info("upload stock in out : " + list.size());
            uploadFile("STOCK_IO_UPLOAD", list, serverQ);
        }
    }

    public String getQueue(TransferHis th) {
        String q1 = hmQueue.get(th.getLocCodeTo());
        String q2 = hmQueue.get(th.getLocCodeFrom());
        String mig = Util1.isNull(q1, q2);
        return client ? serverQ : mig;
    }

    private void uploadStock() {
        List<Stock> list = stockService.unUpload();
        if (!list.isEmpty()) log.info("upload stock : " + list.size());
        list.forEach(o -> saveMessage("STOCK", gson.toJson(o), serverQ));
    }

    public void send(Stock s) {
        sendTopicMessage("STOCK", gson.toJson(s));
    }


    private void uploadVouStatus() {
        List<VouStatus> list = vouStatusService.unUpload();
        if (!list.isEmpty()) log.info("upload vou status : " + list.size());
        list.forEach((e) -> saveMessage("VOU_STATUS", gson.toJson(e), serverQ));
    }

    public void send(VouStatus s) {
        sendTopicMessage("VOU_STATUS", gson.toJson(s));
    }

    private void uploadUnitRelation() {
        List<UnitRelation> list = relationService.unUpload();
        if (!list.isEmpty()) log.info("upload relation : " + list.size());
        list.forEach(o -> saveMessage("RELATION", gson.toJson(o), serverQ));
    }

    public void send(UnitRelation s) {
        sendTopicMessage("RELATION", gson.toJson(s));
    }


    private void uploadTrader() {
        List<Trader> list = traderService.unUploadTrader();
        if (!list.isEmpty()) log.info("upload trader : " + list.size());
        list.forEach((o) -> saveMessage("TRADER", gson.toJson(o), serverQ));
    }

    public void send(Trader t) {
        sendTopicMessage("TRADER", gson.toJson(t));
    }

    private void uploadStockUnit() {
        List<StockUnit> list = unitService.unUpload();
        if (!list.isEmpty()) log.info("upload stock unit : " + list.size());
        list.forEach((o) -> saveMessage("UNIT", gson.toJson(o), serverQ));
    }

    public void send(StockUnit t) {
        sendTopicMessage("UNIT", gson.toJson(t));
    }

    private void uploadStockType() {
        List<StockType> list = stockTypeService.unUpload();
        if (!list.isEmpty()) log.info("upload stock type : " + list.size());
        list.forEach((o) -> saveMessage("STOCK_TYPE", gson.toJson(o), serverQ));
    }

    public void send(StockType t) {
        sendTopicMessage("STOCK_TYPE", gson.toJson(t));
    }

    private void uploadStockBrand() {
        List<StockBrand> list = brandService.unUpload();
        if (!list.isEmpty()) log.info("upload stock brand : " + list.size());
        list.forEach((o) -> saveMessage("STOCK_BRAND", gson.toJson(o), serverQ));
    }

    public void send(StockBrand t) {
        sendTopicMessage("STOCK_BRAND", gson.toJson(t));
    }


    private void uploadSaleMan() {
        List<SaleMan> list = saleManService.unUpload();
        if (!list.isEmpty()) log.info("upload sale man : " + list.size());
        list.forEach(o -> saveMessage("SALEMAN", gson.toJson(o), serverQ));

    }

    public void send(SaleMan t) {
        sendTopicMessage("SALEMAN", gson.toJson(t));
    }


    private void uploadCategory() {
        List<Category> list = categoryService.unUpload();
        if (!list.isEmpty()) log.info("upload category : " + list.size());
        list.forEach(o -> saveMessage("STOCK_CATEGORY", gson.toJson(o), serverQ));
    }

    public void send(Category t) {
        sendTopicMessage("STOCK_CATEGORY", gson.toJson(t));
    }


    private void uploadLocation() {
        List<Location> list = locationService.unUpload();
        if (!list.isEmpty()) log.info("upload location : " + list.size());
        list.forEach(o -> saveMessage("LOCATION", gson.toJson(o), serverQ));
    }

    public void send(Location t) {
        sendTopicMessage("LOCATION", gson.toJson(t));
    }

    private void info(String message) {
        AppTray.showMessage(message);
    }
}
