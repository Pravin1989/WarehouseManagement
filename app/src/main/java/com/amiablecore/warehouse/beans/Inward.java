package com.amiablecore.warehouse.beans;

import java.util.Date;

public class Inward {
    private String inwardId;
    private Date inwardDate;
    private String lotName;
    private String traderId;
    private String commodityId;
    private String categoryId;
    private Integer totalQuantity;
    private Double wightPerBag;
    private Double totalWeight;

    public String getLotName() {
        return lotName;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public void setInwardId(String inwardId) {
        this.inwardId = inwardId;
    }

    public void setInwardDate(Date inwardDate) {
        this.inwardDate = inwardDate;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setWightPerBag(Double wightPerBag) {
        this.wightPerBag = wightPerBag;
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

    public Date getInwardDate() {
        return inwardDate;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public Double getWightPerBag() {
        return wightPerBag;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    private String physicalAddress;
}
