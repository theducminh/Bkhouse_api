package com.api.bkhouse.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;

@Entity
@Table(name = "planning_zones")
public class PlanningZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "zone_name")
    private String zoneName;
    
    @Column(name = "zone_type")
    private String zoneType;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }
}
