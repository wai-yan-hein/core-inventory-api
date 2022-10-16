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
    private boolean progress = false;
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();

    @Scheduled(fixedRate = 100000)
    private void uploadToServer() {
        boolean sync = Util1.getBoolean(environment.getProperty("cloud.upload.server"));
        if (sync) {
            if (!progress) {
                progress = true;
                uploadSetup();
                //uploadTransaction();
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

    private void uploadSetup() {
        info("upload setup start.");
        uploadVouStatus();
        uploadUnitRelation();
        uploadTraderGroup();
        uploadTrader();
        uploadStockUnit();
        uploadStockType();
        uploadStockBrand();
        uploadSaleMan();
        uploadCategory();
        uploadLocation();
        uploadPattern();
        uploadStock();
        info("upload setup end.");
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
        //sendMessage("SALE", "-", null);

    }

    private void uploadPurchase() {
        log.info("upload purchase.");
        //sendMessage("PURCHASE", "-", null);

    }

    private void uploadReturnIn() {
        log.info("upload return in.");
        //sendMessage("RETURN_IN", "-", null);
    }

    private void uploadReturnOut() {
        log.info("upload return out.");
        //sendMessage("RETURN_OUT", "-", null);


    }

    private void uploadStockInOut() {
        log.info("upload stock in out.");
        //sendMessage("STOCK_IO", "-", null);


    }

    private void uploadTransfer() {
        log.info("upload transfer.");
        //sendMessage("TRANSFER", "-", null);

    }

    private void uploadOpening() {
        log.info("upload opening.");
        //sendMessage("OPENING", "-", null);


    }

    private void uploadStock() {
        log.info("upload stock.");
        List<Stock> list = stockService.unUpload();
        list.forEach(o -> sendMessage("STOCK", gson.toJson(o)));
    }

    private void uploadPattern() {
        log.info("upload pattern.");
        List<Pattern> list = patternService.unUpload();
        list.forEach(p -> sendMessage("PATTERN", gson.toJson(p)));
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
