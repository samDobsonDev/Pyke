package com.samdobsondev.pyke.api;

public record ApiResponse<T>(T responseObject, String rawResponse, int statusCode) {
    public boolean success()
    {
        return statusCode / 200 == 1;
    }
}
