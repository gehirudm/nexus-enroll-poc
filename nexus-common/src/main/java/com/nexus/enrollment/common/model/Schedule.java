package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.util.JsonSerializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class Schedule implements JsonSerializable {
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    
    public Schedule() {}
    
    public Schedule(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String location) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public boolean hasTimeConflict(Schedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }
    
    // JSON serialization method
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id != null ? id : "null").append(",");
        json.append("\"dayOfWeek\":\"").append(dayOfWeek != null ? dayOfWeek.toString() : "").append("\",");
        json.append("\"startTime\":\"").append(startTime != null ? startTime.toString() : "").append("\",");
        json.append("\"endTime\":\"").append(endTime != null ? endTime.toString() : "").append("\",");
        json.append("\"location\":\"").append(location != null ? location.replace("\"", "\\\"") : "").append("\"");
        json.append("}");
        return json.toString();
    }
}
