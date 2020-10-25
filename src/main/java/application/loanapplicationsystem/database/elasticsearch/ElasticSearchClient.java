package application.loanapplicationsystem.database.elasticsearch;

import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.BOOLEAN;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.COMMA;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.DOUBLE;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.INDEX_LOAN;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.KEYWORD;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.PROPERTIES;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.TEXT;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.TYPE;
import static application.loanapplicationsystem.database.elasticsearch.ElasticSearchConstant.TYPE_ENTITY;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchContextMissingException;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.loanapplicationsystem.database.result.ElasticSearchResult;
import application.loanapplicationsystem.database.result.ResourceResultStrategy;
import application.loanapplicationsystem.database.result.ResultStrategy;
import application.loanapplicationsystem.util.ApplicationConfig;
import application.loanapplicationsystem.util.Tag;

public class ElasticSearchClient {

	public static final String MISSINGCONTEXT = "CONTEXT_MISSING";

	private static final String SCROLL_SIZE_25 = "25";

	/**
	 * Singleton client instance.
	 */
	private static ElasticSearchClient instance = null;

	/**
	 * Client of the elastic search.
	 */
	private Client client;

	/**
	 * Logger instance.
	 */
	private static Logger logger = Logger.getLogger(ElasticSearchClient.class);

	/**
	 * Configuration.
	 */
	private ElasticSearchConfig elasticSearchConfig;

	/**
	 * Default constructor for the {@link ElasticSearchClient}. This constructor
	 * also creates indexes and mappings if
	 * {@link ElasticSearchConstants#INDEX_LOAN} has not been created before.
	 */
	private ElasticSearchClient() {
		this(new ElasticSearchConfig());
	}

	/**
	 * Constructor with {@link ElasticSearchConfig}.
	 * 
	 * @param config configuration.
	 */
	private ElasticSearchClient(ElasticSearchConfig config) {
		try {
			this.elasticSearchConfig = config;
			client = createClient();
		} catch (UnknownHostException e) {
			logErrorESInitialized(e);
		}
	}

	public Client getNativeClient() {
		if (client == null) {
			try {
				client = createClient();
			} catch (UnknownHostException e) {
				logErrorClientCreated(e);
			}
		}
		return client;
	}

	/**
	 * Returns the singleton {@link ElasticSearchClient} instance.
	 * 
	 * @return {@link #instance}.
	 */
	public static ElasticSearchClient getInstance() {
		if (instance == null) {
			instance = new ElasticSearchClient();
		}
		return instance;
	}

	/**
	 * Creates a {@link TransportClient}.
	 *
	 * @return created transport client.
	 * @throws UnknownHostException
	 */
	private TransportClient createClient() throws UnknownHostException {
		int port = elasticSearchConfig.getPort();
		String[] hosts = elasticSearchConfig.getHosts().split(COMMA);
		return prepareClient(port, hosts);
	}

	public static TransportClient prepareClient() throws UnknownHostException {
		ApplicationConfig config = ApplicationConfig.getInstance();
		int port = config.getElasticSearchPort();
		String[] hosts = config.getElasticSearchHosts().split(COMMA);
		return prepareClient(port, hosts);
	}

	@SuppressWarnings("unchecked")
	public static TransportClient prepareClient(int port, String... hosts) throws UnknownHostException {
		Settings settings = prepareClientSettings();
		TransportClient client = new PreBuiltTransportClient(settings);
		for (String host : hosts) {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
		}
		logInitialized(port, hosts);
		return client;
	}

	public static Settings prepareClientSettings() {
		return Settings.builder().put("client.transport.ignore_cluster_name", true).build();
	}

	/**
	 * Create mappings for {@link ElasticSearchConstants#INDEX_LOAN}.
	 * 
	 * @throws IOException
	 */
	public void createMappings() throws IOException {
		// create loan mappings.
		XContentBuilder loanMapping = prepareLoanIndexMapping();
		// create..
		client.admin().indices().preparePutMapping(INDEX_LOAN).setType(TYPE_ENTITY).setSource(loanMapping).execute()
				.actionGet();
	}

