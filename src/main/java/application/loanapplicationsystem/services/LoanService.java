package application.loanapplicationsystem.services;

import static application.loanapplicationsystem.util.Tag.CREDIT_LIMIT;
import static application.loanapplicationsystem.util.Tag.CREDIT_RESULT;
import static application.loanapplicationsystem.util.Tag.CREDIT_SCORE;
import static application.loanapplicationsystem.util.Tag.IDENTIFICATION_NO;
import static application.loanapplicationsystem.util.Tag.MONTHLY_INCOME;
import static application.loanapplicationsystem.util.Tag.NAME;
import static application.loanapplicationsystem.util.Tag.PHONE_NO;
import static application.loanapplicationsystem.util.Tag.SURNAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.loanapplicationsystem.database.AbstractDBFactory;
import application.loanapplicationsystem.database.AbstractDBOperation;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.tags.Tag;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/loan")
@Tag(name = "LoanCalculation")
public class LoanService {

	/**
	 * Logger instance.
	 */
	private static Logger logger = Logger.getLogger(LoanService.class);

	AbstractDBFactory abstractDBFactory = new AbstractDBFactory();

	@Produces(MediaType.APPLICATION_JSON)
	@POST
	@Path("/calculate")
	public Response calculate(@Context HttpServletRequest request) throws IOException {

		ResponseMessage responseMessage = null;
		try {
			UserInfoRequest userInfoRequest = getUserInformation(request);
			if (userInfoRequest != null) {
				AbstractDBOperation abstractDBOperation = abstractDBFactory.getInstance();
				List<JsonObject> userInformations = abstractDBOperation
						.getUserInformationToDB(userInfoRequest.getIdentificationNo());
				for (JsonObject userInformation : userInformations) {
					String identificationNo = userInformation.get(IDENTIFICATION_NO).getAsString();
					double creaditScore = userInformation.get(CREDIT_SCORE).getAsDouble();
					double monthlyIncome = userInformation.get(MONTHLY_INCOME).getAsDouble();
					CreditStatusResult creditStatusResult = new CreditStatusResult(identificationNo, creaditScore,
							monthlyIncome);
					JsonObject creditResultJsonObject = creditStatusResult.process();
					abstractDBOperation.writeCreditResultToDB(creditResultJsonObject);
				}
				String[] includeField = new String[] { CREDIT_LIMIT, CREDIT_RESULT };
				List<JsonObject> creditResultAndLimit = abstractDBOperation
						.getCreditResultAndLimit(userInfoRequest.getIdentificationNo(), includeField);
				// response..
				JsonObject response = prepareResponseObject(creditResultAndLimit.get(0));
				responseMessage = new ResponseMessage(Status.OK, "Loan information results returned successfully.",
						response);
			}
		} catch (JwtException e) {
			logger.error("Error occured while missing parameter.", e);
			responseMessage = new ResponseMessage(Status.BAD_REQUEST, "Error occured while missing parameter.");
		} catch (Exception e) {
			logger.error("Unexpected error occured.", e);
			responseMessage = new ResponseMessage(Status.INTERNAL_SERVER_ERROR, "Unexpected error occured.");
		}
		return Response.status(responseMessage.getStatusCode()).entity(responseMessage.toJson().toString()).build();
	}

	public UserInfoRequest getUserInformation(HttpServletRequest request) throws IOException {
		String body = getBody(request);
		if (body != null && !body.isEmpty()) {
			JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
			String identifier = jsonObject.get(IDENTIFICATION_NO).getAsString();
			String name = jsonObject.get(NAME).getAsString();
			String surname = jsonObject.get(SURNAME).getAsString();
			String phoneNo = jsonObject.get(PHONE_NO).getAsString();
			Double monthluIncome = jsonObject.get(MONTHLY_INCOME).getAsDouble();
			UserInfoRequest userInfoRequest = new UserInfoRequest(identifier, name, surname, monthluIncome, phoneNo);
			return userInfoRequest;
		}
		return null;
	}

	private String getBody(HttpServletRequest request) throws IOException {
		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		InputStream inputStream = request.getInputStream();
		if (inputStream != null) {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			char[] charBuffer = new char[128];
			int bytesRead = -1;
			while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
				stringBuilder.append(charBuffer, 0, bytesRead);
			}
		} else {
			stringBuilder.append("");
		}
		body = stringBuilder.toString();
		return body;
	}

	private JsonObject prepareResponseObject(JsonObject jsonObject) {
		if (jsonObject.has(CREDIT_RESULT)) {
			boolean creditResult = jsonObject.get(CREDIT_RESULT).getAsBoolean();
			if (creditResult) {
				jsonObject.addProperty(CREDIT_RESULT, "approved");
			} else {
				jsonObject.addProperty(CREDIT_RESULT, "refuse");
			}
		}
		return jsonObject;
	}
}
