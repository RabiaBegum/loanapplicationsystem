package application.loanapplicationsystem.database.result;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class ElasticSearchResult extends SearchResult {

	@Expose
	private List<JsonObject> searchResults;

	@Expose
	private String searchId;

	@Expose
	private long hits;

	@Expose
	private String failure;

	@Expose
	private long tookInMillis;

	/**
	 * @param results
	 * @param searchId
	 * @param hits
	 * @param tookInMillis
	 */
	public ElasticSearchResult(List<JsonObject> results, String searchId, long hits, long tookInMillis) {
		this.searchResults = results;
		this.searchId = searchId;
		this.hits = hits;
		this.tookInMillis = tookInMillis;
	}

	public ElasticSearchResult(String error) {
		this.failure = error;
	}

	public List<JsonObject> getSearchResults() {
		return searchResults;
	}

	public String getSearchId() {
		return searchId;
	}

	public long getHits() {
		return hits;
	}

	public String getFailure() {
		return failure;
	}

	public long getTookInMillis() {
		return tookInMillis;
	}

	@Override
	public String toString() {
		return "ElasticSearchResult [searchResultsCount=" + (searchResults != null ? searchResults.size() : "0")
				+ ", searchId=" + searchId + ", hits=" + hits + ", failure=" + failure + ", tookInMillis="
				+ tookInMillis + "]";
	}
}
