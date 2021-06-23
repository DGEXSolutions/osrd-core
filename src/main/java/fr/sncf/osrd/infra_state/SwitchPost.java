package fr.sncf.osrd.infra_state;

import fr.sncf.osrd.SuccessionTable;
import fr.sncf.osrd.train.Train;
import fr.sncf.osrd.simulation.Simulation;
import fr.sncf.osrd.simulation.SimulationError;

import java.util.List;
import java.util.HashMap;

public class SwitchPost {

    private HashMap<String, SuccessionTable> tables;
    private HashMap<String, Integer> currentIndex;
    private HashMap<String, HashMap<String, Integer>> occurences;

    public SwitchPost() {
        tables = null;
    }

    public void init(List<SuccessionTable> initTables)
    {
        this.tables = new HashMap<String, SuccessionTable>();
        this.currentIndex = new HashMap<String, Integer>();
        this.occurences = new HashMap<String, HashMap<String, Integer>>();
        for (var table : initTables) {
            this.tables.put(table.switchID, table.clone());
            this.currentIndex.put(table.switchID, 0);
            this.occurences.put(table.switchID, new HashMap<String, Integer>());
            for (var trainID : table.table) {
                this.plan(table.switchID, trainID);
            }
        }
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

    public boolean request(
        Simulation sim,
        RouteState routeState,
        Train train) throws SimulationError {
            if (routeState.status != RouteStatus.FREE) {
                return false;
            }
            var trainID = train.schedule.trainID;
            for (var switchEntry : routeState.route.switchesPosition.entrySet()) {
                var switchId = switchEntry.getKey().id;
                if (!this.isPlanned(switchId, trainID)) {
                    this.plan(switchId, trainID);
                }
                if (!this.isNext(switchId, trainID)) {
                    return false;
                }
            }

            routeState.reserve(sim);

            for (var switchEntry : routeState.route.switchesPosition.entrySet()) {
                this.next(switchEntry.getKey().id);
            }

            return true;
    }
}