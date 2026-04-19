package com.api.bkhouse.payload.response;

public class CountInterestAndCommentResponse {
    private Long noOfInterest;
    private Long noOfComment;

    public Long getNoOfInterest() {
        return noOfInterest;
    }

    public void setNoOfInterest(Long noOfInterest) {
        this.noOfInterest = noOfInterest;
    }

    public Long getNoOfComment() {
        return noOfComment;
    }

    public void setNoOfComment(Long noOfComment) {
        this.noOfComment = noOfComment;
    }
}
