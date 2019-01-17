package com.amiablecore.warehouse.beans;

/**
 * @author Pravin
 */
public class Outward {
    private Integer outwardId;
    private Integer traderId;
    private Integer inwardId;
    private Integer whAdminId;
    private Integer whUserId;
    private String outwardDate;
    private Integer totalQuantity;
    private Double totalWeight;
    private String lotName;

    public Outward(Integer outwardId, Integer traderId, Integer inwardId, Integer whAdminId, Integer whUserId, String outwardDate, Integer totalQuantity, Double totalWeight, Double bagWeight, String lotName) {
        this.outwardId = outwardId;
        this.traderId = traderId;
        this.inwardId = inwardId;
        this.whAdminId = whAdminId;
        this.whUserId = whUserId;
        this.outwardDate = outwardDate;
        this.totalQuantity = totalQuantity;
        this.totalWeight = totalWeight;
        this.bagWeight = bagWeight;
        this.lotName = lotName;
    }

    public Outward() {
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public String getLotName() {

        return lotName;
    }

    private Double bagWeight;

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    public Integer getTraderId() {

        return traderId;
    }

    public void setInwardId(Integer inwardId) {
        this.inwardId = inwardId;
    }

    public Integer getInwardId() {
        return inwardId;
    }

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
