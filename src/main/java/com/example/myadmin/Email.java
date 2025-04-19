package com.example.myadmin;

import java.io.Serializable;
import java.util.Map;

public class Email implements Serializable {
    private String emailId;
    private String sender;
    private String recipient;
    private String subject;
    private String message;
    private long timestamp;
    private Map<String, String> attachment;

    public Email() {}

    public Email(String emailId, String sender, String recipient, String subject, String message, long timestamp, Map<String, String> attachment) {
        this.emailId = emailId;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.timestamp = timestamp;
        this.attachment = attachment;
    }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }
}
