package com.demo.recon.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.demo.recon.model.ReconStmtRequest;
import com.demo.recon.utility.JsonConverter;

import lombok.Data;

@Entity
@Data
@Table(name = "TB_INPUT_AUDIT_MSG")
public class InputAuditMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long audId;

	private String uid;

	@Convert(converter = JsonConverter.class)
	@Column(columnDefinition = "jsonb")
	private ReconStmtRequest jsonContent;

}
