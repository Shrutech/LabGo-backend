package com.labgo.backend.repository;

import com.labgo.backend.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    boolean existsBySerialNumber(String serialNumber);

    void deleteAll();

    List<Equipment> findByUserId(Long userId);
}
