package com.PollBuzz.pollbuzz;

public class VoteDetails {
    private String UserId,option,username,PollId,profileUrl;

    public VoteDetails(String PollId, String option, String username,String UserId,String profileUrl) {
        this.PollId = PollId;
        this.UserId = UserId;
        this.option = option;
        this.username = username;
        this.profileUrl=profileUrl;
    }

    public String getProgfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getPollId() {
        return PollId;
    }

    public void setPollId(String pollId) {
        PollId = pollId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return UserId;
    }

    public String getOption() {
        return option;
    }

    public String getUsername() {
        return username;
    }

    VoteDetails(){}
}
