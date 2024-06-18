package com.sahibinden.codecase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.CodecaseModel;
import com.sahibinden.codecase.repository.CodecaseRepository;
import com.sahibinden.codecase.util.BadWordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodecaseController.class)
public class CodecaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodecaseRepository codecaseRepository;

    @MockBean
    private BadWordUtil badWordUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CodecaseModel> codecaseModels;

    @BeforeEach
    void setUp() {
        codecaseModels = Arrays.asList(
                new CodecaseModel(1, "Title1", "Description1", "Emlak", "Aktif"),
                new CodecaseModel(2, "Title2", "Description2", "Vasıta", "Deaktif"),
                new CodecaseModel(3, "Title3", "Description3", "Diğer", "Mükerrer")
        );

        when(codecaseRepository.findAll()).thenReturn(codecaseModels);
    }

    @Test
    void getAllClientAdvertTest() throws Exception {
        mockMvc.perform(get("/dashboard/classifieds/getAllClientAdvert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[0].description").value("Description1"))
                .andExpect(jsonPath("$[0].category").value("Emlak"))
                .andExpect(jsonPath("$[0].status").value("Aktif"))
                .andExpect(jsonPath("$[1].title").value("Title2"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].category").value("Vasıta"))
                .andExpect(jsonPath("$[1].status").value("Deaktif"))
                .andExpect(jsonPath("$[2].title").value("Title3"))
                .andExpect(jsonPath("$[2].description").value("Description3"))
                .andExpect(jsonPath("$[2].category").value("Diğer"))
                .andExpect(jsonPath("$[2].status").value("Mükerrer"));
    }

    @Test
    void saveClientAdvertTest() throws Exception {
        CodecaseModel newAdvert = new CodecaseModel();
        newAdvert.setTitle("ValidTitle");
        newAdvert.setDescription("Valid description with sufficient length");
        newAdvert.setCategory("Emlak");

        when(badWordUtil.containsBadWord(any())).thenReturn(false);
        when(codecaseRepository.save(any(CodecaseModel.class))).thenReturn(newAdvert);

        mockMvc.perform(post("/dashboard/classifieds/saveClientAdvert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdvert)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan kaydedildi !"));
    }

    @Test
    void saveClientAdvertWithBadWordTest() throws Exception {
        CodecaseModel newAdvert = new CodecaseModel();
        newAdvert.setTitle("opsiyonluopsiyonlu");
        newAdvert.setDescription("Valid description with sufficient length");
        newAdvert.setCategory("Emlak");

        when(badWordUtil.containsBadWord(any())).thenReturn(true);

        mockMvc.perform(post("/dashboard/classifieds/saveClientAdvert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdvert)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan girişi yasaklı kelimeden dolayı engellendi."));
    }

    @Test
    void saveClientAdvertWithShortTitleTest() throws Exception {
        CodecaseModel newAdvert = new CodecaseModel();
        newAdvert.setTitle("Short");
        newAdvert.setDescription("ClientAdvertWithShortTitleTest denemesi");
        newAdvert.setCategory("Emlak");

        mockMvc.perform(post("/dashboard/classifieds/saveClientAdvert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdvert)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Başlık 10 ila 50 karakter sayısı arasında olmalıdır."));
    }

    @Test
    void saveClientAdvertWithLongDescriptionTest() throws Exception {
        CodecaseModel newAdvert = new CodecaseModel();
        newAdvert.setTitle("ValidTitle");
        newAdvert.setDescription("Bu açıklama çok uzun ve izin verilen maksimum karakter sınırı olan 200 karakteri aşıyor. Dolayısıyla bu, " +
                "doğrulamayı tetiklemeli ve bir hata mesajı döndürmelidir. Sınırı aştığından emin olmak için burayı uzatıyorum.");
        newAdvert.setCategory("Emlak");

        mockMvc.perform(post("/dashboard/classifieds/saveClientAdvert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdvert)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Açıklama 20 ila 200 karakter arasında olmalıdır."));
    }

    @Test
    void deleteClientAdvertTest() throws Exception {
        int advertId = 1;
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.of(new CodecaseModel()));
        doNothing().when(codecaseRepository).deleteById(advertId);

        mockMvc.perform(delete("/dashboard/classifieds/deleteClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan başarıyla silindi."));
    }

    @Test
    void deleteClientAdvertNotFoundTest() throws Exception {
        int advertId = 999;
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/dashboard/classifieds/deleteClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan bulunamadı."));
    }

    @Test
    void updateClientAdvertStatusTest() throws Exception {
        int advertId = 1;
        CodecaseModel advert = new CodecaseModel(1, "Title", "Description", "Emlak", "Onay Bekliyor");
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.of(advert));
        when(codecaseRepository.save(any(CodecaseModel.class))).thenReturn(advert);

        mockMvc.perform(put("/dashboard/classifieds/updateClientAdvertStatus/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan durumu Aktif olarak değiştirildi."));
    }

    @Test
    void updateClientAdvertStatusDuplicateTest() throws Exception {
        int advertId = 1;
        CodecaseModel advert = new CodecaseModel(1, "Title", "Description", "Emlak", "Mükerrer");
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.of(advert));

        mockMvc.perform(put("/dashboard/classifieds/updateClientAdvertStatus/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Birden fazla girilmiş bir ilanı güncelleyemezsin."));
    }

    @Test
    void updateClientAdvertStatusNotPendingTest() throws Exception {
        int advertId = 1;
        CodecaseModel advert = new CodecaseModel(1, "Title", "Description", "Emlak", "Aktif");
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.of(advert));

        mockMvc.perform(put("/dashboard/classifieds/updateClientAdvertStatus/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan durumu 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı."));
    }

    @Test
    void updateClientAdvertStatusNotFoundTest() throws Exception {
        int advertId = 999;
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/dashboard/classifieds/updateClientAdvertStatus/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan bulunamadı."));
    }

    @Test
    void deactivateClientAdvertTest() throws Exception {
        int advertId = 1;
        CodecaseModel advert = new CodecaseModel(1, "Title", "Description", "Emlak", "Aktif");
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.of(advert));
        when(codecaseRepository.save(any(CodecaseModel.class))).thenReturn(advert);

        mockMvc.perform(put("/dashboard/classifieds/deactivateClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan durumu Deaktif olarak değiştirildi."));
    }

    @Test
    void deactivateClientAdvertNotActiveTest() throws Exception {
        int advertId = 1;
        CodecaseModel advert = new CodecaseModel(1, "Title", "Description", "Emlak", "Deaktif");
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.of(advert));

        mockMvc.perform(put("/dashboard/classifieds/deactivateClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan durumu 'Aktif' ya da 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı."));

    }

    @Test
    void deactivateClientAdvertNotFoundTest() throws Exception {
        int advertId = 999;
        when(codecaseRepository.findById(advertId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/dashboard/classifieds/deactivateClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan bulunamadı."));
    }

    @Test
    void getStatisticsTest() throws Exception {
        StatisticsResponse statisticsResponse = new StatisticsResponse(3, 2, 1, 1);
        when(codecaseRepository.countByStatus("Aktif")).thenReturn(3L);
        when(codecaseRepository.countByStatus("Deaktif")).thenReturn(2L);
        when(codecaseRepository.countByStatus("Onay Bekliyor")).thenReturn(1L);
        when(codecaseRepository.countByStatus("Mükerrer")).thenReturn(1L);

        mockMvc.perform(get("/dashboard/classifieds/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCount").value(3))
                .andExpect(jsonPath("$.inactiveCount").value(2))
                .andExpect(jsonPath("$.pendingApprovalCount").value(1))
                .andExpect(jsonPath("$.duplicateCount").value(1));
    }

}
