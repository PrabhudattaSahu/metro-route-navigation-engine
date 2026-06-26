package com.metro.app;

import com.metro.domain.Station;
import com.metro.domain.TrainLine;
import com.metro.exceptions.InvalidRouteException;
import com.metro.exceptions.StationNotFoundException;
import com.metro.factory.TrainLineFactory;
import com.metro.graph.MetroNetwork;
import com.metro.graph.Track;
import com.metro.routing.AStarStrategy;
import com.metro.routing.BFSStrategy;
import com.metro.routing.IRoutingStrategy;
import com.metro.routing.RouteResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        MetroNetwork network = new MetroNetwork();
        initializeNetwork(network);

        Scanner scanner = new Scanner(System.in);
        System.out.println("==================================================");
        System.out.println("      WELCOME TO THE METRO ROUTE PLANNER          ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- Available Stations ---");
            for (Station station : network.getAllStations().values()) {
                System.out.printf("[%s] %s (Lines: %s)%n", station.getId(), station.getName(), String.join(", ", station.getLines()));
            }

            System.out.println("\nEnter Source Station ID (or type 'exit' to quit):");
            String sourceId = scanner.nextLine().trim().toUpperCase();
            if (sourceId.equalsIgnoreCase("EXIT")) {
                System.out.println("Thank you for using the Metro Route Planner. Goodbye!");
                break;
            }

            System.out.println("Enter Destination Station ID:");
            String destId = scanner.nextLine().trim().toUpperCase();

            System.out.println("Select Routing Strategy:");
            System.out.println("1. Fewest Stops (BFS)");
            System.out.println("2. Shortest Travel Time (A* Search)");
            String strategyChoice = scanner.nextLine().trim();

            try {
                Station source = network.getStationById(sourceId);
                Station destination = network.getStationById(destId);

                IRoutingStrategy strategy;
                if ("1".equals(strategyChoice)) {
                    strategy = new BFSStrategy();
                } else if ("2".equals(strategyChoice)) {
                    strategy = new AStarStrategy();
                } else {
                    System.out.println("Invalid strategy selection. Defaulting to Fewest Stops (BFS).");
                    strategy = new BFSStrategy();
                }

                RouteResult result = strategy.findRoute(network, source, destination);
                printRouteResult(result, source, destination);

            } catch (StationNotFoundException | InvalidRouteException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
                System.out.println("Please check your inputs and try again.");
            } catch (Exception e) {
                System.out.println("\n[CRITICAL ERROR] An unexpected error occurred: " + e.getMessage());
            }
            
            System.out.println("\nPress Enter to plan another route...");
            scanner.nextLine();
        }
        
        scanner.close();
    }

    private static void initializeNetwork(MetroNetwork network) {
        // 1. Create Stations with realistic coordinates (e.g., mock Manhattan/Brooklyn locations)
        Station astorPlace = new Station("AST", "Astor Place", 40.7295, -73.9904);
        Station unionSquare = new Station("USQ", "Union Square", 40.7359, -73.9911);
        Station timesSquare = new Station("TSQ", "Times Square", 40.7580, -73.9855);
        Station pennStation = new Station("PEN", "Penn Station", 40.7506, -73.9935);
        Station fultonSt = new Station("FUL", "Fulton Street", 40.7103, -74.0095);
        Station wallSt = new Station("WAL", "Wall Street", 40.7071, -74.0119);

        // Add to network
        network.addStation(astorPlace);
        network.addStation(unionSquare);
        network.addStation(timesSquare);
        network.addStation(pennStation);
        network.addStation(fultonSt);
        network.addStation(wallSt);

        // 2. Define Lines using Factory
        // Red Line (Local)
        TrainLine redLineLocal = TrainLineFactory.createLocalLine("L1", "Red Line", 
                Arrays.asList(timesSquare, pennStation, unionSquare, astorPlace, fultonSt, wallSt));
        
        // Blue Line (Express) - Skips Astor Place and Penn Station
        Set<Station> blueSkipped = new HashSet<>(Arrays.asList(pennStation, astorPlace));
        TrainLine blueLineExpress = TrainLineFactory.createExpressLine("E1", "Blue Line", 
                Arrays.asList(timesSquare, unionSquare, fultonSt, wallSt), blueSkipped);

        // 3. Populate Tracks (Graph Edges)
        // Red Line Local Tracks (Avg 3-5 mins between adjacent stations)
        network.addTrack(timesSquare, pennStation, redLineLocal.getName(), 3.0);
        network.addTrack(pennStation, unionSquare, redLineLocal.getName(), 4.0);
        network.addTrack(unionSquare, astorPlace, redLineLocal.getName(), 2.0);
        network.addTrack(astorPlace, fultonSt, redLineLocal.getName(), 6.0);
        network.addTrack(fultonSt, wallSt, redLineLocal.getName(), 2.0);

        // Blue Line Express Tracks (Faster travel times, skipping stations)
        network.addTrack(timesSquare, unionSquare, blueLineExpress.getName(), 5.0);
        network.addTrack(unionSquare, fultonSt, blueLineExpress.getName(), 6.0);
        network.addTrack(fultonSt, wallSt, blueLineExpress.getName(), 2.0);
        
        // Transfer Link (Simulating a walk/transfer track between lines at Union Square)
        // Note: The A* algorithm automatically injects a +5 min penalty when line names differ.
        network.addTrack(unionSquare, unionSquare, "Transfer Link", 1.0); 
    }

    private static void printRouteResult(RouteResult result, Station source, Station destination) {
        System.out.println("\n==================================================");
        System.out.println("                 ROUTE SUMMARY                    ");
        System.out.println("==================================================");
        System.out.println("From: " + source.getName());
        System.out.println("To:   " + destination.getName());
        
        if (result.getPath().isEmpty()) {
            System.out.println("You are already at your destination!");
            return;
        }

        System.out.printf("Total Travel Time: %.1f minutes%n", result.getTotalTravelTime());
        System.out.println("Total Stops: " + result.getTotalStops());
        System.out.println("Line Transfers: " + result.getTransfers());
        System.out.println("\n--- Step-by-Step Directions ---");

        String currentLine = result.getPath().get(0).getLineName();
        System.out.println("Board the " + currentLine + " at " + source.getName());

        for (int i = 0; i < result.getPath().size(); i++) {
            Track track = result.getPath().get(i);
            
            if (!track.getLineName().equals(currentLine)) {
                System.out.println("  --> [TRANSFER] Switch to the " + track.getLineName() + " at " + track.getSource().getName());
                currentLine = track.getLineName();
            }
            
            System.out.printf("  Travel to %s (%.1f mins)%n", track.getDestination().getName(), track.getTravelTimeMinutes());
        }

        System.out.println("Arrive at " + destination.getName());
        System.out.println("==================================================");
    }
}