package fr.sncf.osrd.infra.waypointgraph;

import fr.sncf.osrd.infra.TVDSection;
import fr.sncf.osrd.utils.SortedArraySet;
import fr.sncf.osrd.utils.graph.BiNEdge;
import fr.sncf.osrd.utils.graph.EdgeDirection;
import fr.sncf.osrd.utils.graph.EdgeEndpoint;

public class TVDSectionPath extends BiNEdge<TVDSectionPath> {
    /** The direction to the inside of the TVDSection from the start node, relative to the TrackSection */
    public final EdgeDirection startNodeDirection;
    /** The direction to the inside of the TVDSection from the end node, relative to the TrackSection */
    public final EdgeDirection endNodeDirection;
    public final SortedArraySet<TVDSection> tvdSections = new SortedArraySet<>();

    public EdgeDirection nodeDirection(EdgeEndpoint endpoint) {
        return endpoint == EdgeEndpoint.BEGIN ? startNodeDirection : endNodeDirection;
    }

    TVDSectionPath(
            WaypointGraph graph,
            int startNode,
            EdgeDirection startNodeDirection,
            int endNode,
            EdgeDirection endNodeDirection,
            double length
    ) {
        super(graph.nextEdgeIndex(), startNode, endNode, length);
        graph.registerEdge(this);
        this.startNodeDirection = startNodeDirection;
        this.endNodeDirection = endNodeDirection;
    }
}