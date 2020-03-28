package com.PollBuzz.pollbuzz;

import java.util.Map;

public class PollDetails {

    private String title, question, created_date, expiry_date, poll_type, author, UID;
    private Map<String, Integer> map;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getPoll_type() {
        return poll_type;
    }

    public void setPoll_type(String poll_type) {
        this.poll_type = poll_type;
    }
}