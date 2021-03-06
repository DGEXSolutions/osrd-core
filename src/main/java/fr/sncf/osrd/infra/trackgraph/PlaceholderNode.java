package fr.sncf.osrd.infra.trackgraph;

import fr.sncf.osrd.utils.graph.BiGraph;

/**
 * A placeholder node type, without any special purpose.
 * Its list of neighbors is held by {@link BiGraph}.
 */
public class PlaceholderNode extends TrackNode {
    PlaceholderNode(TrackGraph graph, int index, String id) {
        super(index, id);
        graph.registerNode(this);
    }
}
