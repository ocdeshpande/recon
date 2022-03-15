package com.demo.recon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.recon.entity.CurrencyClauseMapping;

@Repository
public interface CurrencyClauseMappingRepository extends JpaRepository<CurrencyClauseMapping, Long> {

}
