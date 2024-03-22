package com.mydaygpt;

public class Appointment {
    public String startTime;
    public String endTime;
    public String reminderTime;
    public String title;
    public String location;
    public String sender;
    public String mail;

    public Appointment() {
    }

    public Appointment(String startTime, String endTime, String reminderTime, String title, String location,
            String sender, String mail) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.reminderTime = reminderTime;
        this.title = title;
        this.location = location;
        this.sender = sender;
        this.mail = mail;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
