package com.sahibinden.codecase.service;

import com.sahibinden.codecase.model.AdvertModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.util.BadWordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvertService {

    private static final Logger logger = LoggerFactory.getLogger(AdvertService.class);

    private static final String STATUS_APPROVAL_PENDING = "Onay Bekliyor";
    private static final String STATUS_ACTIVE = "Aktif";
    private static final String STATUS_INACTIVE = "Deaktif";
    private static final String STATUS_DUPLICATE = "Mükerrer";

    private final AdvertRepository advertRepository;
    private final BadWordUtil badWordUtil;
    private final StatusChangeRepository statusChangeRepository;
    private final LogService logService;

    @Autowired
    public AdvertService(AdvertRepository advertRepository, BadWordUtil badWordUtil, StatusChangeRepository statusChangeRepository, LogService logService) {
        this.advertRepository = advertRepository;
        this.badWordUtil = badWordUtil;
        this.statusChangeRepository = statusChangeRepository;
        this.logService = logService;

    }

    public String validateAdvert(AdvertModel advertModel) {
        String title = advertModel.getTitle();

        if (badWordUtil.containsBadWord(title)) {
            return "İlan girişi yasaklı kelimeden dolayı engellendi.";
        }

        return null;
    }

    public String determineStatusByCategory(String category) {
        if ("Emlak".equals(category) || "Vasıta".equals(category) || "Diğer".equals(category)) {
            return STATUS_APPROVAL_PENDING;
        } else {
            return STATUS_ACTIVE;
        }
    }

    public String updateStatusToActive(AdvertModel advert) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (STATUS_APPROVAL_PENDING.equals(advert.getStatus())) {
            advert.setStatus(STATUS_ACTIVE);
            advertRepository.save(advert);

            stopWatch.stop();
            logExecutionTime("updateStatusToActive", stopWatch.getTotalTimeMillis());

            return "İlan durumu Aktif olarak değiştirildi.";
        }

        if (STATUS_DUPLICATE.equals(advert.getStatus())) {
            stopWatch.stop();
            logExecutionTime("updateStatusToActive", stopWatch.getTotalTimeMillis());

            return "Birden fazla girilmiş bir ilanı güncelleyemezsin.";
        }

        stopWatch.stop();
        logExecutionTime("updateStatusToActive", stopWatch.getTotalTimeMillis());

        return "İlan durumu 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
    }

    public String deactivateAdvert(AdvertModel advert) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (STATUS_ACTIVE.equals(advert.getStatus()) || STATUS_APPROVAL_PENDING.equals(advert.getStatus())) {
            advert.setStatus(STATUS_INACTIVE);
            advertRepository.save(advert);

            stopWatch.stop();
            logExecutionTime("deactivateAdvert", stopWatch.getTotalTimeMillis());

            return "İlan durumu Deaktif olarak değiştirildi.";
        }

        stopWatch.stop();
        logExecutionTime("deactivateAdvert", stopWatch.getTotalTimeMillis());

        return "İlan durumu 'Aktif' ya da 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
    }

    public void saveStatusChange(int advertId, String status) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<StatusChange> statusChanges = statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(advertId);
        if (!statusChanges.isEmpty()) {
            StatusChange lastStatusChange = statusChanges.get(statusChanges.size() - 1);

            if (lastStatusChange.getStatus().equals(status)) {
                stopWatch.stop();
                logExecutionTime("saveStatusChange", stopWatch.getTotalTimeMillis());
                return;
            }
        }

        StatusChange statusChange = new StatusChange();
        statusChange.setAdvertId(advertId);
        statusChange.setStatus(status);
        statusChange.setChangeTime(LocalDateTime.now());
        statusChangeRepository.save(statusChange);

        stopWatch.stop();
        logExecutionTime("saveStatusChange", stopWatch.getTotalTimeMillis());
    }

    public void logExecutionTime(String methodName, long executionTime) {
        String logMessage = String.format("Method %s took %d ms to execute", methodName, executionTime);
        logger.info(logMessage);
        if (executionTime > 5) {
            logService.addLog(logMessage);
        }
    }
}
