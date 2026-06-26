package com.metro.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TrainLine {
    
    private final String id;
    private final String name;
    private final List<Station> routeStations;

    public TrainLine(String id, String name, List<Station> routeStations) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("TrainLine ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("TrainLine name cannot be null or empty");
        }
        if (routeStations == null || routeStations.isEmpty()) {
            throw new IllegalArgumentException("TrainLine must have at least one station");
        }
        this.id = id;
        this.name = name;
        this.routeStations = new ArrayList<>(routeStations);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Station> getRouteStations() {
        return new ArrayList<>(routeStations);
    }

    public abstract boolean servesStation(Station station);

    public abstract List<Station> getServedStations();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainLine trainLine = (TrainLine) o;
        return id.equals(trainLine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}