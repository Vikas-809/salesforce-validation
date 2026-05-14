package com.example.Salesforce.util;

public class TokenStore {

    private static String accessToken;
    private static String instanceUrl;

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getInstanceUrl() {
        return instanceUrl;
    }

    public static void setInstanceUrl(String url) {
        instanceUrl = url;
    }
}