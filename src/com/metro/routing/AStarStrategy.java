package com.metro.routing;

import com.metro.domain.Station;
import com.metro.graph.MetroNetwork;
import com.metro.graph.Track;
import com.metro.exceptions.InvalidRouteException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

public class AStarStrategy implements IRoutingStrategy {

    private static final double TRANSFER_PENALTY_MINUTES = 5.0;
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double MAX_METRO_SPEED_KMPH = 120.0; // Ensures heuristic is admissible

    private static class NodeRecord {
        Station station;
        Track incomingTrack;
        double gScore;
        double fScore;

        NodeRecord(Station station, Track incomingTrack, double gScore, double fScore) {
            this.station = station;
            this.incomingTrack = incomingTrack;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }

    @Override
    public RouteResult findRoute(MetroNetwork network, Station source, Station destination) {
        if (source.equals(destination)) {
            return new RouteResult(new LinkedList<>(), 0.0, 0);
        }

        PriorityQueue<NodeRecord> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<Station, Double> minGScore = new HashMap<>();
        Map<Station, Track> edgeToReach = new HashMap<>();

        openSet.add(new NodeRecord(source, null, 0.0, calculateHeuristic(source, destination)));
        minGScore.put(source, 0.0);

        while (!openSet.isEmpty()) {
            NodeRecord currentRecord = openSet.poll();
            Station currentStation = currentRecord.station;

            if (currentStation.equals(destination)) {
                return reconstructPath(edgeToReach, source, destination, currentRecord.gScore);
            }

            // If we found a structurally worse path to a previously processed node, skip it
            if (currentRecord.gScore > minGScore.getOrDefault(currentStation, Double.MAX_VALUE)) {
                continue;
            }

            for (Track track : network.getAdjacentTracks(currentStation)) {
                Station neighbor = track.getDestination();
                
                double edgeWeight = track.getTravelTimeMinutes();
                boolean isTransfer = false;
                
                if (currentRecord.incomingTrack != null && !currentRecord.incomingTrack.getLineName().equals(track.getLineName())) {
                    edgeWeight += TRANSFER_PENALTY_MINUTES;
                    isTransfer = true;
                }

                double tentativeGScore = currentRecord.gScore + edgeWeight;

                if (tentativeGScore < minGScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    minGScore.put(neighbor, tentativeGScore);
                    edgeToReach.put(neighbor, track);
                    double fScore = tentativeGScore + calculateHeuristic(neighbor, destination);
                    openSet.add(new NodeRecord(neighbor, track, tentativeGScore, fScore));
                }
            }
        }

        throw new InvalidRouteException("No valid route exists between " + source.getName() + " and " + destination.getName());
    }

    private double calculateHeuristic(Station current, Station target) {
        double latDistance = Math.toRadians(target.getLatitude() - current.getLatitude());
        double lonDistance = Math.toRadians(target.getLongitude() - current.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(current.getLatitude())) * Math.cos(Math.toRadians(target.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS_KM * c;

        // Admissible heuristic logic: minimum possible time using max straight-line speed
        return (distanceKm / MAX_METRO_SPEED_KMPH) * 60.0; 
    }

    private RouteResult reconstructPath(Map<Station, Track> edgeToReach, Station source, Station destination, double totalGScore) {
        LinkedList<Track> path = new LinkedList<>();
        Station current = destination;
        int transfers = 0;
        String currentLine = null;

        while (!current.equals(source)) {
            Track track = edgeToReach.get(current);
            path.addFirst(track);
            current = track.getSource();
        }

        for (Track t : path) {
            if (currentLine != null && !currentLine.equals(t.getLineName())) {
                transfers++;
            }
            currentLine = t.getLineName();
        }

        return new RouteResult(path, totalGScore, transfers);
    }
}