package com.labgo.backend.dto;

import com.labgo.backend.entity.EquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentResponseDto {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String labLocation;
    private int quantity;
    private int availableQuantity;
    private EquipmentStatus status;
    private String serialNumber;
    private String manufacturer;
    private Double purchasePrice;
}
