package com.labgo.backend.repository;

import com.labgo.backend.entity.Lending;
import com.labgo.backend.entity.LendingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LendingRepository extends JpaRepository<Lending, Long> {

    boolean existsByEquipmentIdAndStatus(Long equipmentId, LendingStatus status);

    void deleteAll();

    List<Lending> findByUserId(Long userId);

}