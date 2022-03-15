package com.demo.recon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.recon.entity.SettlementSystemDetails;

@Repository
public interface SettlementSystemDetailsRepository extends JpaRepository<SettlementSystemDetails, Long> {

	List<SettlementSystemDetails> findByOrganizationBICAndCurrencyId(String organizationBIC, Long currencyId);

}
