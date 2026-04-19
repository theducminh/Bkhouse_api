package com.api.bkhouse.payload.response;

import java.util.List;

public class PushNotifyResponse {
    private String multicast_id;
    private Integer success;
    private Integer failure;
    private Integer canonical_ids;
    private List<PNResult> results;

    public String getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(String multicast_id) {
        this.multicast_id = multicast_id;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailure() {
        return failure;
    }

    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    public Integer getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(Integer canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<PNResult> getResults() {
        return results;
    }

    public void setResults(List<PNResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "PushNotifyResponse{" +
                "multicast_id='" + multicast_id + '\'' +
                ", success=" + success +
                ", failure=" + failure +
                ", canonical_ids=" + canonical_ids +
                ", results=" + results.toString() +
                '}';
    }
}
