package application.loanapplicationsystem.database.result;

import java.util.ArrayList;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import application.loanapplicationsystem.database.elasticsearch.ElasticSearchClient;

public class ResourceResultStrategy implements ResultStrategy {

	public SearchResult prepareResults(SearchResponse scrollResp, JsonElement queryJson) {
		ArrayList<JsonObject> results = new ArrayList<JsonObject>();
		String searchId = scrollResp.getScrollId();
		long totalHits = scrollResp.getHits().totalHits;
		long tookInMillis = scrollResp.getTook().getMillis();
		SearchHit[] hits = scrollResp.getHits().getHits();
		for (SearchHit hit : hits) {
			String sourceAsString = hit.getSourceAsString();
			JsonObject resultJson = ElasticSearchClient.getResourceFromElasticSearchResult(sourceAsString);
			results.add(resultJson);
		}
		return new ElasticSearchResult(results, searchId, totalHits, tookInMillis);
	}
}
