package fr.sncf.osrd.speedcontroller.generators;

import fr.sncf.osrd.TrainSchedule;
import fr.sncf.osrd.infra.trackgraph.TrackSection;
import fr.sncf.osrd.simulation.Simulation;
import fr.sncf.osrd.speedcontroller.LimitAnnounceSpeedController;
import fr.sncf.osrd.speedcontroller.MaxSpeedController;
import fr.sncf.osrd.speedcontroller.SpeedController;
import fr.sncf.osrd.train.TrackSectionRange;
import fr.sncf.osrd.utils.graph.EdgeDirection;

import java.util.HashSet;
import java.util.Set;

/** This is a SpeedControllerGenerator that generates the maximum allowed speed at any given point. */
public class MaxSpeedGenerator extends SpeedControllerGenerator {
    public MaxSpeedGenerator() {
        super(null);
    }

    @Override
    public Set<SpeedController> generate(Simulation sim, TrainSchedule schedule, Set<SpeedController> maxSpeed,
                                         double initialSpeed) {
        // the path is computed at the beginning of the simulation, as it is (for now) part of the event
        var trainPath = schedule.fullPath;
        var rollingStock = schedule.rollingStock;

        var controllers = new HashSet<SpeedController>();

        // add a limit for the maximum speed the hardware is rated for
        controllers.add(new MaxSpeedController(
                rollingStock.maxSpeed,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY
        ));

        var offset = 0;
        for (var trackSectionRange : trainPath) {
            var edge = trackSectionRange.edge;
            var absBegin = Double.min(trackSectionRange.getBeginPosition(), trackSectionRange.getEndPosition());
            var absEnd = Double.max(trackSectionRange.getBeginPosition(), trackSectionRange.getEndPosition());
            for (var speedRange : TrackSection.getSpeedSections(edge, trackSectionRange.direction)) {
                var speedSection = speedRange.value;
                var speedTrackRange = new TrackSectionRange(
                        edge,
                        EdgeDirection.START_TO_STOP,
                        Double.max(speedRange.begin, absBegin),
                        Double.min(speedRange.end, absEnd)
                );
                if (trackSectionRange.direction == EdgeDirection.STOP_TO_START)
                    speedTrackRange = speedTrackRange.opposite();

                // ignore the speed limit if it doesn't apply to our train
                if (!speedSection.isValidFor(rollingStock))
                    continue;

                // compute where this limit is active from and to
                var begin = offset + speedTrackRange.getBeginPosition() - trackSectionRange.getBeginPosition();
                var end = begin + speedTrackRange.length();

                // Add the speed controller corresponding to the approach to the restricted speed section
                controllers.add(LimitAnnounceSpeedController.create(
                        rollingStock.maxSpeed,
                        speedSection.speedLimit,
                        begin,
                        rollingStock.timetableGamma
                ));

                // Add the speed controller corresponding to the restricted speed section
                controllers.add(new MaxSpeedController(speedSection.speedLimit, begin, end));
            }
            offset += trackSectionRange.length();
        }

        // Add the speed controller corresponding to the end of the path
        controllers.add(LimitAnnounceSpeedController.create(
                rollingStock.maxSpeed,
                0,
                offset - 5,
                rollingStock.timetableGamma
        ));
        controllers.add(new MaxSpeedController(0, offset - 5, Double.POSITIVE_INFINITY));
        return controllers;
    }
}
