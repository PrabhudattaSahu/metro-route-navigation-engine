package com.metro.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpressLine extends TrainLine {

    private final Set<Station> skippedStations;

    public ExpressLine(String id, String name, List<Station> routeStations, Set<Station> skippedStations) {
        super(id, name, routeStations);
        this.skippedStations = skippedStations == null ? new HashSet<>() : new HashSet<>(skippedStations);
    }

    @Override
    public boolean servesStation(Station station) {
        if (station == null) {
            return false;
        }
        return getRouteStations().contains(station) && !skippedStations.contains(station);
    }

    @Override
    public List<Station> getServedStations() {
        List<Station> served = new ArrayList<>();
        for (Station station : getRouteStations()) {
            if (!skippedStations.contains(station)) {
                served.add(station);
            }
        }
        return served;
    }
}