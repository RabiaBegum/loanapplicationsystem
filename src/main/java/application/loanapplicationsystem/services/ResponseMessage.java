package application.loanapplicationsystem.services;

import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonObject;

public class ResponseMessage {

	private Status statusCode;
	private String message;
	private JsonObject jsonObject;

	public ResponseMessage(Status statusCode, String message, JsonObject jsonObject) {
		this.statusCode = statusCode;
		this.message = message;
		this.jsonObject = jsonObject;
	}

	public ResponseMessage(Status statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	public Status getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Status statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JsonObject toJson() {
		JsonObject response = new JsonObject();
		response.addProperty("status", this.statusCode.getStatusCode());
		if (this.message != null) {
			response.addProperty("message", getMessage());
		}
		if (this.jsonObject != null) {
			response.add("result", getJsonObject());
		}
		return response;
	}

}
