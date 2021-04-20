package fr.sncf.osrd.api;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fr.sncf.osrd.infra.Infra;
import fr.sncf.osrd.infra.routegraph.Route;
import fr.sncf.osrd.infra.routegraph.RouteLocation;
import fr.sncf.osrd.infra.trackgraph.TrackSection;
import fr.sncf.osrd.utils.graph.BiDijkstra;
import fr.sncf.osrd.utils.graph.DistCostFunction;
import fr.sncf.osrd.utils.graph.EdgeDirection;
import fr.sncf.osrd.utils.graph.path.BasicDirPathNode;
import fr.sncf.osrd.utils.graph.path.FullPathArray;
import org.takes.Request;
import org.takes.Response;
import org.takes.rs.RsJson;
import org.takes.rs.RsText;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;

import java.io.IOException;
import java.util.ArrayList;

public class PathfindingTracksEndpoint extends PathfindingEndpoint {
    public static final JsonAdapter<TrackSectionRangeResult[][]> adapterResult = new Moshi
            .Builder()
            .build()
            .adapter(TrackSectionRangeResult[][].class)
            .failOnUnknown();


    public PathfindingTracksEndpoint(Infra infra) {
        super(infra);
    }

    @Override
    public Response act(Request req) throws IOException {
        var buffer = new okio.Buffer();
        buffer.write(req.body().readAllBytes());
        var jsonRequest = adapterRequest.fromJson(buffer);
        if (jsonRequest == null)
            return new RsWithStatus(new RsText("missing request body"), 400);

        var reqWaypoints = jsonRequest.waypoints;

        // parse the waypoints
        @SuppressWarnings({"unchecked", "rawtypes"})
        var waypoints = (ArrayList<BasicDirPathNode<TrackSection>>[]) new ArrayList[reqWaypoints.length];
        for (int i = 0; i < waypoints.length; i++) {
            var stopWaypoints = new ArrayList<BasicDirPathNode<TrackSection>>();
            for (var stopWaypoint : reqWaypoints[i]) {
                var edge = infra.trackGraph.trackSectionMap.get(stopWaypoint.trackSection);
                stopWaypoints.add(new BasicDirPathNode<>(edge, stopWaypoint.offset, stopWaypoint.direction));
            }
            waypoints[i] = stopWaypoints;
        }

        var costFunction = new DistCostFunction<TrackSection>();
        var candidatePaths = BiDijkstra.<TrackSection>makePriorityQueue();
        candidatePaths.addAll(waypoints[0]);

        var pathsToGoal = new ArrayList<BasicDirPathNode<TrackSection>>();

        // Compute the paths from the entry waypoint to the exit waypoint
        for (int i = 1; i < waypoints.length; i++) {
            var destinationWaypoints = waypoints[i];

            BiDijkstra.findPaths(
                    infra.trackGraph,
                    candidatePaths,
                    costFunction,
                    (pathNode) -> {
                        for (var goalEdge : destinationWaypoints) {
                            if (goalEdge.edge != pathNode.edge)
                                continue;
                            var addedCost = costFunction.evaluate(
                                    goalEdge.edge,
                                    pathNode.position,
                                    goalEdge.edge.length
                            );
                            return pathNode.end(addedCost, goalEdge.edge, goalEdge.position, goalEdge.direction);
                        }
                        return null;
                    },
                    (pathToGoal) -> {
                        pathsToGoal.add(pathToGoal);
                        return false;
                    });

            candidatePaths.clear();
            candidatePaths.add(pathsToGoal.get(pathsToGoal.size() - 1));
        }

        var result = new TrackSectionRangeResult[reqWaypoints.length - 1][];

        for (int i = 0; i < pathsToGoal.size(); i++) {
            var path = FullPathArray.from(pathsToGoal.get(i));
            result[i] = fullPathToTrackSectionRange(path);
        }
        return new RsJson(new RsWithBody(adapterResult.toJson(result)));
    }

    @SuppressFBWarnings({"BC_UNCONFIRMED_CAST"})
    private TrackSectionRangeResult[] fullPathToTrackSectionRange(
            FullPathArray<TrackSection, BasicDirPathNode<TrackSection>> path
    ) {
        var result = new ArrayList<TrackSectionRangeResult>();
        for (int i = 0; i < path.pathNodes.size() - 2; i++) {
            var node = path.pathNodes.get(i);
            if (node.direction == EdgeDirection.START_TO_STOP)
                result.add(new TrackSectionRangeResult(node.edge.id, node.position, node.edge.length));
            else
                result.add(new TrackSectionRangeResult(node.edge.id, node.position, 0));
        }
        var lastNode = path.pathNodes.get(path.pathNodes.size() - 1);
        var secondLastNode = path.pathNodes.get(path.pathNodes.size() - 2);
        assert lastNode.edge == secondLastNode.edge;
        result.add(new TrackSectionRangeResult(lastNode.edge.id, secondLastNode.position, lastNode.position));
        return result.toArray(new TrackSectionRangeResult[result.size()]);
    }
}