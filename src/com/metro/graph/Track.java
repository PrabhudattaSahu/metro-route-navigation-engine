package com.metro.graph;

import com.metro.domain.Station;
import java.util.Objects;

public class Track {
    
    private final Station source;
    private final Station destination;
    private final String lineName;
    private final double travelTimeMinutes;

    public Track(Station source, Station destination, String lineName, double travelTimeMinutes) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source and destination stations cannot be null");
        }
        if (lineName == null || lineName.trim().isEmpty()) {
            throw new IllegalArgumentException("Line name cannot be null or empty");
        }
        if (travelTimeMinutes <= 0) {
            throw new IllegalArgumentException("Travel time must be positive");
        }
        this.source = source;
        this.destination = destination;
        this.lineName = lineName;
        this.travelTimeMinutes = travelTimeMinutes;
    }

    public Station getSource() {
        return source;
    }

    public Station getDestination() {
        return destination;
    }

    public String getLineName() {
        return lineName;
    }

    public double getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Double.compare(track.travelTimeMinutes, travelTimeMinutes) == 0 &&
               source.equals(track.source) &&
               destination.equals(track.destination) &&
               lineName.equals(track.lineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination, lineName, travelTimeMinutes);
    }
}