	/**
	 * Creates a loan mapping.
	 * 
	 * @return loan mapping.
	 * @throws IOException
	 */
	private XContentBuilder prepareLoanIndexMapping() throws IOException {
		XContentBuilder loanMapping = XContentFactory.jsonBuilder()
				// entity..
				.startObject().startObject(TYPE_ENTITY).startObject(PROPERTIES)
				// identification no..
				.startObject(Tag.IDENTIFICATION_NO).field(TYPE, KEYWORD).endObject()
				// name..
				.startObject(Tag.NAME).field(TYPE, TEXT).endObject()
				// surname..
				.startObject(Tag.SURNAME).field(TYPE, TEXT).endObject()
				// monthly income..
				.startObject(Tag.MONTHLY_INCOME).field(TYPE, DOUBLE).endObject()
				// phone no..
				.startObject(Tag.PHONE_NO).field(TYPE, TEXT).endObject()
				// credit limit..
				.startObject(Tag.CREDIT_RESULT).field(TYPE, BOOLEAN).endObject()
				// credit limit..
				.startObject(Tag.CREDIT_LIMIT).field(TYPE, DOUBLE).endObject()
				// credit score..
				.startObject(Tag.CREDIT_SCORE).field(TYPE, DOUBLE).endObject().endObject().endObject().endObject();
		return loanMapping;
	}

	/**
	 * Creates index which is given in parameter and its mappings if there is no
	 * index with a given string.
	 * 
	 * @param indexName Will be created indexName
	 * @param settings  index's settings.
	 * @param mapping   Mapping json string.
	 * @throws IOException
	 */
	public void createIndexesIfNotExists(String indexName) throws IOException {
		try {
			client.admin().indices().prepareCreate(indexName).execute().actionGet();
		} catch (Exception e) {
		}
		createMappings();
		client.admin().indices().refresh(new RefreshRequest(indexName)).actionGet();
	}

	public DeleteResponse deleteProduct(String uri) {
		return deleteEntity(INDEX_LOAN, TYPE_ENTITY, uri);
	}

	/**
	 * Deletes given entity from given index.
	 * 
	 * @param index index to lookup.
	 * @param type  of the entity.
	 * @param uri   identifier of the entity.
	 * @return delete response.
	 */
	public DeleteResponse deleteEntity(String index, String type, String uri) {
		DeleteResponse response = client.prepareDelete(index, type, uri).get();
		logDeleted(uri, response);
		return response;
	}

	/**
	 * Adds given json data to elastic search.
	 * 
	 * @param dataJson as json object.
	 * @return response
	 */
	public IndexResponse addProduct(String index, String type, JsonObject dataJson) {
		return add(dataJson, index, type, OpType.CREATE);
	}

	/**
	 * Adds given json data to elastic search.
	 * 
	 * @return response of the add operation.
	 */
	private IndexResponse add(JsonObject jsonObj, String indexName, String typeName, OpType operation) {
		String identificationNo = getIdentifier(jsonObj);
		IndexResponse response = add(jsonObj, indexName, typeName, operation, identificationNo);
		return response;
	}

	private String getIdentifier(JsonObject jsonObj) {
		return jsonObj.get(Tag.IDENTIFICATION_NO).getAsString();
	}

