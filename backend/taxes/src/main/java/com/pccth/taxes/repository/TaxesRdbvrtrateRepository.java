package com.pccth.taxes.repository;

import com.pccth.taxes.entity.TaxesRdbvrtrateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface TaxesRdbvrtrateRepository extends JpaRepository<TaxesRdbvrtrateEntity, Long> {

    @Query(value = "SELECT * FROM rdbvrtrate r WHERE :amount BETWEEN r.SALEFROM AND r.SALETO LIMIT 1", nativeQuery = true)
    Optional<TaxesRdbvrtrateEntity> findRateByPurchaseAmount(@Param("amount") BigDecimal amount);
}