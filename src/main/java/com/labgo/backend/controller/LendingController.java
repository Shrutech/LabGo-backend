package com.labgo.backend.controller;

import com.labgo.backend.dto.LendingRequestDto;
import com.labgo.backend.dto.LendingResponseDto;
import com.labgo.backend.entity.Equipment;
import com.labgo.backend.entity.Lending;
import com.labgo.backend.service.EquipmentService;
import com.labgo.backend.service.LendingService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:4173",
    "https://your-app-name.netlify.app"
})
@RequestMapping("/lending")
public class LendingController {

    private final LendingService lendingService;
    private final EquipmentService equipmentService;

    public LendingController(LendingService lendingService, EquipmentService equipmentService) {
        this.lendingService = lendingService;
        this.equipmentService = equipmentService;
    }

    @PostMapping("/issue")
    public ResponseEntity<LendingResponseDto> issueLending(@Valid @RequestBody LendingRequestDto request) {
        Equipment equipment = equipmentService.findById(request.getEquipmentId());
        Lending lending = Lending.builder()
                .equipment(equipment)
                .issuedToName(request.getIssuedTo())
                .contactNumber(request.getContactNumber())
                .issuedToRole(request.getIssuedToRole())
                .issueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now())
                .returnDate(request.getReturnDate())
                .userId(request.getUserId())
                .build();
        Lending savedLending = lendingService.issueLending(lending, request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedLending));
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<LendingResponseDto> returnLending(@PathVariable Long id) {
        Lending returnedLending = lendingService.returnLending(id);
        return ResponseEntity.ok(mapToResponse(returnedLending));
    }

    @GetMapping
    public ResponseEntity<List<LendingResponseDto>> getAllLendings(@RequestParam Long userId) {
        List<LendingResponseDto> lendings = lendingService.getAllLendingsByUser(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lendings);
    }

    private LendingResponseDto mapToResponse(Lending lending) {
        return LendingResponseDto.builder()
                .id(lending.getId())
                .equipmentId(lending.getEquipment().getId())
                .equipmentName(lending.getEquipment().getName())
                .issuedToName(lending.getIssuedToName())
                .contactNumber(lending.getContactNumber())
                .quantity(lending.getQuantity())
                .issuedToRole(lending.getIssuedToRole())
                .issueDate(lending.getIssueDate())
                .returnDate(lending.getReturnDate())
                .status(lending.getStatus())
                .build();
    }
}
