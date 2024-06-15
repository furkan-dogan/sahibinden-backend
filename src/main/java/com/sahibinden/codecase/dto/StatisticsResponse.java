package com.sahibinden.codecase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponse {
    private long activeCount;
    private long inactiveCount;
    private long pendingApprovalCount;
    private long duplicateCount;
}