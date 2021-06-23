package fr.sncf.osrd.infra_state;

import java.util.ArrayList;

import fr.sncf.osrd.TrainSchedule;
import fr.sncf.osrd.infra.trackgraph.Switch;
import fr.sncf.osrd.simulation.Simulation;
import fr.sncf.osrd.simulation.SimulationError;

import java.util.HashMap;

public class SwitchPost {

    private int postId;
    private SuccessionTable[] table;

    public SwitchPost(
        int postId,
        SuccessionTable[] table
    ) {
        this.postId = postId;
        this.table = table;
    }

    public SuccessionTable getSuccessionTable(Switch s) {
        return table[s.switchIndex];
    }

    public boolean request(
            Simulation sim,
            RouteState routeState,
            TrainSchedule train
    ) throws SimulationError {
        if (routeState.status != RouteStatus.FREE) {
            return false;
        }
        for (var switchEntry : routeState.route.switchesPosition.entrySet()) {
            var table = getSuccessionTable(switchEntry.getKey());
            if (!table.isPlanned(train)) {
                table.add(train);
            }
            if (!table.isNext(train)) {
                return false;
            }
        }
        
        routeState.reserve(sim);
        for (var switchEntry : routeState.route.switchesPosition.entrySet()) {
            getSuccessionTable(switchEntry.getKey()).next();
        }
        return true;
    }

    public static final class SuccessionTable {
        
        private int current;
        private ArrayList<TrainSchedule> succession;
        private HashMap<String, Integer> countTrain;
    
        public SuccessionTable() {
            this.current = 0;
            this.succession = new ArrayList<TrainSchedule>();
            this.countTrain = new HashMap<String, Integer>();
        }
        
        public SuccessionTable(ArrayList<TrainSchedule> succession) {
            this.current = 0;
            this.succession = succession;
            for (var train : succession) {
                var trainId = train.trainID;
                if (!countTrain.containsKey(trainId)) {
                    countTrain.put(trainId, 0);
                }
                countTrain.replace(trainId, countTrain.get(trainId) + 1);
            }
        }
        
        public boolean isNext(TrainSchedule train) {
            return train.trainID.equals(succession.get(current).trainID);
        }

        public void next() {
            var trainId = succession.get(current).trainID;
            countTrain.replace(trainId, countTrain.get(trainId) - 1);
            current++;
        }

        public boolean isPlanned(TrainSchedule train) {
            var trainId = train.trainID;
            return countTrain.containsKey(trainId) && countTrain.get(trainId) > 0;
        }
        
        public void add(TrainSchedule train) {
            succession.add(train);
            var trainId = train.trainID;
            if (!countTrain.containsKey(trainId)) {
                countTrain.put(trainId, 0);
            }
            countTrain.replace(trainId, countTrain.get(trainId) + 1);
        }
    }
}