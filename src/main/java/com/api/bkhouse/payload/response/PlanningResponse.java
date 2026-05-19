package com.api.bkhouse.payload.response;

public class PlanningResponse {
    private boolean isPlanning;
    private String zoneName;
    private String zoneType;

    // Constructor nhanh
    public PlanningResponse(boolean isPlanning, String zoneName, String zoneType) {
        this.isPlanning = isPlanning;
        this.zoneName = zoneName;
        this.zoneType = zoneType;
    }

    // Getters and Setters
    public boolean isPlanning() { return isPlanning; }
    public void setPlanning(boolean planning) { isPlanning = planning; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }
}
