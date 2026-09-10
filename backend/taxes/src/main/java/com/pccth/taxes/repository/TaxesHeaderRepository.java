package com.pccth.taxes.repository;

import com.pccth.taxes.entity.TaxesHeaderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TaxesHeaderRepository extends JpaRepository<TaxesHeaderEntity, Long> {

    // เพิ่ม Annotation นี้เพื่อแก้ปัญหา N+1 Query (ดึง Details มาทีเดียว)
    @EntityGraph(attributePaths = {"details"})
    Optional<TaxesHeaderEntity> findBySummaryNo(String summaryNo);

    boolean existsBySummaryNo(String summaryNo);
}