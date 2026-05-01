package com.labgo.backend.controller;

import com.labgo.backend.dto.EquipmentRequestDto;
import com.labgo.backend.dto.EquipmentResponseDto;
import com.labgo.backend.entity.Equipment;
import com.labgo.backend.entity.EquipmentStatus;
import com.labgo.backend.service.EquipmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:4173", "http://127.0.0.1:4173"})
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ResponseEntity<EquipmentResponseDto> createEquipment(@Valid @RequestBody EquipmentRequestDto request) {
        Equipment equipment = buildEquipmentFromRequest(request);
        Equipment savedEquipment = equipmentService.createEquipment(equipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedEquipment));
    }

    @GetMapping
    public ResponseEntity<List<EquipmentResponseDto>> getAllEquipment(@RequestParam Long userId) {
        List<EquipmentResponseDto> equipmentList = equipmentService.getAllEquipmentByUser(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(equipmentList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponseDto> getEquipmentById(@PathVariable Long id) {
        Equipment equipment = equipmentService.findById(id);
        return ResponseEntity.ok(mapToResponse(equipment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponseDto> updateEquipment(@PathVariable Long id,
            @Valid @RequestBody EquipmentRequestDto request) {
        Equipment equipment = buildEquipmentFromRequest(request);
        Equipment updatedEquipment = equipmentService.updateEquipment(id, equipment);
        return ResponseEntity.ok(mapToResponse(updatedEquipment));
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/reset-all")
    public ResponseEntity<String> resetSystem() {
        equipmentService.resetSystem();
        return ResponseEntity.ok("System reset successful");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }

    private Equipment buildEquipmentFromRequest(EquipmentRequestDto request) {
        EquipmentStatus status;
        try {
            status = EquipmentStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Invalid equipment status: " + request.getStatus());
        }
        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();
        return Equipment.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .labLocation(request.getLabLocation())
                .quantity(quantity)
                .availableQuantity(quantity)
                .status(status)
                .serialNumber(
                    request.getSerialNumber() != null ? request.getSerialNumber() : ""
                )
                .manufacturer(request.getManufacturer())
                .purchasePrice(
                    request.getPurchasePrice() != null ? request.getPurchasePrice() : 0.0
                )
                .userId(request.getUserId())
                .build();
    }

    private EquipmentResponseDto mapToResponse(Equipment equipment) {
        return EquipmentResponseDto.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .category(equipment.getCategory())
                .description(equipment.getDescription())
                .labLocation(equipment.getLabLocation())
                .quantity(equipment.getQuantity())
                .availableQuantity(equipment.getAvailableQuantity())
                .status(equipment.getStatus())
                .serialNumber(equipment.getSerialNumber())
                .manufacturer(equipment.getManufacturer())
                .purchasePrice(equipment.getPurchasePrice())
                .build();
    }
}
