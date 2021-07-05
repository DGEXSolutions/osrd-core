package fr.sncf.osrd.infra_state;

import fr.sncf.osrd.SuccessionTable;
import fr.sncf.osrd.infra.TVDSection;
import fr.sncf.osrd.infra.routegraph.Route;
import fr.sncf.osrd.train.Train;
import fr.sncf.osrd.train.TrainStatus;
import fr.sncf.osrd.simulation.Simulation;
import fr.sncf.osrd.simulation.SimulationError;
import fr.sncf.osrd.infra.Infra;

import java.util.List;
import java.util.HashMap;
import java.util.HashSet;

public class SwitchPost {

    private HashMap<String, SuccessionTable> tables;
    private HashMap<String, Integer> currentIndex;
    private HashMap<String, HashMap<String, Integer>> occurences;
    private HashMap<String, HashSet<Request>> waitingList;
    private HashMap<String, String> lastRequestedRoute;
    private HashMap<String, String> currentTrainAllowed;

    public SwitchPost() {
        tables = null;
    }

    public void init(Infra infra, List<SuccessionTable> initTables)
    {
        // build succession tables from initTables
        tables = new HashMap<String, SuccessionTable>();
        currentIndex = new HashMap<String, Integer>();
        occurences = new HashMap<String, HashMap<String, Integer>>();
        for (var table : initTables) {
            tables.put(table.switchID, table.clone());
            currentIndex.put(table.switchID, 0);
            occurences.put(table.switchID, new HashMap<String, Integer>());
            for (var trainID : table.table) {
                plan(table.switchID, trainID);
            }
        }

        // build waiting list for each TVDSection
        waitingList = new HashMap<String, HashSet<Request>>();
        for (var tvdSection : infra.tvdSections.values()) {                
            waitingList.put(tvdSection.id, new HashSet<Request>());
        }

        // build last request table for train
        lastRequestedRoute = new HashMap<String, String>();
        currentTrainAllowed = new HashMap<String, String>();
    }

    public boolean isCurrentAllowed(String switchID, String trainID) {
        return currentTrainAllowed.containsKey(trainID) && currentTrainAllowed.get(switchID).equals(trainID);
    }

    private boolean isPlanned(String switchID, String trainID) {
        assert tables.containsKey(switchID);
        return occurences.get(switchID).containsKey(trainID) && occurences.get(switchID).get(trainID) > 0;
    }

    private boolean isNext(String switchID, String trainID) {
        assert tables.containsKey(switchID);
        var index = currentIndex.get(switchID);
        return tables.get(switchID).table.get(index).equals(trainID);
    }

    private void plan(String switchID, String trainID) {
        assert tables.containsKey(switchID);
        tables.get(switchID).table.add(trainID);
        var count = occurences.get(switchID).containsKey(trainID)? occurences.get(switchID).get(trainID) : 0;
        occurences.get(switchID).put(trainID, count + 1);
    }

    private void next(String switchID) {
        assert tables.containsKey(switchID);
        var index = currentIndex.get(switchID);
        var trainID = tables.get(switchID).table.get(index);
        var count = occurences.get(switchID).get(trainID);
        occurences.get(switchID).put(trainID, count - 1);
        currentIndex.put(switchID, index + 1);
    }

    public void process(
        Simulation sim,
        Request request
    ) throws SimulationError {

        var trainID = request.train.schedule.trainID;

        // check if the route is free
        for (var tvdSectionPath : request.routeState.route.tvdSectionsPaths) {
            var tvdSectionIndex = tvdSectionPath.tvdSection.index;
            if (sim.infraState.getTvdSectionState(tvdSectionIndex).isReserved()) {
                return;
            }
        }
        // check if the train is next on each switch of the route
        for (var s : request.routeState.route.switchesPosition.keySet()) {
            if (!isPlanned(s.id, trainID)) { // plan the train if not planned
                plan(s.id, trainID);
            }
            if (!isNext(s.id, trainID)) { // check if next
                return;
            }
        }

        // erase the request of the waiting list of each tvd section of the route
        for (var tvdSectionPath : request.routeState.route.tvdSectionsPaths) {
            waitingList.get(tvdSectionPath.tvdSection.id).remove(request);
        }

        // go to next train to each switch of the route
        for (var s : request.routeState.route.switchesPosition.keySet()) {
            next(s.id);
            currentTrainAllowed.put(s.id, request.train.schedule.trainID);
        }

        // reserve the route
        request.routeState.reserve(sim);
    }

    public void request(
            Simulation sim,
            RouteState routeState,
            Train train
    ) throws SimulationError {
        var trainID = train.schedule.trainID;
        if (!lastRequestedRoute.containsKey(trainID)
        || !lastRequestedRoute.get(trainID).equals(routeState.route.id)) {
            lastRequestedRoute.put(trainID, routeState.route.id);

            var request = new Request(train, routeState);
            for (var tvdSectionPath : routeState.route.tvdSectionsPaths) {
                var tvdSectionID = tvdSectionPath.tvdSection.id;
                waitingList.get(tvdSectionID).add(request);
            }
            process(sim, request);
        }
    }

    @SuppressWarnings("unchecked")
    public void notifyFreed(
        Simulation sim,
        TVDSection tvdSection
    ) throws SimulationError {
        var list = (HashSet<Request>)waitingList.get(tvdSection.id).clone();
        for (var request : list) {
            process(sim, request);
        }
    }


    private class Request {
        public Train train;
        public RouteState routeState;

        public Request(Train train, RouteState routeState) {
            this.train = train;
            this.routeState = routeState;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Request)) {
                return false;
            }
            var request = (Request)object;
            return train.schedule.trainID.equals(request.train.schedule.trainID)
            && routeState.route.id.equals(request.routeState.route.id);
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return train.schedule.trainID + "#" + routeState.route.id;
        }
    }
}
