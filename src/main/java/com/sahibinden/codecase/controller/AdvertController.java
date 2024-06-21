package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.AdvertModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.service.LogService;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.service.AdvertService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard/classifieds")
public class AdvertController {

    private static final Logger logger = LoggerFactory.getLogger(AdvertController.class);

    private final AdvertRepository advertRepository;
    private final AdvertService advertService;
    private final StatusChangeRepository statusChangeRepository;
    private final LogService logService;

    @Autowired
    public AdvertController(AdvertRepository advertRepository, AdvertService advertService, StatusChangeRepository statusChangeRepository, LogService logService) {
        this.advertRepository = advertRepository;
        this.advertService = advertService;
        this.statusChangeRepository = statusChangeRepository;
        this.logService = logService;
    }

    @GetMapping("/getAllClientAdvert")
    public List<AdvertModel> getAllClientAdvert() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<AdvertModel> advertModels = advertRepository.findAll();

        stopWatch.stop();
        advertService.logExecutionTime("/getAllClientAdvert", stopWatch.getTotalTimeMillis());

        return advertModels;
    }

    @PostMapping("/saveClientAdvert")
    public ResponseEntity<String> saveClientAdvert(@RequestBody @Valid AdvertModel advertModel, BindingResult bindingResult) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("İlan kaydedilemedi.\n");
            bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        String validationMessage = advertService.validateAdvert(advertModel);
        if (validationMessage != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationMessage);
        }

        List<AdvertModel> existingAdverts = advertRepository.findByCategoryAndTitleAndDescription(
                advertModel.getCategory(), advertModel.getTitle(), advertModel.getDescription()
        );

        if (!existingAdverts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aynı başlık ve açıklamaya sahip başka bir ilan mevcut.");
        } else {
            advertModel.setStatus(advertService.determineStatusByCategory(advertModel.getCategory()));
        }

        AdvertModel savedAdvert = advertRepository.save(advertModel);
        advertService.saveStatusChange(savedAdvert.getID(), savedAdvert.getStatus());

        stopWatch.stop();
        advertService.logExecutionTime("/saveClientAdvert", stopWatch.getTotalTimeMillis());

        return ResponseEntity.ok("İlan kaydedildi !");
    }

    @DeleteMapping("/deleteClientAdvert/{id}")
    public String deleteClientAdvert(@PathVariable int id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String result = advertRepository.findById(id)
                .map(advert -> {
                    advertRepository.deleteById(id);
                    return "İlan başarıyla silindi.";
                })
                .orElse("İlan bulunamadı.");

        stopWatch.stop();
        advertService.logExecutionTime("/deleteClientAdvert/{id}", stopWatch.getTotalTimeMillis());

        return result;
    }

    @PutMapping("/updateClientAdvertStatus/{id}")
    public String updateClientAdvertStatus(@PathVariable int id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String result = advertRepository.findById(id)
                .map(advert -> {
                    String updateResult = advertService.updateStatusToActive(advert);
                    advertService.saveStatusChange(advert.getID(), advert.getStatus());
                    return updateResult;
                })
                .orElse("İlan bulunamadı.");

        stopWatch.stop();
        advertService.logExecutionTime("/updateClientAdvertStatus/{id}", stopWatch.getTotalTimeMillis());

        return result;
    }

    @PutMapping("/deactivateClientAdvert/{id}")
    public String deactivateClientAdvert(@PathVariable int id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String result = advertRepository.findById(id)
                .map(advert -> {
                    String deactivateResult = advertService.deactivateAdvert(advert);
                    advertService.saveStatusChange(advert.getID(), advert.getStatus());
                    return deactivateResult;
                })
                .orElse("İlan bulunamadı.");

        stopWatch.stop();
        advertService.logExecutionTime("/deactivateClientAdvert/{id}", stopWatch.getTotalTimeMillis());

        return result;
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        long activeCount = advertRepository.countByStatus("Aktif");
        long inactiveCount = advertRepository.countByStatus("Deaktif");
        long pendingApprovalCount = advertRepository.countByStatus("Onay Bekliyor");
        long duplicateCount = advertRepository.countByStatus("Mükerrer");

        stopWatch.stop();
        advertService.logExecutionTime("/statistics", stopWatch.getTotalTimeMillis());

        return new StatisticsResponse(activeCount, inactiveCount, pendingApprovalCount, duplicateCount);
    }

    @GetMapping("/getStatusChanges/{id}")
    public List<StatusChange> getStatusChanges(@PathVariable int id) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<StatusChange> statusChanges = statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(id);

        stopWatch.stop();
        advertService.logExecutionTime("/getStatusChanges/{id}", stopWatch.getTotalTimeMillis());

        return statusChanges;
    }

    @GetMapping("/logs")
    public List<String> getLogs() {
        return logService.getLogs();
    }
}
