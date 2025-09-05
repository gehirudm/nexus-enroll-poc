package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.enums.NotificationType;
import java.util.Date;

public class Notification {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private Date createdDate;
    private boolean isRead;
    
    public Notification() {
        this.createdDate = new Date();
        this.isRead = false;
    }
    
    public Notification(Long userId, NotificationType type, String message) {
        this();
        this.userId = userId;
        this.type = type;
        this.message = message;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
