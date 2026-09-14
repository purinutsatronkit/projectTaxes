package com.pccth.taxes.repository;

import com.pccth.taxes.entity.TaxesDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxesDetailRepository extends JpaRepository<TaxesDetailEntity, Long> {

    List<TaxesDetailEntity> findByHeaderId(Long headerId);
}