package com.amiablecore.warehouse.beans;

public class Inward {
    private String inwardId;
    private String inwardDate;
    private String lotName;
    private String traderId;
    private Integer commodityId;
    private Integer categoryId;
    private Integer totalQuantity;
    private Double weightPerBag;
    private Double totalWeight;
    private String physicalAddress;
    private Integer whAdminId;
    private Integer whUserId;

    public void setWhAdminId(Integer whAdminId) {
        this.whAdminId = whAdminId;
    }

    public void setWhUserId(Integer whUserId) {
        this.whUserId = whUserId;
    }

    public Integer getWhAdminId() {
        return whAdminId;
    }

    public Integer getWhUserId() {
        return whUserId;
    }

    public String getLotName() {
        return lotName;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public void setInwardId(String inwardId) {
        this.inwardId = inwardId;
    }

    public void setInwardDate(String inwardDate) {
        this.inwardDate = inwardDate;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setWeightPerBag(Double weightPerBag) {
        this.weightPerBag = weightPerBag;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getInwardId() {
        return inwardId;
    }

    public String getInwardDate() {
        return inwardDate;
    }

    public String getTraderId() {
        return traderId;
    }

    public Integer getCommodityId() {
        return commodityId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public Double getWeightPerBag() {
        return weightPerBag;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

}
