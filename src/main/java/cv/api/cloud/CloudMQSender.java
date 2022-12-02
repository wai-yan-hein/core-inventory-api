package cv.api.cloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import cv.api.tray.AppTray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Session;
import java.text.DateFormat;
import java.util.List;

@Slf4j
@Component
public class CloudMQSender {
    @Value("${cloud.activemq.client.queue}")
    private String listenQ;
    @Autowired
    private JmsTemplate cloudMQTemplate;
    @Autowired
    private Environment environment;
    //service
    @Autowired
    private PatternService patternService;
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
    private StockInOutService inOutService;
    @Autowired
    private TransferHisService transferHisService;
    @Autowired
    private OPHisService opHisService;
    private boolean progress = false;
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();

    @Scheduled(fixedRate = 10000000)
    private void uploadToServer() {
        boolean sync = Util1.getBoolean(environment.getProperty("cloud.upload.server"));
        if (sync) {
            if (!progress) {
                progress = true;
                //uploadSetup();
                //uploadTransaction();
                downloadSetup();
                progress = false;
            }
        }
    }

    private void sendMessage(String entity, String data) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "SENT");
            mm.setString("DATA", data);
            return mm;
        };
        String serverQ = environment.getProperty("cloud.activemq.server.queue");
        if (serverQ != null) {
            cloudMQTemplate.send(serverQ, mc);
        }
    }

    private void requestSetup(String entity, String date) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("SENDER_QUEUE", listenQ);
            mm.setString("ENTITY", entity);
            mm.setString("OPTION", "REQUEST-SETUP");
            mm.setString("DATA", date);
            return mm;
        };
        String serverQ = environment.getProperty("cloud.activemq.server.queue");
        if (serverQ != null) {
            cloudMQTemplate.send(serverQ, mc);
        }
    }

    private void uploadSetup() {
        log.info("upload setup start.");
        uploadVouStatus();
        uploadUnitRelation();
        uploadTrader();
        uploadStockUnit();
        uploadStockType();
        uploadStockBrand();
        uploadSaleMan();
        uploadCategory();
        uploadLocation();
        //uploadPattern();
        uploadStock();
        log.info("upload setup end.");
        uploadStock();
        info("upload setup end.");
    }

    private void downloadSetup() {
        requestSetup("VOU_STATUS", gson.toJson(new VouStatus(vouStatusService.getMaxDate())));
        requestSetup("RELATION", gson.toJson(new UnitRelation(relationService.getMaxDate())));
        requestSetup("TRADER", gson.toJson(new Trader(traderService.getMaxDate())));
        requestSetup("SALEMAN", gson.toJson(new SaleMan(saleManService.getMaxDate())));
        requestSetup("UNIT", gson.toJson(new StockUnit(unitService.getMaxDate())));
        requestSetup("STOCK_TYPE", gson.toJson(new StockType(stockTypeService.getMaxDate())));
        requestSetup("STOCK_BRAND", gson.toJson(new StockBrand(brandService.getMaxDate())));
        requestSetup("STOCK_CATEGORY", gson.toJson(new Category(categoryService.getMaxDate())));
        requestSetup("LOCATION", gson.toJson(new Location(locationService.getMaxDate())));
        requestSetup("STOCK", gson.toJson(new Stock(stockService.getMaxDate())));
    }


    private void downloadTransaction() {
        requestSetup("SALE", gson.toJson(new Stock(stockService.getMaxDate())));
        requestSetup("PURCHASE", gson.toJson(new Stock(stockService.getMaxDate())));
        requestSetup("RETURN_IN", gson.toJson(new Stock(stockService.getMaxDate())));
        requestSetup("RETURN_OUT", gson.toJson(new Stock(stockService.getMaxDate())));
        requestSetup("STOCK_IO", gson.toJson(new Stock(stockService.getMaxDate())));
    }

    private void uploadTransaction() {
        info("upload transaction start.");
        uploadOpening();
        uploadSale();
        uploadPurchase();
        uploadReturnIn();
        uploadReturnOut();
        uploadStockInOut();
        uploadTransfer();
        info("upload transaction end.");
    }

    private void uploadSale() {
        log.info("upload sale.");
        List<SaleHis> list = saleHisService.unUpload();
        list.forEach(o -> sendMessage("SALE", gson.toJson(o)));
    }

    private void uploadPurchase() {
        log.info("upload purchase.");
        List<PurHis> list = purHisService.unUpload();
        list.forEach(o -> sendMessage("PURCHASE", gson.toJson(o)));
    }

    private void uploadReturnIn() {
        log.info("upload return in.");
        List<RetInHis> list = retInService.unUpload();
        list.forEach(o -> sendMessage("RETURN_IN", gson.toJson(o)));
    }

    private void uploadReturnOut() {
        log.info("upload return out.");
        List<RetOutHis> list = retOutService.unUpload();
        list.forEach(o -> sendMessage("RETURN_OUT", gson.toJson(o)));
    }

    private void uploadStockInOut() {
        log.info("upload stock in out.");
        List<StockInOut> list = inOutService.unUpload();
        list.forEach(o -> sendMessage("STOCK_IO", gson.toJson(o)));
    }

    private void uploadTransfer() {
        log.info("upload transfer.");
        List<TransferHis> list = transferHisService.unUpload();
        list.forEach(o -> sendMessage("TRANSFER", gson.toJson(o)));
    }

    private void uploadOpening() {
        log.info("upload opening.");
        List<OPHis> list = opHisService.unUpload();
        list.forEach(o -> sendMessage("OPENING", gson.toJson(o)));
    }

    private void uploadStock() {
        log.info("upload stock.");
        List<Stock> list = stockService.unUpload();
        list.forEach(o -> sendMessage("STOCK", gson.toJson(o)));
    }

    private void uploadPattern() {
        log.info("upload pattern.");
        List<Pattern> list = patternService.unUpload();
        list.forEach(p -> sendMessage( "PATTERN", gson.toJson(p)));
    }

    private void uploadVouStatus() {
        log.info("upload vou status.");
        List<VouStatus> list = vouStatusService.unUpload();
        list.forEach((e) -> sendMessage("VOU_STATUS", gson.toJson(e)));
    }

    private void uploadUnitRelation() {
        log.info("upload unit relation.");
        List<UnitRelation> list = relationService.unUpload();
        list.forEach(o -> sendMessage("RELATION", gson.toJson(o)));
    }

    private void uploadTraderGroup() {
        log.info("upload trader group.");
        List<TraderGroup> list = traderGroupService.unUpload();
        list.forEach((o) -> sendMessage("TRADER_GROUP", gson.toJson(o)));
    }

    private void uploadTrader() {
        log.info("upload trader.");
        List<Trader> list = traderService.unUploadTrader();
        list.forEach((o) -> sendMessage("TRADER", gson.toJson(o)));
    }

    private void uploadStockUnit() {
        log.info("upload stock unit.");
        List<StockUnit> list = unitService.unUpload();
        list.forEach((o) -> sendMessage("UNIT", gson.toJson(o)));
    }

    private void uploadStockType() {
        log.info("upload stock type.");
        List<StockType> list = stockTypeService.unUpload();
        list.forEach((o) -> sendMessage("STOCK_TYPE", gson.toJson(o)));
    }

    private void uploadStockBrand() {
        log.info("upload stock brand.");
        List<StockBrand> list = brandService.unUpload();
        list.forEach((o) -> sendMessage("STOCK_BRAND", gson.toJson(o)));
    }

    private void uploadSaleMan() {
        log.info("upload sale man.");
        List<SaleMan> list = saleManService.unUpload();
        list.forEach(o -> sendMessage("SALEMAN", gson.toJson(o)));

    }

    private void uploadCategory() {
        log.info("upload category.");
        List<Category> list = categoryService.unUpload();
        list.forEach(o -> sendMessage("STOCK_CATEGORY", gson.toJson(o)));
    }

    private void uploadLocation() {
        log.info("upload location.");
        List<Location> list = locationService.unUpload();
        list.forEach(o -> sendMessage("LOCATION", gson.toJson(o)));
    }

    private void info(String message) {
        AppTray.showMessage(message);
    }
}
