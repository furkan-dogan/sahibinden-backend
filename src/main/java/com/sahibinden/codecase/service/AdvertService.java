package com.sahibinden.codecase.service;

import com.sahibinden.codecase.model.AdvertModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.util.BadWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvertService {

    private static final String STATUS_APPROVAL_PENDING = "Onay Bekliyor";
    private static final String STATUS_ACTIVE = "Aktif";
    private static final String STATUS_INACTIVE = "Deaktif";
    private static final String STATUS_DUPLICATE = "Mükerrer";

    private final AdvertRepository advertRepository;
    private final BadWordUtil badWordUtil;
    private final StatusChangeRepository statusChangeRepository;

    @Autowired
    public AdvertService(AdvertRepository advertRepository, BadWordUtil badWordUtil, StatusChangeRepository statusChangeRepository) {
        this.advertRepository = advertRepository;
        this.badWordUtil = badWordUtil;
        this.statusChangeRepository = statusChangeRepository;
    }

    public String validateAdvert(AdvertModel advertModel) {
        String title = advertModel.getTitle();
        if (title == null || title.length() < 10 || title.length() > 50) {
            return "Başlık 10 ila 50 karakter sayısı arasında olmalıdır.";
        }

        if (!Character.isLetterOrDigit(title.charAt(0))) {
            return "Başlık harf veya rakam ile başlamalıdır.";
        }

        if (badWordUtil.containsBadWord(title)) {
            return "İlan girişi yasaklı kelimeden dolayı engellendi.";
        }

        String description = advertModel.getDescription();
        if (description.length() < 20 || description.length() > 200) {
            return "Açıklama 20 ila 200 karakter arasında olmalıdır.";
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
        if (STATUS_APPROVAL_PENDING.equals(advert.getStatus())) {
            advert.setStatus(STATUS_ACTIVE);
            advertRepository.save(advert);
            return "İlan durumu Aktif olarak değiştirildi.";
        }

        if (STATUS_DUPLICATE.equals(advert.getStatus())) {
            return "Birden fazla girilmiş bir ilanı güncelleyemezsin.";
        }

        return "İlan durumu 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
    }

    public String deactivateAdvert(AdvertModel advert) {
        if (STATUS_ACTIVE.equals(advert.getStatus()) || STATUS_APPROVAL_PENDING.equals(advert.getStatus())) {
            advert.setStatus(STATUS_INACTIVE);
            advertRepository.save(advert);
            return "İlan durumu Deaktif olarak değiştirildi.";
        }

        return "İlan durumu 'Aktif' ya da 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
    }

    public void saveStatusChange(int advertId, String status) {
        List<StatusChange> statusChanges = statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(advertId);
        if (!statusChanges.isEmpty()) {
            StatusChange lastStatusChange = statusChanges.get(statusChanges.size() - 1);

            if (lastStatusChange.getStatus().equals(status)) {
                return;
            }
        }

        StatusChange statusChange = new StatusChange();
        statusChange.setAdvertId(advertId);
        statusChange.setStatus(status);
        statusChange.setChangeTime(LocalDateTime.now());
        statusChangeRepository.save(statusChange);
    }
}
