package com.metro.routing;

import com.metro.domain.Station;
import com.metro.graph.MetroNetwork;
import com.metro.graph.Track;
import com.metro.exceptions.InvalidRouteException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BFSStrategy implements IRoutingStrategy {

    @Override
    public RouteResult findRoute(MetroNetwork network, Station source, Station destination) {
        if (source.equals(destination)) {
            return new RouteResult(new LinkedList<>(), 0.0, 0);
        }

        Queue<Station> queue = new LinkedList<>();
        Map<Station, Track> edgeToReach = new HashMap<>();
        Map<Station, Boolean> visited = new HashMap<>();

        queue.add(source);
        visited.put(source, true);

        boolean found = false;

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(destination)) {
                found = true;
                break;
            }

            for (Track track : network.getAdjacentTracks(current)) {
                Station neighbor = track.getDestination();
                if (!visited.getOrDefault(neighbor, false)) {
                    visited.put(neighbor, true);
                    edgeToReach.put(neighbor, track);
                    queue.add(neighbor);
                }
            }
        }

        if (!found) {
            throw new InvalidRouteException("No valid route exists between " + source.getName() + " and " + destination.getName());
        }

        return reconstructPath(edgeToReach, source, destination);
    }

    private RouteResult reconstructPath(Map<Station, Track> edgeToReach, Station source, Station destination) {
        LinkedList<Track> path = new LinkedList<>();
        Station current = destination;
        double totalTime = 0.0;
        int transfers = 0;
        String currentLine = null;

        while (!current.equals(source)) {
            Track track = edgeToReach.get(current);
            path.addFirst(track);
            totalTime += track.getTravelTimeMinutes();
            current = track.getSource();
        }

        // Calculate transfers strictly for the output DTO accuracy
        for (Track t : path) {
            if (currentLine != null && !currentLine.equals(t.getLineName())) {
                transfers++;
            }
            currentLine = t.getLineName();
        }

        return new RouteResult(path, totalTime, transfers);
    }
}