package fr.sncf.osrd.train;

import fr.sncf.osrd.infra.routegraph.Route;
import fr.sncf.osrd.infra_state.RouteStatus;
import fr.sncf.osrd.infra.waypointgraph.TVDSectionPath;
import fr.sncf.osrd.simulation.Simulation;
import fr.sncf.osrd.simulation.SimulationError;
import fr.sncf.osrd.train.phases.SignalNavigatePhase;
import fr.sncf.osrd.infra_state.SwitchPost;

public class ActivateRoute {
    /** This function try to reserve forwarding routes */
    public static void reserveRoutes(
            Simulation sim,
            SignalNavigatePhase.State navigatePhaseState,
            Train trainReserver
    ) throws SimulationError {
        // TODO have a smarter way to reserve routes
        for (int advance = 1; advance <= 1; advance++) {
            if (navigatePhaseState.getRouteIndex() + advance < navigatePhaseState.phase.routePath.size()) {
                var nextRoute = navigatePhaseState.phase.routePath.get(navigatePhaseState.getRouteIndex() + advance);
                var nextRouteState = sim.infraState.getRouteState(nextRoute.index);
                sim.infraState.switchPost.request(sim, nextRouteState, trainReserver.schedule);
            }
        }
    }

    /** Reserve the initial routes, mark occupied tvd sections and add interactable elements that are under the train
     * to the TrainState*/
    public static void trainCreation(Simulation sim, TrainState trainState) throws SimulationError {
        Route route = trainState.trainSchedule.initialRoute;
        var routeState = sim.infraState.getRouteState(route.index);

        // Reserve the initial route
        if (routeState.status != RouteStatus.FREE)
            throw new SimulationError(String.format(
                    "Impossible to reserve the route '%s' since it is not available.", routeState.route.id));
        routeState.initialReserve(sim);

        // Reserve the tvdSection where the train is created
        var trainPosition = trainState.location.trackSectionRanges.getFirst();

        var lastTvdSectionPath = route.tvdSectionsPaths.get(0);
        occupyTvdSectionPath(sim, lastTvdSectionPath);

        for (var i = 0; i < route.tvdSectionsPaths.size(); i++) {
            var currentTvdSectionPath = route.tvdSectionsPaths.get(i);
            var currentTvdSectionPathDirection = route.tvdSectionsPathDirections.get(i);
            for (var trackSection : currentTvdSectionPath.getTrackSections(currentTvdSectionPathDirection)) {
                if (trainPosition.intersect(trackSection))
                    return;
            }
            freeTvdSectionPath(sim, lastTvdSectionPath);
            occupyTvdSectionPath(sim, currentTvdSectionPath);
            lastTvdSectionPath = currentTvdSectionPath;
        }
    }

    private static void occupyTvdSectionPath(Simulation sim, TVDSectionPath tvdSectionPath) throws SimulationError {
        var tvdSection = sim.infraState.getTvdSectionState(tvdSectionPath.tvdSection.index);
        tvdSection.occupy(sim);
    }

    private static void freeTvdSectionPath(Simulation sim, TVDSectionPath tvdSectionPath) throws SimulationError {
        var tvdSection = sim.infraState.getTvdSectionState(tvdSectionPath.tvdSection.index);
        tvdSection.free(sim);
    }
}
