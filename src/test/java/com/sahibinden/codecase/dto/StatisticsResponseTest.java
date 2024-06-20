package com.sahibinden.codecase.dto;

import com.sahibinden.codecase.controller.AdvertController;
import com.sahibinden.codecase.repository.AdvertRepository;
import com.sahibinden.codecase.repository.StatusChangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StatisticsResponseTest {

    @Mock
    private AdvertRepository advertRepository;

    @Mock
    private StatusChangeRepository statusChangeRepository;

    @InjectMocks
    private AdvertController advertController;

    @BeforeEach
    public void init() throws Exception {
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
        }
    }

    @Test
    public void testGetStatistics() {
        long activeCount = 5;
        long inactiveCount = 3;
        long pendingApprovalCount = 2;
        long duplicateCount = 1;

        when(advertRepository.countByStatus(anyString())).thenReturn(0L);
        when(advertRepository.countByStatus("Aktif")).thenReturn(activeCount);
        when(advertRepository.countByStatus("Deaktif")).thenReturn(inactiveCount);
        when(advertRepository.countByStatus("Onay Bekliyor")).thenReturn(pendingApprovalCount);
        when(advertRepository.countByStatus("Mükerrer")).thenReturn(duplicateCount);

        StatisticsResponse statistics = advertController.getStatistics();

        assertEquals(activeCount, statistics.getActiveCount());
        assertEquals(inactiveCount, statistics.getInactiveCount());
        assertEquals(pendingApprovalCount, statistics.getPendingApprovalCount());
        assertEquals(duplicateCount, statistics.getDuplicateCount());

        verify(advertRepository, times(1)).countByStatus("Aktif");
        verify(advertRepository, times(1)).countByStatus("Deaktif");
        verify(advertRepository, times(1)).countByStatus("Onay Bekliyor");
        verify(advertRepository, times(1)).countByStatus("Mükerrer");

        verifyNoMoreInteractions(advertRepository);
    }
}
