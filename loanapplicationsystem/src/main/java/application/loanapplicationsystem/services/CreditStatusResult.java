package application.loanapplicationsystem.services;

import com.google.gson.JsonObject;

import application.loanapplicationsystem.util.Tag;

public class CreditStatusResult {

	private static final double CREDIT_LIMIT_MULTIPLER = 4;

	private String identificationNo;
	private double creaditScore;
	private double monthlyIncome;

	public CreditStatusResult(String identificationNo, double creaditScore, double monthlyIncome) {
		this.identificationNo = identificationNo;
		this.creaditScore = creaditScore;
		this.monthlyIncome = monthlyIncome;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public double getCreaditScore() {
		return creaditScore;
	}

	public double getMonthlyIncome() {
		return monthlyIncome;
	}

	public JsonObject process() {
		JsonObject resultJsonObject = new JsonObject();
		resultJsonObject.addProperty(Tag.IDENTIFICATION_NO, getIdentificationNo());
		if (getCreaditScore() < 500) {
			resultJsonObject.addProperty(Tag.CREDIT_RESULT, false);
		} else if (getCreaditScore() == 500) {
			// this condition information is missing..
		} else if ((getCreaditScore() > 500 && getCreaditScore() < 1000) && getMonthlyIncome() < 5000) {
			resultJsonObject.addProperty(Tag.CREDIT_RESULT, true);
			resultJsonObject.addProperty(Tag.CREDIT_LIMIT, 10000);
		} else if ((getCreaditScore() > 500 && getCreaditScore() < 1000) && getMonthlyIncome() >= 5000) {
			// this condition information is missing..
		} else if (getCreaditScore() >= 1000) {
			Double creditLimit = getMonthlyIncome() * CREDIT_LIMIT_MULTIPLER;
			resultJsonObject.addProperty(Tag.CREDIT_RESULT, true);
			resultJsonObject.addProperty(Tag.CREDIT_LIMIT, creditLimit);
		}
		return resultJsonObject;
	}

}
