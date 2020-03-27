package com.PollBuzz.pollbuzz;

public class VoteDetails {
    String UserId,option,username;

    public VoteDetails(String userId, String option, String username) {
        UserId = userId;
        this.option = option;
        this.username = username;
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
