package com.demo.recon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.recon.entity.InputAuditMessage;

@Repository
public interface InputAuditMessageRepository extends JpaRepository<InputAuditMessage, Long> {

}
