package com.demo.recon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.recon.entity.ReconAuditDetails;

@Repository
public interface ReconAuditDetailsRepository extends JpaRepository<ReconAuditDetails, Long> {

	@Query
	List<ReconAuditDetails> findByTransactionRefNumber(Long txnRefNum);

}
