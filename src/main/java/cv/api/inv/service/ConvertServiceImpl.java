package cv.api.inv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConvertServiceImpl implements ConverterService {
    @Autowired
    private ReportService reportService;

    @Override
    public void convertToUnicode() {

    }

    private void convertStock() {
        String sql="select stock_name\n" +
                "from stock\n";
    }
}
