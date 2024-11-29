package com.example.healthcare.models;

import java.util.List;

public class Token {
    private List<String> tokenList;
    private String userId;

    public Token() {
    }

    public Token(List<String> tokenList, String userId) {
        this.tokenList = tokenList;
        this.userId = userId;
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<String> tokenList) {
        this.tokenList = tokenList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
