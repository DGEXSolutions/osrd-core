package fr.sncf.osrd.infra.parsing.railml;

import fr.sncf.osrd.infra.topological.TopoEdge;
import fr.sncf.osrd.util.FloatCompare;
import fr.sncf.osrd.util.MutPair;
import fr.sncf.osrd.util.Pair;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class NetElement {
    final TopoEdge topoEdge;
    final ArrayList<NetElement> children;
    final Map<String, Double> lrsDeltas;

    /* constructor for netElement which contains elementCollectionUnordered */
    NetElement(Node netElement, Map<String, NetElement> netElementMap) {
        topoEdge = null;
        children = new ArrayList<>();
        lrsDeltas = null;
        parseChildren(netElement, netElementMap);
    }

    /* constructor for netElement which have a length but no elementCollectionUnordered */
    NetElement(TopoEdge topoEdge, Node netElement) {
        this.topoEdge = topoEdge;
        children = null;
        lrsDeltas = new HashMap<>();
        parseLrs(netElement);
    }

    private void parseChildren(Node netElement, Map<String, NetElement> netElementMap) {
        for (var elementPart : netElement.selectNodes("elementCollectionUnordered/elementPart")) {
            var ref = elementPart.valueOf("@ref");
            children.add(netElementMap.get(ref));
        }
    }

    private void parseLrs(Node netElement) {
        var lrsMap = new HashMap<String, MutPair<Double, Double>>();

        for (var intrinsicCoordinate: netElement.selectNodes("associatedPositioningSystem/intrinsicCoordinate")) {
            var intrinsicCoord = Double.valueOf(intrinsicCoordinate.valueOf("@intrinsicCoord"));
            assert FloatCompare.eq(intrinsicCoord, 0) || FloatCompare.eq(intrinsicCoord, 1);
            var positioningSystemRef = intrinsicCoordinate.valueOf("linearCoordinate/@positioningSystemRef");
            if (positioningSystemRef.isEmpty())
                continue;
            var measure = Double.valueOf(intrinsicCoordinate.valueOf("linearCoordinate/@measure"));
            lrsMap.putIfAbsent(positioningSystemRef, new MutPair<>(Double.NaN, Double.NaN));
            if (FloatCompare.eq(intrinsicCoord, 0))
                lrsMap.get(positioningSystemRef).first = measure;
            else
                lrsMap.get(positioningSystemRef).second = measure;
        }

        for (var entry : lrsMap.entrySet()) {
            var range = entry.getValue();
            assert FloatCompare.eq(range.second - range.first, topoEdge.length);
            lrsDeltas.put(entry.getKey(), range.first);
        }
    }

    public ArrayList<Pair<TopoEdge, Double>> placeOn(String lrsId, double measure) {
        var list = new ArrayList<Pair<TopoEdge, Double>>();
        if (topoEdge == null) {
            for (var child : children) {
                list.addAll(child.placeOn(lrsId, measure));
            }
            return list;
        }

        if (!lrsDeltas.containsKey(lrsId))
            return list;
        double position = measure - lrsDeltas.get(lrsId);
        if (position < 0 || position > topoEdge.length)
            return list;
        list.add(new Pair<>(topoEdge, position));
        return list;
    }
}