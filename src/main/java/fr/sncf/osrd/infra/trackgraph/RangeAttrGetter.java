package fr.sncf.osrd.infra.trackgraph;

import fr.sncf.osrd.infra.graph.EdgeDirection;
import fr.sncf.osrd.util.RangeSequence;

@FunctionalInterface
public interface RangeAttrGetter<ValueT> {
    RangeSequence<ValueT> getAttr(TrackSection edge, EdgeDirection dir);
}