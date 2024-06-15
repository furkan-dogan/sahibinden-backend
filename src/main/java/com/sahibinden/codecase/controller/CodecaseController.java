package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.model.CodecaseModel;
import com.sahibinden.codecase.repository.CodecaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sahibinden")
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
        return("customer advert saved !");
    }

    @DeleteMapping("/deleteClientAdvert/{id}")
    public String deleteClientAdvert(@PathVariable int id) {
        Optional<CodecaseModel> optionalCodecaseModel = codecaseRepository.findById(id);
        if (optionalCodecaseModel.isPresent()) {
            codecaseRepository.deleteById(id);
            return "ClientAdvert deleted successfully.";
        } else {
            return "ClientAdvert not found.";
        }
    }

    @PutMapping("/updateClientAdvert/{id}")
    public String updateClientAdvert(@PathVariable int id, @RequestBody CodecaseModel updatedCodecaseModel){
        Optional<CodecaseModel> optionalExistingAdvert = codecaseRepository.findById(id);
        if (optionalExistingAdvert.isPresent()) {
            CodecaseModel existingAdvert = optionalExistingAdvert.get();

            if ("Onay Bekliyor".equals(existingAdvert.getStatus())) {
                updatedCodecaseModel.setStatus("Aktif");
            }

            if ("Mükerrer".equals(existingAdvert.getStatus())) {
                return "Cannot update a duplicate advert.";
            }

            updatedCodecaseModel.setID(id);
            codecaseRepository.save(updatedCodecaseModel);
            return "ClientAdvert updated.";
        } else {
            return "ClientAdvert not found.";
        }
    }



}