	private IndexResponse add(JsonObject jsonObj, String indexName, String typeName, OpType operation,
			String identificationNo) {
		IndexResponse response = null;
		try {
			IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, typeName, identificationNo);
			indexRequestBuilder.setOpType(operation);
			response = indexRequestBuilder.setSource(jsonObj.toString(), XContentType.JSON).get();
		} catch (Exception e) {
			String msg = "already exist: product: {0}";
			logger.error(MessageFormat.format(msg, jsonObj.toString()), e);
		}
		return response;
	}

	/**
	 * Checks if {@link ElasticSearchClient} host is "localhost".
	 * 
	 * @return boolean valid.
	 */
	public boolean checkElasticsearchHost() {
		boolean valid = false;
		ApplicationConfig config = ApplicationConfig.getInstance();
		String hosts = config.getElasticSearchHosts();
		if (hosts.equals("localhost")) {
			valid = true;
		} else {
			logger.error(
					"application.conf loanapplicationsystem.elasticsearch.hosts parameter value must be \"localhost\".");
		}
		return valid;
	}

	public ElasticSearchResult search(String searchId, TimeValue keepAlive, String count, String index, String type,
			SearchParameter... parameters) {
		return search(searchId, keepAlive, count, index, type, null, parameters);
	}

	/**
	 * Prepares and query for given search parameters.
	 * 
	 * @param searchId  can be <code>null</code> if this is not a scrolled query.
	 *                  Otherwise should be sent searchId which is returned previous
	 *                  {@link ElasticSearchResult#getSearchId()}.
	 * @param keepAlive keep alive time in milliseconds.
	 * @param count     requested product count at a time. can be <code>null</code>
	 *                  to use default
	 *                  {@link ElasticSearchConstants#SCROLL_SIZE_25}, otherwise
	 *                  should be given as string. If given string is not proper
	 *                  integer it will be thrown error.
	 * @param index     index to query
	 * @param type      type to query
	 */
	public ElasticSearchResult search(String searchId, TimeValue keepAlive, String count, String index, String type,
			String[] includeField, SearchParameter... parameters) {
		ResultStrategy strategy = new ResourceResultStrategy();
		BoolQueryBuilder qb = QueryBuilders.boolQuery();

		// traverse over search parameters like monthly income, name, surname, credit
		// score and limit..
		for (SearchParameter searchParameter : parameters) {
			qb = searchParameter.apply(qb);
		}
		// execute search query..
		SearchResponse scrollResp;
		ElasticSearchResult result = null;
		try {
			if (searchId != null) {
				scrollResp = client.prepareSearchScroll(searchId).setScroll(keepAlive).execute().actionGet();
			} else {
				count = getCount(count);
				SearchRequestBuilder searchBuilder = client.prepareSearch();
				SearchRequestBuilder searchRequestBuilder = searchBuilder.setIndices(index).setTypes(type).setQuery(qb)
						.setScroll(keepAlive).setSize(Integer.parseInt(count));
				if (includeField != null && includeField.length > 0) {
					searchRequestBuilder.setFetchSource(includeField, null);
				}
				scrollResp = searchRequestBuilder.execute().actionGet();
				logger.info("search query : " + searchBuilder);
			}
			result = (ElasticSearchResult) strategy.prepareResults(scrollResp, null);
			logSearched(result);
		} catch (SearchPhaseExecutionException e) {
			if (e.getCause() != null && e.getCause().getCause() != null
					&& e.getCause().getCause() instanceof SearchContextMissingException) {
				result = new ElasticSearchResult(MISSINGCONTEXT);
			} else {
				logger.warn(String.format("could not search: {\"index\": \"%s\", \"type\": \"%s\"}", index, type), e);
			}
		}
		return result;
	}

	/**
	 * Checks count value and assign default value if it is given as null.
	 * 
	 * @param count count to check.
	 * @return count itself or default value.
	 */
	private String getCount(String count) {
		if (count == null || count.equals("0")) {
			count = SCROLL_SIZE_25;
		}
		return count;
	}

	/**
	 * Gets indexed content and convert it to a real product object in json-ld
	 * format.
	 * 
	 * @param indexedContent content which is indexed in elastic search.
	 * @return product as json-ld format.
	 */
	public static JsonObject getResourceFromElasticSearchResult(String sourceAsString) {
		return JsonParser.parseString(sourceAsString).getAsJsonObject();
	}

	/* Elasticsearch Logger */
	private static void logInitialized(int port, String[] hosts) {
		logger.info("Client created. host: " + Arrays.toString(hosts) + " port: " + String.valueOf(port));
	}

	private void logErrorESInitialized(UnknownHostException e) {
		logger.error("Error occurred during elastic search initialized", e);
	}

	private void logErrorClientCreated(UnknownHostException e) {
		logger.error("error occurred during client created", e);
	}

	private void logDeleted(String uri, DeleteResponse response) {
		if (logger.isDebugEnabled()) {
			String msg = "deleted: found: {0}, uri: {1}";
			logger.debug(MessageFormat.format(msg, response.status(), uri));
		}
	}

	private void logSearched(ElasticSearchResult result) {
		logger.info(MessageFormat.format("searched: {0}", result));
	}
}
