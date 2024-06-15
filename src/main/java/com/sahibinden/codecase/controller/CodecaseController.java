package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.CodecaseModel;
import com.sahibinden.codecase.repository.CodecaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard/classifieds")
public class CodecaseController {

    @Autowired
    private CodecaseRepository codecaseRepository;

    @GetMapping("/getAllClientAdvert")
    public List<CodecaseModel> getAll(){
        return codecaseRepository.findAll();
    }

    @PostMapping("/saveClientAdvert")
    public String saveClientAdvert(@RequestBody CodecaseModel codecaseModel){
        List<CodecaseModel> existingAdverts = codecaseRepository.findByCategoryAndTitleAndDescription(
                codecaseModel.getCategory(),
                codecaseModel.getTitle(),
                codecaseModel.getDescription()
        );

        if (!existingAdverts.isEmpty()) {
            codecaseModel.setStatus("Mükerrer");
        } else {
            String category = codecaseModel.getCategory();
            if (category.equals("Emlak") || category.equals("Vasıta") || category.equals("Diğer")) {
                codecaseModel.setStatus("Onay Bekliyor");
            } else {
                codecaseModel.setStatus("Aktif");
            }
        }
        codecaseRepository.save(codecaseModel);
        return("İlan kaydedildi !");
    }

    @DeleteMapping("/deleteClientAdvert/{id}")
    public String deleteClientAdvert(@PathVariable int id) {
        Optional<CodecaseModel> optionalCodecaseModel = codecaseRepository.findById(id);
        if (optionalCodecaseModel.isPresent()) {
            codecaseRepository.deleteById(id);
            return "İlan başarıyla silindi.";
        } else {
            return "İlan bulunamadı.";
        }
    }

    @PutMapping("/updateClientAdvertStatus/{id}")
    public String updateClientAdvertStatus(@PathVariable int id) {
        Optional<CodecaseModel> optionalExistingAdvert = codecaseRepository.findById(id);
        if (optionalExistingAdvert.isPresent()) {
            CodecaseModel existingAdvert = optionalExistingAdvert.get();

            if ("Onay Bekliyor".equals(existingAdvert.getStatus())) {
                existingAdvert.setStatus("Aktif");
                codecaseRepository.save(existingAdvert);
                return "İlan durumu Aktif olarak değiştirildi.";
            }

            if ("Mükerrer".equals(existingAdvert.getStatus())) {
                return "Birden fazla girilmiş bir ilanı güncelleyemezsin.";
            }

            return "İlan durumu 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
        } else {
            return "İlan bulunamadı.";
        }
    }

    @PutMapping("/deactivateClientAdvert/{id}")
    public String deactivateClientAdvert(@PathVariable int id) {
        Optional<CodecaseModel> optionalExistingAdvert = codecaseRepository.findById(id);
        if (optionalExistingAdvert.isPresent()) {
            CodecaseModel existingAdvert = optionalExistingAdvert.get();

            if ("Aktif".equals(existingAdvert.getStatus()) || "Onay Bekliyor".equals(existingAdvert.getStatus())) {
                existingAdvert.setStatus("Deaktif");
                codecaseRepository.save(existingAdvert);
                return "İlan durumu Deaktif olarak değiştirildi.";
            }

            return "İlan durumu 'Aktif' ya da 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.";
        } else {
            return "İlan bulunamadı.";
        }
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics() {
        long activeCount = codecaseRepository.countByStatus("Aktif");
        long inactiveCount = codecaseRepository.countByStatus("Deaktif");
        long pendingApprovalCount = codecaseRepository.countByStatus("Onay Bekliyor");
        long duplicateCount = codecaseRepository.countByStatus("Mükerrer");

        return new StatisticsResponse(activeCount, inactiveCount, pendingApprovalCount, duplicateCount);
    }


}
