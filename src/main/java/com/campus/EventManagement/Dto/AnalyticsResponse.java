package com.campus.EventManagement.Dto;

public class AnalyticsResponse {

    private long totalUsers;
    private long totalStudents;
    private long totalClubs;

    private long totalEvents;
    private long approvedEvents;
    private long pendingEvents;

    private long totalRegistrations;

    public AnalyticsResponse(
            long totalUsers,
            long totalStudents,
            long totalClubs,
            long totalEvents,
            long approvedEvents,
            long pendingEvents,
            long totalRegistrations
    ) {

        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.totalClubs = totalClubs;
        this.totalEvents = totalEvents;
        this.approvedEvents = approvedEvents;
        this.pendingEvents = pendingEvents;
        this.totalRegistrations = totalRegistrations;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getTotalClubs() {
        return totalClubs;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public long getApprovedEvents() {
        return approvedEvents;
    }

    public long getPendingEvents() {
        return pendingEvents;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }
}