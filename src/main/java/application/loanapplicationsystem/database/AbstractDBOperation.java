package application.loanapplicationsystem.database;

import java.net.UnknownHostException;
import java.util.List;

import com.google.gson.JsonObject;

import application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient;

public abstract class AbstractDBOperation {

	/**
	 * Gets credit score and limit of the given identification no.
	 * 
	 * @param identification number.
	 * @return document as string.
	 */
	public abstract List<JsonObject> getUserInformationToDB(String identificationNo);

	/**
	 * Gets credit result and limit of the given identification no.
	 * 
	 * @param identification number.
	 * @param includeField.
	 * @return document as string.
	 */
	public abstract List<JsonObject> getCreditResultAndLimit(String identificationNo, String[] includeField);

	/**
	 * Write credit result and limit informations to {@link ElasticSearchClient}
	 * 
	 * @param creditResultJsonObject contains credit result and credit limit
	 * @throws UnknownHostException
	 */
	public abstract void writeCreditResultToDB(JsonObject creditResultJsonObject) throws UnknownHostException;

}
