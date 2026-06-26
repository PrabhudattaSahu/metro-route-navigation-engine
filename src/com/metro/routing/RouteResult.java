package com.metro.routing;

import com.metro.graph.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteResult {
    
    private final List<Track> path;
    private final double totalTravelTime;
    private final int totalStops;
    private final int transfers;

    public RouteResult(List<Track> path, double totalTravelTime, int transfers) {
        this.path = new ArrayList<>(path);
        this.totalTravelTime = totalTravelTime;
        this.totalStops = path.size();
        this.transfers = transfers;
    }

    public List<Track> getPath() {
        return Collections.unmodifiableList(path);
    }

    public double getTotalTravelTime() {
        return totalTravelTime;
    }

    public int getTotalStops() {
        return totalStops;
    }

    public int getTransfers() {
        return transfers;
    }
}