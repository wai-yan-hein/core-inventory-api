package cv.api.service;

import cv.api.dao.FontDao;
import cv.api.entity.CFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FontServiceImpl implements FontService {
    @Autowired
    private FontDao fontDao;

    @Override
    public List<CFont> getFont() {
        return fontDao.getFont();
    }
}
