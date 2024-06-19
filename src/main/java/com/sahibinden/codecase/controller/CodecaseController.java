package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.CodecaseModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.repository.CodecaseRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.util.BadWordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/dashboard/classifieds")
public class CodecaseController {

    private static final String STATUS_APPROVAL_PENDING = "Onay Bekliyor";
    private static final String STATUS_ACTIVE = "Aktif";
    private static final String STATUS_INACTIVE = "Deaktif";
    private static final String STATUS_DUPLICATE = "Mükerrer";

    private final CodecaseRepository codecaseRepository;
    private final BadWordUtil badWordUtil;
    private final StatusChangeRepository statusChangeRepository;

    @Autowired
    public CodecaseController(CodecaseRepository codecaseRepository, BadWordUtil badWordUtil, StatusChangeRepository statusChangeRepository) {
        this.codecaseRepository = codecaseRepository;
        this.badWordUtil = badWordUtil;
        this.statusChangeRepository = statusChangeRepository;
    }

    @GetMapping("/getAllClientAdvert")
    public List<CodecaseModel> getAllClientAdvert(){
        return codecaseRepository.findAll();
    }

    @PostMapping("/saveClientAdvert")
    public String saveClientAdvert(@RequestBody CodecaseModel codecaseModel) {
        String validationMessage = validateAdvert(codecaseModel);
        if (validationMessage != null) {
            return validationMessage;
        }

        List<CodecaseModel> existingAdverts = codecaseRepository.findByCategoryAndTitleAndDescription(
                codecaseModel.getCategory(), codecaseModel.getTitle(), codecaseModel.getDescription()
        );

        if (!existingAdverts.isEmpty()) {
            codecaseModel.setStatus(STATUS_DUPLICATE);
        } else {
            codecaseModel.setStatus(determineStatusByCategory(codecaseModel.getCategory()));
        }

        CodecaseModel savedAdvert = codecaseRepository.save(codecaseModel);
        saveStatusChange(savedAdvert.getID(), savedAdvert.getStatus());
        return "İlan kaydedildi !";
    }

    @DeleteMapping("/deleteClientAdvert/{id}")
    public String deleteClientAdvert(@PathVariable int id) {
        return codecaseRepository.findById(id)
                .map(advert -> {
                    codecaseRepository.deleteById(id);
                    return "İlan başarıyla silindi.";
                })
                .orElse("İlan bulunamadı.");
    }

    @PutMapping("/updateClientAdvertStatus/{id}")
    public String updateClientAdvertStatus(@PathVariable int id) {
        return codecaseRepository.findById(id)
                .map(advert -> {
                    String result = updateStatusToActive(advert);
                    saveStatusChange(advert.getID(), advert.getStatus());
                    return result;
                })
                .orElse("İlan bulunamadı.");
    }

    @PutMapping("/deactivateClientAdvert/{id}")
    public String deactivateClientAdvert(@PathVariable int id) {
        return codecaseRepository.findById(id)
                .map(advert -> {
                    String result = deactivateAdvert(advert);
                    saveStatusChange(advert.getID(), advert.getStatus());
                    return result;
                })
                .orElse("İlan bulunamadı.");
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics() {
        long activeCount = codecaseRepository.countByStatus(STATUS_ACTIVE);
        long inactiveCount = codecaseRepository.countByStatus(STATUS_INACTIVE);
        long pendingApprovalCount = codecaseRepository.countByStatus(STATUS_APPROVAL_PENDING);
        long duplicateCount = codecaseRepository.countByStatus(STATUS_DUPLICATE);

        return new StatisticsResponse(activeCount, inactiveCount, pendingApprovalCount, duplicateCount);
    }

    @GetMapping("/getStatusChanges/{id}")
    public List<StatusChange> getStatusChanges(@PathVariable int id) {
        return statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(id);
    }

    private String validateAdvert(CodecaseModel codecaseModel) {
        String title = codecaseModel.getTitle();
        if (title == null || title.length() < 10 || title.length() > 50) {
            return "Başlık 10 ila 50 karakter sayısı arasında olmalıdır.";
        }

        if (!Character.isLetterOrDigit(title.charAt(0))) {
            return "Başlık harf veya rakam ile başlamalıdır.";
        }

        if (badWordUtil.containsBadWord(title)) {
            return "İlan girişi yasaklı kelimeden dolayı engellendi.";
        }

        String description = codecaseModel.getDescription();
        if (description.length() < 20 || description.length() > 200) {
            return "Açıklama 20 ila 200 karakter arasında olmalıdır.";
        }

        return null;
    }

    private String determineStatusByCategory(String category) {
        if ("Emlak".equals(category) || "Vasıta".equals(category) || "Diğer".equals(category)) {
            return STATUS_APPROVAL_PENDING;
        } else {
            return STATUS_ACTIVE;
        }
    }

    private String updateStatusToActive(CodecaseModel advert) {
        if (STATUS_APPROVAL_PENDING.equals(advert.getStatus())) {
            advert.setStatus(STATUS_ACTIVE);
            codecaseRepository.save(advert);
            return "İlan durumu Aktif olarak değiştirildi.";
        }

        if (STATUS_DUPLICATE.equals(advert.getStatus())) {
            return "Birden fazla girilmiş bir ilanı güncelleyemezsin.";
        }

        return "İlan durumu 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
    }

    private String deactivateAdvert(CodecaseModel advert) {
        if (STATUS_ACTIVE.equals(advert.getStatus()) || STATUS_APPROVAL_PENDING.equals(advert.getStatus())) {
            advert.setStatus(STATUS_INACTIVE);
            codecaseRepository.save(advert);
            return "İlan durumu Deaktif olarak değiştirildi.";
        }

        return "İlan durumu 'Aktif' ya da 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
    }

    private void saveStatusChange(int advertId, String status) {
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
