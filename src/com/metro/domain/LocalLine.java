package com.metro.domain;

import java.util.List;

public class LocalLine extends TrainLine {

    public LocalLine(String id, String name, List<Station> routeStations) {
        super(id, name, routeStations);
    }

    @Override
    public boolean servesStation(Station station) {
        if (station == null) {
            return false;
        }
        return getRouteStations().contains(station);
    }

    @Override
    public List<Station> getServedStations() {
        return getRouteStations();
    }
}
