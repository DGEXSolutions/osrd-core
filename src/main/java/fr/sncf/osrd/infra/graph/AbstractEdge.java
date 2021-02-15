package fr.sncf.osrd.infra.graph;

import fr.sncf.osrd.util.CryoList;
import fr.sncf.osrd.util.Freezable;
import fr.sncf.osrd.util.Indexable;

import java.util.List;

public abstract class AbstractEdge<NodeT extends AbstractNode<?>> implements Indexable, Freezable {
    public final int startNode;
    public final int endNode;
    public final double length;

    public final CryoList<AbstractEdge<NodeT>> startNeighbors = new CryoList<>();
    public final CryoList<AbstractEdge<NodeT>> endNeighbors = new CryoList<>();

    private boolean frozen = false;

    /**
     * Given a side of the edge, return the list of neighbors
     * @param endpoint the end of the edge to consider
     * @return the list of neighbors at this end
     */
    public List<AbstractEdge<NodeT>> getNeighbors(EdgeEndpoint endpoint) {
        if (endpoint == EdgeEndpoint.BEGIN)
            return startNeighbors;
        return endNeighbors;
    }

    /**
     * The list of reachable edges at the start of the course over the edge.
     * @param dir the course direction
     * @return the list of reachable edges at the start of the course over the edge
     */
    @SuppressWarnings("unused")
    public List<AbstractEdge<NodeT>> getStartNeighbors(EdgeDirection dir) {
        if (dir == EdgeDirection.START_TO_STOP)
            return startNeighbors;
        return endNeighbors;
    }

    /**
     * The node at the end of the course over the edge.
     * @param dir the course direction
     * @return The node at the end of the course over the edge.
     */
    public int getEndNode(EdgeDirection dir) {
        if (dir == EdgeDirection.START_TO_STOP)
            return endNode;
        return startNode;
    }

    /**
     * The node at the start of the course over the edge.
     * @param dir the course direction
     * @return The node at the start of the course over the edge.
     */
    @SuppressWarnings("unused")
    public int getStartNode(EdgeDirection dir) {
        if (dir == EdgeDirection.START_TO_STOP)
            return startNode;
        return endNode;
    }

    /**
     * The list of reachable edges at the end of the course over the edge.
     * @param dir the course direction
     * @return the list of reachable edges at the end of the course over the edge
     */
    public List<AbstractEdge<NodeT>> getEndNeighbors(EdgeDirection dir) {
        if (dir == EdgeDirection.START_TO_STOP)
            return endNeighbors;
        return startNeighbors;
    }

    private int index = -1;

    @Override
    public void setIndex(int index) {
        assert this.index == -1;
        this.index = index;
    }

    @Override
    public int getIndex() {
        assert index != -1;
        return index;
    }

    @Override
    public void freeze() {
        assert !frozen;
        startNeighbors.freeze();
        endNeighbors.freeze();
        frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    protected AbstractEdge(int startNode, int endNode, double length) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.length = length;
    }
}
