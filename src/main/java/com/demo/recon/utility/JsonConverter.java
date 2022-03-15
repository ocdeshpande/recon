package com.demo.recon.utility;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.demo.recon.model.ReconStmtRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to convert JSON Obj to Java for ReconStmtRequest
 * 
 * @author deshpande_o
 *
 */
@Converter(autoApply = true)
public class JsonConverter implements AttributeConverter<ReconStmtRequest, String> {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(ReconStmtRequest mjo) {
		String jsonString = "";
		try {
			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mjo);
			//jsonString = mapper.convertValue(mjo, String.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}

	@Override
	public ReconStmtRequest convertToEntityAttribute(String dbData) {
		ReconStmtRequest reconStmtRequest = null;
		try {
			reconStmtRequest = mapper.readValue(dbData, ReconStmtRequest.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return reconStmtRequest;
	}
}