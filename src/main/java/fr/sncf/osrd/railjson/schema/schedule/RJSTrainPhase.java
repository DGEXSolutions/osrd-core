package fr.sncf.osrd.railjson.schema.schedule;

import com.squareup.moshi.Json;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import fr.sncf.osrd.railjson.schema.common.ID;
import fr.sncf.osrd.railjson.schema.common.RJSTrackLocation;
import fr.sncf.osrd.railjson.schema.infra.RJSRoute;

/**
 * <p>A train phase corresponds to an operation during the lifetime of a scheduled train.
 * It could be a stop, a maneuver, or a navigation phase.</p>
 *
 * <p>The location of the train when entering a phase is known from the previous phase,
 * and the phase exit state is up to the phase.</p>
 */
public abstract class RJSTrainPhase {
    public static final PolymorphicJsonAdapterFactory<RJSTrainPhase> adapter = (
            PolymorphicJsonAdapterFactory.of(RJSTrainPhase.class, "type")
                    .withSubtype(RJSTrainPhase.Navigate.class, "navigate")
                    .withSubtype(RJSTrainPhase.Stop.class, "stop")
    );

    /** What generator to use to generate the target speed */
    public RJSAllowance[] allowances;

    /** The location of the head of the train when it exits this phase */
    @Json(name = "end_location")
    public RJSTrackLocation endLocation;

    public static final class Navigate extends RJSTrainPhase {
        /** The sequence of routes the train should take.
         * The train must be on the first route when it enters this phase. */
        public ID<RJSRoute>[] routes;

        /** The distance at which the driver can see objects on the tracks */
        @Json(name = "driver_sight_distance")
        public double driverSightDistance;

        /** Create a navigation phase */
        public Navigate(ID<RJSRoute>[] routes, RJSTrackLocation endLocation, double driverSightDistance) {
            this.routes = routes;
            this.endLocation = endLocation;
            this.driverSightDistance = driverSightDistance;
        }

        /** Create an uninitialized navigation phase */
        public Navigate() {
            this.routes = null;
            this.endLocation = null;
            this.driverSightDistance = Double.NaN;
        }
    }

    public static final class Stop extends RJSTrainPhase {
        /** The duration of the stop */
        public double duration;

        public Stop(double duration) {
            this.duration = duration;
        }
    }
}
