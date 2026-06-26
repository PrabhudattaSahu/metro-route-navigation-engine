package com.metro.graph;

import com.metro.domain.Station;
import com.metro.exceptions.StationNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetroNetwork {
    
    private final Map<String, Station> stationsById;
    private final Map<Station, List<Track>> adjacencyList;

    public MetroNetwork() {
        this.stationsById = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    public void addStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }
        stationsById.put(station.getId(), station);
        adjacencyList.putIfAbsent(station, new ArrayList<>());
    }

    public void addTrack(Station source, Station destination, String lineName, double travelTimeMinutes) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            throw new StationNotFoundException("Source or destination station not registered in the network");
        }
        
        Track forwardTrack = new Track(source, destination, lineName, travelTimeMinutes);
        adjacencyList.get(source).add(forwardTrack);
        
        // Assuming tracks are bidirectional but modeled as two directed edges
        Track reverseTrack = new Track(destination, source, lineName, travelTimeMinutes);
        adjacencyList.get(destination).add(reverseTrack);
    }

    public Station getStationById(String id) {
        Station station = stationsById.get(id);
        if (station == null) {
            throw new StationNotFoundException("Station with ID " + id + " not found in network");
        }
        return station;
    }

    public List<Track> getAdjacentTracks(Station station) {
        if (!adjacencyList.containsKey(station)) {
            throw new StationNotFoundException("Station not found in network");
        }
        return Collections.unmodifiableList(adjacencyList.get(station));
    }

    public Map<String, Station> getAllStations() {
        return Collections.unmodifiableMap(stationsById);
    }
}