package com.metro.factory;

import com.metro.domain.ExpressLine;
import com.metro.domain.LocalLine;
import com.metro.domain.Station;
import com.metro.domain.TrainLine;

import java.util.List;
import java.util.Set;

public class TrainLineFactory {

    private TrainLineFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }

    public static TrainLine createLocalLine(String id, String name, List<Station> stations) {
        if (stations != null) {
            for (Station station : stations) {
                if (station != null) {
                    station.addLine(name);
                }
            }
        }
        return new LocalLine(id, name, stations);
    }

    public static TrainLine createExpressLine(String id, String name, List<Station> stations, Set<Station> skippedStations) {
        if (stations != null) {
            for (Station station : stations) {
                if (station != null && (skippedStations == null || !skippedStations.contains(station))) {
                    station.addLine(name);
                }
            }
        }
        return new ExpressLine(id, name, stations, skippedStations);
    }
}