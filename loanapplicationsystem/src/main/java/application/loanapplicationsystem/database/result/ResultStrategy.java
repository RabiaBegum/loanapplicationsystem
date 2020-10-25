package application.loanapplicationsystem.database.result;

import org.elasticsearch.action.search.SearchResponse;

import com.google.gson.JsonElement;

public interface ResultStrategy {

	SearchResult prepareResults(SearchResponse scrollResp, JsonElement queryJson);
}
