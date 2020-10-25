package application.loanapplicationsystem.services;

import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient.prepareClient;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient;
import application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant;
import application.loanapplicationsystem.util.Tag;

public abstract class AbstractServiceTest {

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
	 * Singleton client for test.
	 */
	public static List<String> productUrisToBeDeleted;

	/**
	 * Singleton ES client for test.
	 */
	public static ElasticSearchClient searchClient;

	public static TransportClient client;

	public HttpServletRequest mockRequest;
	private HttpServletResponse mockResponse;
	private StringWriter actualResponse;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// check if elasticsearch host is "localhost".
		if (!getESClient().checkElasticsearchHost()) {
			fail("application.conf loanapplicationsystem.elasticsearch.hosts parameter value must be \"localhost\".");
		}
		// init es..
		client = prepareClient();
		getESClient().createIndexesIfNotExists(ElasticSearchConstant.INDEX_LOAN);
		// init product uris to be deleted..
		productUrisToBeDeleted = new ArrayList<String>();
	}

	@Before
	public void before() throws IOException {
		mockRequest = mock(HttpServletRequest.class);
		mockResponse = mock(HttpServletResponse.class);
		// Mock response writer behavior.
		actualResponse = new StringWriter();
		mockResponseWriter(mockResponse, actualResponse);
		createTestData();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		// delete es..
		if (productUrisToBeDeleted != null) {
			for (String productUri : productUrisToBeDeleted) {
				getESClient().deleteProduct(productUri);
			}
		}
	}

	public static ElasticSearchClient getESClient() {
		if (searchClient == null) {
			searchClient = ElasticSearchClient.getInstance();
		}
		return searchClient;
	}

	public abstract void createTestData();

	public static void addDataForTest() throws Exception {
		List<String> dataList = new ArrayList<String>();
		dataList.add(TEST_DATA_1);
		dataList.add(TEST_DATA_2);
		dataList.add(TEST_DATA_3);
		dataList.add(TEST_DATA_4);
		dataList.add(TEST_DATA_5);
		dataList.add(TEST_DATA_6);
		addProducts(dataList);
	}

	private static void addProducts(List<String> dataList) {
		// add sample data to es..
		for (String data : dataList) {
			JsonObject dataJson = JsonParser.parseString(data).getAsJsonObject();
			String identifier = dataJson.get(Tag.IDENTIFICATION_NO).getAsString();
			productUrisToBeDeleted.add(identifier);
			getESClient().addProduct(ElasticSearchConstant.INDEX_LOAN, ElasticSearchConstant.TYPE_ENTITY, dataJson);
		}
		// apply changes immediately..
		client.admin().indices().refresh(new RefreshRequest()).actionGet();
	}

	protected void mockResponseWriter(HttpServletResponse mockResponse, StringWriter strWriter) throws IOException {
		PrintWriter printWriter = new PrintWriter(strWriter);
		doReturn(printWriter).when(mockResponse).getWriter();
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
