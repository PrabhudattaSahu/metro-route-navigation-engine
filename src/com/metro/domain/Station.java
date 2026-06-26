package com.metro.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Station {
    
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final Set<String> lines;

    public Station(String id, String name, double latitude, double longitude) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lines = new HashSet<>();
    }

    public void addLine(String lineName) {
        if (lineName == null || lineName.trim().isEmpty()) {
            throw new IllegalArgumentException("Line name cannot be null or empty");
        }
        this.lines.add(lineName);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Set<String> getLines() {
        return new HashSet<>(lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lines=" + lines +
                '}';
    }
}