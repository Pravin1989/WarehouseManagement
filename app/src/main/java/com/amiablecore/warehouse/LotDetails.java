package com.amiablecore.warehouse;

public class LotDetails {
    private int id;
    private String lotName;
    private Double weightPerBag;
    private Double totalWeight;
    private Integer totalQuantityCurrent;

    public int getId() {
        return id;
    }

    public String getLotName() {
        return lotName;
    }

    public Double getWeightPerBag() {
        return weightPerBag;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public Integer getTotalQuantityCurrent() {
        return totalQuantityCurrent;
    }

    public LotDetails(int id, String lotName, Double weightPerBag, Double totalWeight, Integer totalQuantityCurrent) {
        this.id = id;
        this.lotName = lotName;
        this.weightPerBag = weightPerBag;
        this.totalWeight = totalWeight;
        this.totalQuantityCurrent = totalQuantityCurrent;
    }
}
