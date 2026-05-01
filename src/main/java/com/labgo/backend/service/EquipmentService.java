package com.labgo.backend.service;

import com.labgo.backend.entity.Equipment;
import com.labgo.backend.entity.EquipmentStatus;
import com.labgo.backend.exception.EquipmentNotFoundException;
import com.labgo.backend.exception.InvalidRequestException;
import com.labgo.backend.exception.OutOfStockException;
import com.labgo.backend.repository.EquipmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.labgo.backend.entity.LendingStatus;
import com.labgo.backend.repository.LendingRepository;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final LendingRepository lendingRepository;
    public EquipmentService(EquipmentRepository equipmentRepository,
                            LendingRepository lendingRepository) {
        this.equipmentRepository = equipmentRepository;
        this.lendingRepository = lendingRepository;
    }
    @Transactional
    public Equipment createEquipment(Equipment equipment) {
        if (equipmentRepository.existsBySerialNumber(equipment.getSerialNumber())) {
            throw new InvalidRequestException("Serial number already exists: " + equipment.getSerialNumber());
        }
        if (equipment.getQuantity() <= 0) {
            equipment.setQuantity(1);
        }
        equipment.setAvailableQuantity(equipment.getQuantity());
        if (equipment.getStatus() == null) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
        }
        return equipmentRepository.save(equipment);
    }

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipmentByUser(Long userId) {
        return equipmentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Equipment findById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException("Equipment not found with id: " + id));
    }

    @Transactional
    public Equipment updateEquipment(Long id, Equipment equipment) {
        Equipment existing = findById(id);
        if (!existing.getSerialNumber().equals(equipment.getSerialNumber())
                && equipmentRepository.existsBySerialNumber(equipment.getSerialNumber())) {
            throw new InvalidRequestException("Serial number already exists: " + equipment.getSerialNumber());
        }
        existing.setName(equipment.getName());
        existing.setCategory(equipment.getCategory());
        existing.setDescription(equipment.getDescription());
        existing.setLabLocation(equipment.getLabLocation());
        existing.setSerialNumber(equipment.getSerialNumber());
        existing.setManufacturer(equipment.getManufacturer());
        existing.setPurchasePrice(equipment.getPurchasePrice());
        existing.setStatus(equipment.getStatus() == null ? EquipmentStatus.AVAILABLE : equipment.getStatus());

        int newQuantity = Math.max(equipment.getQuantity(), 1);
        int difference = newQuantity - existing.getQuantity();
        existing.setQuantity(newQuantity);
        if (difference > 0) {
            existing.setAvailableQuantity(existing.getAvailableQuantity() + difference);
        } else if (existing.getAvailableQuantity() > newQuantity) {
            existing.setAvailableQuantity(newQuantity);
        }
        if (existing.getStatus() != EquipmentStatus.RETIRED
                && existing.getStatus() != EquipmentStatus.UNDER_MAINTENANCE
                && existing.getAvailableQuantity() > 0) {
            existing.setStatus(EquipmentStatus.AVAILABLE);
        }
        if (equipment.getUserId() != null) {
            existing.setUserId(equipment.getUserId());
        }
        return equipmentRepository.save(existing);
    }

    @Transactional
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException("Equipment not found"));

        boolean isIssued = lendingRepository.existsByEquipmentIdAndStatus(
                id,
                LendingStatus.ISSUED
        );

        if (isIssued) {
            throw new RuntimeException("Cannot delete equipment while it is issued");
        }

        equipment.setStatus(EquipmentStatus.RETIRED);

        equipmentRepository.save(equipment);
    }

    @Transactional
    public void decreaseAvailableQuantity(Equipment equipment) {
        if (equipment.getStatus() == EquipmentStatus.RETIRED
                || equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE) {
            throw new InvalidRequestException("Equipment is not available for lending: " + equipment.getName());
        }
        if (equipment.getAvailableQuantity() <= 0) {
            throw new OutOfStockException("Equipment out of stock: " + equipment.getName());
        }
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - 1);
        if (equipment.getAvailableQuantity() == 0) {
            equipment.setStatus(EquipmentStatus.IN_USE);
        }
        equipmentRepository.save(equipment);
    }

    @Transactional
    public void increaseAvailableQuantity(Equipment equipment) {
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + 1);
        if (equipment.getStatus() != EquipmentStatus.RETIRED
                && equipment.getStatus() != EquipmentStatus.UNDER_MAINTENANCE) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
        }
        equipmentRepository.save(equipment);
    }

    @Transactional
    public void resetSystem() {
        System.out.println("RESET API CALLED");
        try {
            lendingRepository.deleteAll();
            equipmentRepository.deleteAll();
        } catch (Exception e) {
            throw new RuntimeException("Reset failed: " + e.getMessage(), e);
        }
    }
}
