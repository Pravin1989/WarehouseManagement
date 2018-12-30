package com.amiablecore.warehouse.beans;

/**
 * @author Pravin
 *
 */
public class Trader {
	private String traderName;
	private String emailId;
	private String city;
	private String contactNo;
	private String traderState;
	private String traderPinCode;
	private String whAdminId;
	private Integer traderId;

	public Integer getTraderId() {
		return traderId;
	}

	public void setTraderId(Integer traderId) {
		this.traderId = traderId;
	}

	public String getTraderName() {
		return traderName;
	}

	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getTraderState() {
		return traderState;
	}

	public void setTraderState(String traderState) {
		this.traderState = traderState;
	}

	public String getTraderPinCode() {
		return traderPinCode;
	}

	public void setTraderPinCode(String traderPinCode) {
		this.traderPinCode = traderPinCode;
	}

	public String getWhAdminId() {
		return whAdminId;
	}

	public void setWhAdminId(String whId) {
		this.whAdminId = whId;
	}
}
