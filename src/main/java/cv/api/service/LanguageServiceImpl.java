package cv.api.service;

import cv.api.dao.LanguageDao;
import cv.api.dao.OrderStatusDao;
import cv.api.entity.Language;
import cv.api.entity.LanguageKey;
import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    LanguageDao dao;
    @Autowired
    private SeqTableService seqService;
    @Override
    public Language save(Language status) {
        return dao.save(status);
    }

    @Override
    public List<Language> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(LanguageKey key) {
        return dao.delete(key);
    }

    @Override
    public Language findById(LanguageKey key) {
        return dao.findById(key);
    }

    @Override
    public List<Language> search(String description) {
        return dao.search(description);
    }

    @Override
    public List<Language> unUpload() {
        return dao.unUpload();
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Language> getLanguage(LocalDateTime updatedDate) {
        return dao.getLanguage(updatedDate);
    }

}
