package com.sahibinden.codecase.service;

import com.sahibinden.codecase.model.AdvertModel;
import com.sahibinden.codecase.model.StatusChange;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import com.sahibinden.codecase.util.BadWordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdvertServiceTest {

    @Mock
    private AdvertRepository advertRepository;

    @Mock
    private BadWordUtil badWordUtil;

    @Mock
    private StatusChangeRepository statusChangeRepository;

    @InjectMocks
    private AdvertService advertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateAdvert_withBadWord() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("opsiyonlu");
        when(badWordUtil.containsBadWord(anyString())).thenReturn(true);

        String result = advertService.validateAdvert(advert);

        assertEquals("İlan girişi yasaklı kelimeden dolayı engellendi.", result);
    }

    @Test
    void validateAdvert_withoutBadWord() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("Sahibinden satılık macbook");
        when(badWordUtil.containsBadWord(anyString())).thenReturn(false);

        String result = advertService.validateAdvert(advert);

        assertNull(result);
    }

    @Test
    void determineStatusByCategory_approvalPending() {
        String result1 = advertService.determineStatusByCategory("Emlak");
        String result2 = advertService.determineStatusByCategory("Vasıta");
        String result3 = advertService.determineStatusByCategory("Diğer");

        assertEquals("Onay Bekliyor", result1);
        assertEquals("Onay Bekliyor", result2);
        assertEquals("Onay Bekliyor", result3);
    }

    @Test
    void determineStatusByCategory_active() {
        String result = advertService.determineStatusByCategory("Elektronik");

        assertEquals("Aktif", result);
    }

    @Test
    void updateStatusToActive_whenPending() {
        AdvertModel advert = new AdvertModel();
        advert.setStatus("Onay Bekliyor");

        String result = advertService.updateStatusToActive(advert);

        assertEquals("İlan durumu Aktif olarak değiştirildi.", result);
        assertEquals("Aktif", advert.getStatus());
        verify(advertRepository).save(advert);
    }

    @Test
    void updateStatusToActive_whenDuplicate() {
        AdvertModel advert = new AdvertModel();
        advert.setStatus("Mükerrer");

        String result = advertService.updateStatusToActive(advert);

        assertEquals("Birden fazla girilmiş bir ilanı güncelleyemezsin.", result);
        verify(advertRepository, never()).save(advert);
    }

    @Test
    void updateStatusToActive_whenNotPending() {
        AdvertModel advert = new AdvertModel();
        advert.setStatus("Aktif");

        String result = advertService.updateStatusToActive(advert);

        assertEquals("İlan durumu 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.", result);
        verify(advertRepository, never()).save(advert);
    }

    @Test
    void deactivateAdvert_whenActive() {
        AdvertModel advert = new AdvertModel();
        advert.setStatus("Aktif");

        String result = advertService.deactivateAdvert(advert);

        assertEquals("İlan durumu Deaktif olarak değiştirildi.", result);
        assertEquals("Deaktif", advert.getStatus());
        verify(advertRepository).save(advert);
    }

    @Test
    void deactivateAdvert_whenPending() {
        AdvertModel advert = new AdvertModel();
        advert.setStatus("Onay Bekliyor");

        String result = advertService.deactivateAdvert(advert);

        assertEquals("İlan durumu Deaktif olarak değiştirildi.", result);
        assertEquals("Deaktif", advert.getStatus());
        verify(advertRepository).save(advert);
    }

    @Test
    void deactivateAdvert_whenNotActiveOrPending() {
        AdvertModel advert = new AdvertModel();
        advert.setStatus("Deaktif");

        String result = advertService.deactivateAdvert(advert);

        assertEquals("İlan durumu 'Aktif' ya da 'Onay Bekliyor' durumunda değil, güncelleme yapılmadı.", result);
        verify(advertRepository, never()).save(advert);
    }

    @Test
    void saveStatusChange_whenStatusChanged() {
        int advertId = 1;
        String newStatus = "Aktif";
        StatusChange lastStatusChange = new StatusChange();
        lastStatusChange.setStatus("Onay Bekliyor");
        when(statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(advertId))
                .thenReturn(Collections.singletonList(lastStatusChange));

        advertService.saveStatusChange(advertId, newStatus);

        ArgumentCaptor<StatusChange> captor = ArgumentCaptor.forClass(StatusChange.class);
        verify(statusChangeRepository).save(captor.capture());
        StatusChange savedStatusChange = captor.getValue();
        assertEquals(advertId, savedStatusChange.getAdvertId());
        assertEquals(newStatus, savedStatusChange.getStatus());
    }

    @Test
    void saveStatusChange_whenStatusNotChanged() {
        int advertId = 1;
        String newStatus = "Aktif";
        StatusChange lastStatusChange = new StatusChange();
        lastStatusChange.setStatus(newStatus);
        when(statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(advertId))
                .thenReturn(Collections.singletonList(lastStatusChange));

        advertService.saveStatusChange(advertId, newStatus);

        verify(statusChangeRepository, never()).save(any(StatusChange.class));
    }

    @Test
    void saveStatusChange_whenNoPreviousStatusChange() {
        int advertId = 1;
        String newStatus = "Aktif";
        when(statusChangeRepository.findByAdvertIdOrderByChangeTimeAsc(advertId))
                .thenReturn(Collections.emptyList());

        advertService.saveStatusChange(advertId, newStatus);

        ArgumentCaptor<StatusChange> captor = ArgumentCaptor.forClass(StatusChange.class);
        verify(statusChangeRepository).save(captor.capture());
        StatusChange savedStatusChange = captor.getValue();
        assertEquals(advertId, savedStatusChange.getAdvertId());
        assertEquals(newStatus, savedStatusChange.getStatus());
    }
}
