package com.labgo.backend.dto;

import com.labgo.backend.entity.LendingStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingResponseDto {
    private Long id;
    private Long equipmentId;
    private String equipmentName;
    private String issuedToName;
    private String contactNumber;
    private Integer quantity;
    private String issuedToRole;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private LendingStatus status;
}
