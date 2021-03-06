package fr.sncf.osrd.railjson.schema.infra.trackobjects;

import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import fr.sncf.osrd.railjson.schema.common.Identified;
import fr.sncf.osrd.utils.graph.ApplicableDirection;
import fr.sncf.osrd.utils.graph.IPointValue;

public abstract class RJSRouteWaypoint
        extends DirectionalRJSTrackObject
        implements Identified, IPointValue<RJSRouteWaypoint> {
    public String id;

    public static final PolymorphicJsonAdapterFactory<RJSRouteWaypoint> adapter =
            PolymorphicJsonAdapterFactory.of(RJSRouteWaypoint.class, "type")
                    .withSubtype(RJSTrainDetector.class, "detector")
                    .withSubtype(RJSBufferStop.class, "buffer_stop");

    RJSRouteWaypoint(String id, ApplicableDirection applicableDirection, double position) {
        super(applicableDirection, position);
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public RJSRouteWaypoint getValue() {
        return this;
    }
}