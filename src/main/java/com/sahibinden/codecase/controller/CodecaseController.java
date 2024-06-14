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

    @PostMapping("/saveClientAdvert")
    public String saveClientAdvert(@RequestBody CodecaseModel codecaseModel){
        codecaseRepository.save(codecaseModel);
        return("ClientAdvertSave..");
    }

    @GetMapping("/getAllClientAdvert")
    public List<CodecaseModel> getAll(){
        return codecaseRepository.findAll();
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

}
