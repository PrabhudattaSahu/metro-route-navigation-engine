package com.metro.routing;

import com.metro.domain.Station;
import com.metro.graph.MetroNetwork;
import com.metro.exceptions.InvalidRouteException;

public interface IRoutingStrategy {
    RouteResult findRoute(MetroNetwork network, Station source, Station destination) throws InvalidRouteException;
}