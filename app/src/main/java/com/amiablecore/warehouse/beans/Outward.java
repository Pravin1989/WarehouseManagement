package com.amiablecore.warehouse.beans;

/**
 * @author Pravin
 *
 */
public class Outward {
	private Integer outwardId;
	private Integer outwardLotId;
	private Integer whAdminId;
	private Integer whUserId;
	private String outwardDate;
	private Integer totalQuantity;
	private Double totalWeight;
	private Double bagWeight;

	public void setBagWeight(Double bagWeight) {
		this.bagWeight = bagWeight;
	}

	public Double getBagWeight() {
		return bagWeight;
	}

	public String getOutwardDate() {
		return outwardDate;
	}

	public void setOutwardDate(String outwardDate) {
		this.outwardDate = outwardDate;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public Integer getOutwardId() {
		return outwardId;
	}

	public void setOutwardId(Integer outwardId) {
		this.outwardId = outwardId;
	}

	public Integer getOutwardLotId() {
		return outwardLotId;
	}

	public void setOutwardLotId(Integer outwardLotId) {
		this.outwardLotId = outwardLotId;
	}

	public Integer getWhAdminId() {
		return whAdminId;
	}

	public void setWhAdminId(Integer whAdminId) {
		this.whAdminId = whAdminId;
	}

	public Integer getWhUserId() {
		return whUserId;
	}

	public void setWhUserId(Integer whUserId) {
		this.whUserId = whUserId;
	}

}
