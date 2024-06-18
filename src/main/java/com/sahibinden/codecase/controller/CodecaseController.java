package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.CodecaseModel;
import com.sahibinden.codecase.repository.CodecaseRepository;
import com.sahibinden.codecase.util.BadWordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public CodecaseController(CodecaseRepository codecaseRepository, BadWordUtil badWordUtil) {
        this.codecaseRepository = codecaseRepository;
        this.badWordUtil = badWordUtil;
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

        codecaseRepository.save(codecaseModel);
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
                .map(this::updateStatusToActive)
                .orElse("İlan bulunamadı.");
    }

    @PutMapping("/deactivateClientAdvert/{id}")
    public String deactivateClientAdvert(@PathVariable int id) {
        return codecaseRepository.findById(id)
                .map(this::deactivateAdvert)
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
}
