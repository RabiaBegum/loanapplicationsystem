package application.loanapplicationsystem;

import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient.prepareClient;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.INDEX_LOAN;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.TYPE_ENTITY;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient;
import application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant;

public abstract class AbstractService {

	public static final int TIMEOUT = 5000;

	// < 500
	private static final String TEST_DATA_1 = "{\"identificationNo\":\"1\",\"name\":\"rabia\",\"surname\":\"hatapoglu\",\"phoneNo\":\"05555555555\",\"monthlyIncome\":60000,\"creditScore\":\"450\"}";

	// >500 && <1000 , monthlyIncome < 50000
	private static final String TEST_DATA_2 = "{\"identificationNo\":\"2\",\"name\":\"rabia\",\"surname\":\"hatapoglu\",\"phoneNo\":\"05555555555\",\"monthlyIncome\":3000,\"creditScore\":\"700\"}";

	// >500 && <1000, monthlyIncome > 50000
	private static final String TEST_DATA_3 = "{\"identificationNo\":\"3\",\"name\":\"rabia\",\"surname\":\"hatapoglu\",\"phoneNo\":\"05555555555\",\"monthlyIncome\":60000,\"creditScore\":\"700\"}";

	// >=1000
	private static final String TEST_DATA_4 = "{\"identificationNo\":\"4\",\"name\":\"rabia\",\"surname\":\"hatapoglu\",\"phoneNo\":\"05555555555\",\"monthlyIncome\":60000,\"creditScore\":\"1000\"}";

	// >1000
	private static final String TEST_DATA_5 = "{\"identificationNo\":\"5\",\"name\":\"rabia\",\"surname\":\"hatapoglu\",\"phoneNo\":\"05555555555\",\"monthlyIncome\":60000,\"creditScore\":\"1500\"}";

	// >= 500
	private static final String TEST_DATA_6 = "{\"identificationNo\":\"6\",\"name\":\"rabia\",\"surname\":\"hatapoglu\",\"phoneNo\":\"05555555555\",\"monthlyIncome\":60000,\"creditScore\":\"500\"}";

	/**
	 * Singleton ES client for test.
	 */
	public static ElasticSearchClient searchClient;

	public static TransportClient client;

	public static ElasticSearchClient getESClient() {
		if (searchClient == null) {
			searchClient = ElasticSearchClient.getInstance();
		}
		return searchClient;
	}

	public abstract void createTestData();

	public static void addDataForTest() throws Exception {
		// init es..
		client = prepareClient();
		List<String> dataList = new ArrayList<String>();
		dataList.add(TEST_DATA_1);
		dataList.add(TEST_DATA_2);
		dataList.add(TEST_DATA_3);
		dataList.add(TEST_DATA_4);
		dataList.add(TEST_DATA_5);
		dataList.add(TEST_DATA_6);
		addProducts(dataList);
		waitResults(TIMEOUT, 20, INDEX_LOAN, TYPE_ENTITY);

	}

	private static void addProducts(List<String> dataList) {
		// add sample data to es..
		for (String data : dataList) {
			JsonObject dataJson = JsonParser.parseString(data).getAsJsonObject();
			getESClient().addProduct(ElasticSearchConstant.INDEX_LOAN, ElasticSearchConstant.TYPE_ENTITY, dataJson);
		}
		// apply changes immediately..
		client.admin().indices().refresh(new RefreshRequest()).actionGet();
	}

	public static SearchHit[] waitResults(int timeout, int count, String indexName, String typeName) throws Exception {
		SearchHit[] results = new SearchHit[0];
		if (client == null) {
			client = prepareClient();
		}
		while (results.length != count && timeout > 0) {
			SearchResponse searchResponse = client.prepareSearch(indexName).setTypes(typeName)
					.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
			results = searchResponse.getHits().getHits();
			Thread.sleep(1000);
			timeout -= 1000;
		}
		return results;
	}
}
