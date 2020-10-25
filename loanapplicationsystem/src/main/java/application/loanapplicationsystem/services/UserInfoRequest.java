package application.loanapplicationsystem.services;

public class UserInfoRequest {

	private String identificationNo;
	private String name;
	private String surname;
	private Double monthlyIncome;
	private String phoneNo;
	
	public UserInfoRequest(String identificationNo, String name, String surname, Double monthlyIncome, String phoneNo) {
		this.identificationNo = identificationNo;
		this.name = name;
		this.surname = surname;
		this.monthlyIncome = monthlyIncome;
		this.phoneNo = phoneNo;
	}
	
	public String getIdentificationNo() {
		return identificationNo;
	}
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public Double getMonthlyIncome() {
		return monthlyIncome;
	}
	public void setMonthlyIncome(Double monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
}
