package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.AdvertModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.service.AdvertService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard/classifieds")
public class AdvertController {

    private final AdvertRepository advertRepository;
    private final AdvertService advertService;
    private final StatusChangeRepository statusChangeRepository;

    @Autowired
    public AdvertController(AdvertRepository advertRepository, AdvertService advertService, StatusChangeRepository statusChangeRepository) {
        this.advertRepository = advertRepository;
        this.advertService = advertService;
        this.statusChangeRepository = statusChangeRepository;
    }

    @GetMapping("/getAllClientAdvert")
    public List<AdvertModel> getAllClientAdvert() {
        return advertRepository.findAll();
    }

    @PostMapping("/saveClientAdvert")
    public String saveClientAdvert(@RequestBody AdvertModel advertModel) {
        String validationMessage = advertService.validateAdvert(advertModel);
        if (validationMessage != null) {
            return validationMessage;
        }

        List<AdvertModel> existingAdverts = advertRepository.findByCategoryAndTitleAndDescription(
                advertModel.getCategory(), advertModel.getTitle(), advertModel.getDescription()
        );

        if (!existingAdverts.isEmpty()) {
            advertModel.setStatus("Mükerrer");
        } else {
            advertModel.setStatus(advertService.determineStatusByCategory(advertModel.getCategory()));
        }

        AdvertModel savedAdvert = advertRepository.save(advertModel);
        advertService.saveStatusChange(savedAdvert.getID(), savedAdvert.getStatus());
        return "İlan kaydedildi !";
    }

    @DeleteMapping("/deleteClientAdvert/{id}")
    public String deleteClientAdvert(@PathVariable int id) {
        return advertRepository.findById(id)
                .map(advert -> {
                    advertRepository.deleteById(id);
                    return "İlan başarıyla silindi.";
                })
                .orElse("İlan bulunamadı.");
    }

    @PutMapping("/updateClientAdvertStatus/{id}")
    public String updateClientAdvertStatus(@PathVariable int id) {
        return advertRepository.findById(id)
                .map(advert -> {
                    String result = advertService.updateStatusToActive(advert);
                    advertService.saveStatusChange(advert.getID(), advert.getStatus());
                    return result;
                })
                .orElse("İlan bulunamadı.");
    }

    @PutMapping("/deactivateClientAdvert/{id}")
    public String deactivateClientAdvert(@PathVariable int id) {
        return advertRepository.findById(id)
                .map(advert -> {
                    String result = advertService.deactivateAdvert(advert);
                    advertService.saveStatusChange(advert.getID(), advert.getStatus());
                    return result;
                })
                .orElse("İlan bulunamadı.");
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics() {
        long activeCount = advertRepository.countByStatus("Aktif");
        long inactiveCount = advertRepository.countByStatus("Deaktif");
        long pendingApprovalCount = advertRepository.countByStatus("Onay Bekliyor");
        long duplicateCount = advertRepository.countByStatus("Mükerrer");

        return new StatisticsResponse(activeCount, inactiveCount, pendingApprovalCount, duplicateCount);
    }

    @GetMapping("/getStatusChanges/{id}")
    public List<StatusChange> getStatusChanges(@PathVariable int id) {
        return statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(id);
    }
}