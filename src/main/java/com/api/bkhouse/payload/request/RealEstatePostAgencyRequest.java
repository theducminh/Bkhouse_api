package com.api.bkhouse.payload.request;

import java.util.List;
import java.util.UUID;

public class RealEstatePostAgencyRequest {
    private UUID realEstatePostId;
    private List<UUID> agencies;

    public UUID getRealEstatePostId() {
        return realEstatePostId;
    }

    public void setRealEstatePostId(UUID realEstatePostId) {
        this.realEstatePostId = realEstatePostId;
    }

    public List<UUID> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<UUID> agencies) {
        this.agencies = agencies;
    }
}
