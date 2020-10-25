package application.loanapplicationsystem.database.result;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SearchResult {

	/**
	 * Search result success property.
	 */
	private boolean success = true;

	/**
	 * Converts this object into JSON object string and excludes fields that is not
	 * annotated as Expose.
	 * 
	 * @return JSON String
	 */
	public String toJsonStringWithIncludingExpose() {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
	}

	public JsonObject toJson() {
		return JsonParser.parseString(toJsonStringWithIncludingExpose()).getAsJsonObject();
	}

	public SearchResult setFailure() {
		success = false;
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

}
