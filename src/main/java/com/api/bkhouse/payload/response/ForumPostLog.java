package com.api.bkhouse.payload.response;

public class ForumPostLog {
    private Long noReports;
    private Long noLikes;
    private Long noComments;

    public Long getNoReports() {
        return noReports;
    }

    public void setNoReports(Long noReports) {
        this.noReports = noReports;
    }

    public Long getNoLikes() {
        return noLikes;
    }

    public void setNoLikes(Long noLikes) {
        this.noLikes = noLikes;
    }

    public Long getNoComments() {
        return noComments;
    }

    public void setNoComments(Long noComments) {
        this.noComments = noComments;
    }
}
