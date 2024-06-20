package com.sahibinden.codecase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahibinden.codecase.dto.StatisticsResponse;
import com.sahibinden.codecase.model.AdvertModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.util.BadWordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdvertController.class)
@AutoConfigureMockMvc
public class AdvertIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertRepository advertRepository;

    @MockBean
    private BadWordUtil badWordUtil;

    @MockBean
    private StatusChangeRepository statusChangeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private List<AdvertModel> advertModels;
    private List<StatusChange> statusChanges;

    @BeforeEach
    void setUp() {
        statusChanges = Arrays.asList(
                new StatusChange(1, 1, "Aktif", LocalDateTime.now()),
                new StatusChange(2, 1, "Deaktif", LocalDateTime.now().plusHours(1))
        );

        advertModels = Arrays.asList(
                new AdvertModel(1, "Title1", "Description1", "Emlak", "Aktif"),
                new AdvertModel(2, "Title2", "Description2", "Vasıta", "Deaktif"),
                new AdvertModel(3, "Title3", "Description3", "Diğer", "Mükerrer")
        );
    }

    @Test
    void testSaveAndGetAllClientAdverts() throws Exception {
        AdvertModel newAdvert = new AdvertModel();
        newAdvert.setTitle("ValidTitle");
        newAdvert.setDescription("Valid description with sufficient length");
        newAdvert.setCategory("Emlak");

        when(badWordUtil.containsBadWord(any())).thenReturn(false);
        when(advertRepository.save(any(AdvertModel.class))).thenReturn(newAdvert);

        mockMvc.perform(post("/dashboard/classifieds/saveClientAdvert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdvert)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan kaydedildi !"));

        when(advertRepository.findAll()).thenReturn(advertModels);

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
    void testGetStatistics() throws Exception {
        StatisticsResponse statisticsResponse = new StatisticsResponse(3, 2, 1, 1);
        when(advertRepository.countByStatus("Aktif")).thenReturn(3L);
        when(advertRepository.countByStatus("Deaktif")).thenReturn(2L);
        when(advertRepository.countByStatus("Onay Bekliyor")).thenReturn(1L);
        when(advertRepository.countByStatus("Mükerrer")).thenReturn(1L);

        mockMvc.perform(get("/dashboard/classifieds/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCount").value(3))
                .andExpect(jsonPath("$.inactiveCount").value(2))
                .andExpect(jsonPath("$.pendingApprovalCount").value(1))
                .andExpect(jsonPath("$.duplicateCount").value(1));
    }

    @Test
    void testGetStatusChanges() throws Exception {
        int advertId = 1;

        when(statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(advertId)).thenReturn(statusChanges);

        mockMvc.perform(get("/dashboard/classifieds/getStatusChanges/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].advertId").value(1))
                .andExpect(jsonPath("$[0].status").value("Aktif"))
                .andExpect(jsonPath("$[1].advertId").value(1))
                .andExpect(jsonPath("$[1].status").value("Deaktif"));
    }

    @Test
    void testDeleteClientAdvert() throws Exception {
        int advertId = 1;
        when(advertRepository.findById(advertId)).thenReturn(Optional.of(new AdvertModel()));
        doNothing().when(advertRepository).deleteById(advertId);

        mockMvc.perform(delete("/dashboard/classifieds/deleteClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan başarıyla silindi."));
    }

    @Test
    void testUpdateClientAdvertStatus() throws Exception {
        int advertId = 1;

        Optional<AdvertModel> codecaseModelOptional = Optional.of(new AdvertModel(advertId, "Title1", "Description1", "Emlak", "Onay Bekliyor"));
        when(advertRepository.findById(advertId)).thenReturn(codecaseModelOptional);

        mockMvc.perform(put("/dashboard/classifieds/updateClientAdvertStatus/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan durumu Aktif olarak değiştirildi."));
    }

    @Test
    void testDeactivateClientAdvert() throws Exception {
        int advertId = 1;

        Optional<AdvertModel> codecaseModelOptional = Optional.of(new AdvertModel(advertId, "Title1", "Description1", "Emlak", "Aktif"));
        when(advertRepository.findById(advertId)).thenReturn(codecaseModelOptional);

        mockMvc.perform(put("/dashboard/classifieds/deactivateClientAdvert/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("İlan durumu Deaktif olarak değiştirildi."));
    }
}
