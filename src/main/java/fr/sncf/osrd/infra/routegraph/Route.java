package fr.sncf.osrd.infra.routegraph;

import fr.sncf.osrd.infra.TVDSection;
import fr.sncf.osrd.infra.signaling.Signal;
import fr.sncf.osrd.infra.trackgraph.Switch;
import fr.sncf.osrd.infra.trackgraph.SwitchPosition;
import fr.sncf.osrd.infra.waypointgraph.TVDSectionPath;
import fr.sncf.osrd.train.TrackSectionRange;
import fr.sncf.osrd.utils.SortedArraySet;
import fr.sncf.osrd.utils.TrackSectionLocation;
import fr.sncf.osrd.utils.graph.DirNEdge;
import fr.sncf.osrd.utils.graph.EdgeDirection;

import java.util.*;

public class Route extends DirNEdge {
    public final String id;
    /** List of tvdSectionPath forming the route */
    public final List<TVDSectionPath> tvdSectionsPaths;
    public final List<EdgeDirection> tvdSectionsPathDirections;
    public final List<SortedArraySet<TVDSection>> releaseGroups;
    public final HashMap<Switch, SwitchPosition> switchesPosition;
    public final List<Signal> signalsWithEntry;
    public ArrayList<Signal> signalSubscribers;

    Route(
            String id,
            RouteGraph graph,
            double length,
            List<SortedArraySet<TVDSection>> releaseGroups,
            List<TVDSectionPath> tvdSectionsPaths,
            List<EdgeDirection> tvdSectionsPathDirections,
            HashMap<Switch, SwitchPosition> switchesPosition,
            Signal entrySignal, List<Signal> signals) {
        super(
                graph.nextEdgeIndex(),
                tvdSectionsPaths.get(0).getStartNode(tvdSectionsPathDirections.get(0)),
                tvdSectionsPaths.get(tvdSectionsPaths.size() - 1).getEndNode(
                        tvdSectionsPathDirections.get(tvdSectionsPaths.size() - 1)),
                length
        );
        this.id = id;
        this.releaseGroups = releaseGroups;
        this.tvdSectionsPathDirections = tvdSectionsPathDirections;
        this.switchesPosition = switchesPosition;
        graph.registerEdge(this);
        this.tvdSectionsPaths = tvdSectionsPaths;
        this.signalSubscribers = new ArrayList<>();

        signalsWithEntry = new ArrayList<>();
        if (entrySignal != null)
            signalsWithEntry.add(entrySignal);
        signalsWithEntry.addAll(signals);
    }

    /** Build track section path. Need to concatenate all track section of all TvdSectionPath.
     * Avoid to have in the path TrackSectionPositions that reference the same TrackSection. */
    public static ArrayList<TrackSectionRange> routesToTrackSectionRange(
            List<Route> routePath,
            TrackSectionLocation beginLocation,
            TrackSectionLocation endLocation
    ) {
        // Flatten the list of track section range
        var flattenSections = new ArrayDeque<TrackSectionRange>();
        for (var route : routePath) {
            for (var i = 0; i < route.tvdSectionsPaths.size(); i++) {
                var tvdSectionPath = route.tvdSectionsPaths.get(i);
                var tvdSectionPathDir = route.tvdSectionsPathDirections.get(i);
                for (var trackSection : tvdSectionPath.getTrackSections(tvdSectionPathDir))
                    flattenSections.addLast(trackSection);
            }
        }

        // Drop first track sections until the begin location
        while (true) {
            if (flattenSections.isEmpty())
                throw new RuntimeException("Begin position not contained in the route path");
            var firstTrack = flattenSections.removeFirst();
            if (firstTrack.containsLocation(beginLocation)) {
                var newTrackSection = new TrackSectionRange(firstTrack.edge, firstTrack.direction,
                        beginLocation.offset, firstTrack.getEndPosition());
                flattenSections.addFirst(newTrackSection);
                break;
            }
        }

        // Drop lasts track sections until the end location
        while (true) {
            if (flattenSections.isEmpty())
                throw new RuntimeException("End position not contained in the route path");
            var lastTrack = flattenSections.removeLast();
            if (lastTrack.containsLocation(endLocation)) {
                var newTrackSection = new TrackSectionRange(lastTrack.edge, lastTrack.direction,
                        lastTrack.getBeginPosition(), endLocation.offset);
                flattenSections.addLast(newTrackSection);
                break;
            }
        }

        // Merge duplicated edges
        var trackSectionPath = new ArrayList<TrackSectionRange>();
        TrackSectionRange lastTrack = flattenSections.removeFirst();
        while (!flattenSections.isEmpty()) {
            var currentTrack = flattenSections.removeFirst();
            if (lastTrack.edge != currentTrack.edge) {
                trackSectionPath.add(lastTrack);
                lastTrack = currentTrack;
                continue;
            }
            lastTrack = TrackSectionRange.merge(lastTrack, currentTrack);
        }
        trackSectionPath.add(lastTrack);
        return trackSectionPath;
    }
}
