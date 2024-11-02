package ru.itmentor.spring.boot_security.demo.Util;

public class UserErrorResponse {
    private String message;
    private long timestamp;

    public UserErrorResponse(String message,long timestamp) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
