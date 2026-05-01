package com.labgo.backend.service;

import com.labgo.backend.entity.Equipment;
import com.labgo.backend.entity.Lending;
import com.labgo.backend.entity.LendingStatus;
import com.labgo.backend.exception.InvalidRequestException;
import com.labgo.backend.exception.LendingNotFoundException;
import com.labgo.backend.exception.OutOfStockException;
import com.labgo.backend.repository.LendingRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LendingService {

    private static final Logger log = LoggerFactory.getLogger(LendingService.class);

    private final LendingRepository lendingRepository;
    private final EquipmentService equipmentService;

    public LendingService(LendingRepository lendingRepository, EquipmentService equipmentService) {
        this.lendingRepository = lendingRepository;
        this.equipmentService = equipmentService;
    }

    @Transactional
    public Lending issueLending(Lending lending, int quantity) {
        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be at least 1");
        }
        Equipment equipment = equipmentService.findById(lending.getEquipment().getId());
        int availableQuantity = equipment.getAvailableQuantity();
        if (availableQuantity <= 0) {
            throw new OutOfStockException("Equipment out of stock: " + equipment.getName());
        }
        if (quantity > availableQuantity) {
            throw new OutOfStockException("Requested quantity " + quantity + " exceeds available quantity " + availableQuantity + " for equipment: " + equipment.getName());
        }
        try {
            for (int i = 0; i < quantity; i++) {
                equipmentService.decreaseAvailableQuantity(equipment);
            }
            lending.setEquipment(equipment);
            lending.setQuantity(quantity);
            lending.setStatus(LendingStatus.ISSUED);
            return lendingRepository.save(lending);
        } catch (RuntimeException exception) {
            log.error("Error issuing lending for equipment id {}: {}", equipment.getId(), exception.getMessage(), exception);
            throw exception;
        }
    }

    @Transactional
    public Lending returnLending(Long lendingId) {
        Lending lending = lendingRepository.findById(lendingId)
                .orElseThrow(() -> new LendingNotFoundException("Lending record not found with id: " + lendingId));
        if (LendingStatus.RETURNED.equals(lending.getStatus())) {
            return lending;
        }
        lending.setStatus(LendingStatus.RETURNED);
        int quantityToRestore = lending.getQuantity() != null ? lending.getQuantity() : 1;
        for (int i = 0; i < quantityToRestore; i++) {
            equipmentService.increaseAvailableQuantity(lending.getEquipment());
        }
        return lendingRepository.save(lending);
    }

    @Transactional(readOnly = true)
    public List<Lending> getAllLendings() {
        return lendingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Lending> getAllLendingsByUser(Long userId) {
        return lendingRepository.findByUserId(userId);
    }
}
