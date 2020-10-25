package application.loanapplicationsystem.database.elasticsearch;

import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.INDEX_LOAN;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.KEEP_ALIVE_ONE_HOUR;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.TYPE_ENTITY;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.gson.JsonObject;

import application.loanapplicationsystem.database.AbstractDBOperation;
import application.loanapplicationsystem.database.result.ElasticSearchResult;
import application.loanapplicationsystem.util.Tag;

public class ElasticSearchOperation extends AbstractDBOperation {

	private static final int BULK_SIZE = 100;

	/**
	 * Logger instance.
	 */
	private static Logger logger = Logger.getLogger(ElasticSearchOperation.class);

	int counter = 0;

	@Override
	public List<JsonObject> getUserInformationToDB(String identificationNo) {
		ElasticSearchClient getSearchClient = ElasticSearchClient.getInstance();
		SearchParameter creaInformationParameter = new CreditInformationParameter(identificationNo);
		ElasticSearchResult elasticSearchResult = getSearchClient.search(null, KEEP_ALIVE_ONE_HOUR, "1000", INDEX_LOAN,
				TYPE_ENTITY, creaInformationParameter);
		List<JsonObject> searchResults = elasticSearchResult.getSearchResults();
		return searchResults;
	}

	@Override
	public List<JsonObject> getCreditResultAndLimit(String identificationNo, String[] includeField) {
		ElasticSearchClient getSearchClient = ElasticSearchClient.getInstance();
		SearchParameter creaInformationParameter = new CreditInformationParameter(identificationNo);
		ElasticSearchResult elasticSearchResult = getSearchClient.search(null, KEEP_ALIVE_ONE_HOUR, "1000", INDEX_LOAN,
				TYPE_ENTITY, includeField, creaInformationParameter);
		List<JsonObject> searchResults = elasticSearchResult.getSearchResults();
		return searchResults;
	}

	@Override
	public void writeCreditResultToDB(JsonObject creditResultJsonObject) throws UnknownHostException {
		if (creditResultJsonObject != null && !creditResultJsonObject.isJsonNull()) {
			TransportClient transportClient = ElasticSearchClient.prepareClient();
			BulkRequestBuilder bulk = transportClient.prepareBulk();
			execute(transportClient, bulk, INDEX_LOAN, TYPE_ENTITY, creditResultJsonObject);
		}
	}

	public void execute(TransportClient transportClient, BulkRequestBuilder bulk, String index, String type,
			JsonObject creditResultJsonObject) {
		try {
			updatePropertiesWithBulk(transportClient, bulk, index, type, creditResultJsonObject);
			counter++;
			if (counter % BULK_SIZE == 1 && counter > 1) {
				sendBulk(transportClient, bulk);
			}
		} catch (Exception e) {
			System.out.println(e);
			logger.error("could not load", e);
		}
		sendBulk(transportClient, bulk);
	}

	public void sendBulk(TransportClient transportClient, BulkRequestBuilder bulk) {
		if (bulk.numberOfActions() > 0) {
			System.out.println("SENDED BULK:" + bulk.numberOfActions());
			bulk.get();
			bulk = transportClient.prepareBulk();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}

	public void updatePropertiesWithBulk(TransportClient transportClient, BulkRequestBuilder bulk, String index,
			String type, JsonObject jsonObject) throws Exception {
		try {
			XContentBuilder content = XContentFactory.jsonBuilder().startObject();
			String identificationNo = jsonObject.get(Tag.IDENTIFICATION_NO).getAsString();
			content.field(Tag.IDENTIFICATION_NO, identificationNo);
			if (jsonObject.has(Tag.CREDIT_LIMIT)) {
				double creditLimit = jsonObject.get(Tag.CREDIT_LIMIT).getAsDouble();
				content.field(Tag.CREDIT_LIMIT, creditLimit);
			}
			if (jsonObject.has(Tag.CREDIT_RESULT)) {
				Boolean creditResult = jsonObject.get(Tag.CREDIT_RESULT).getAsBoolean();
				content.field(Tag.CREDIT_RESULT, creditResult);
			}
			content.endObject();
			System.err.println(String.valueOf(content));
			UpdateRequestBuilder updateReq = transportClient.prepareUpdate(index, type, identificationNo)
					.setDoc(content);
			bulk.add(updateReq);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
