package fr.sncf.osrd.railjson.schema.infra.signaling;

import com.squareup.moshi.Json;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import fr.sncf.osrd.infra.signaling.AspectConstraint;
import fr.sncf.osrd.infra.signaling.AspectConstraint.ConstraintPosition.Element;

public abstract class RJSAspectConstraint {
    public static class ConstraintPosition {
        public final double offset;
        public final RJSElement element;

        public enum RJSElement {
            CURRENT_SIGNAL,
            NEXT_SIGNAL,
            END
        }

        public ConstraintPosition(double offset, RJSElement element) {
            this.offset = offset;
            this.element = element;
        }

        AspectConstraint.ConstraintPosition parse() {
            switch (element) {
                case CURRENT_SIGNAL:
                    return new AspectConstraint.ConstraintPosition(offset, Element.CURRENT_SIGNAL);
                case END:
                    return new AspectConstraint.ConstraintPosition(offset, Element.END);
                default:
                    return new AspectConstraint.ConstraintPosition(offset, Element.NEXT_SIGNAL);
            }
        }
    }

    public static final PolymorphicJsonAdapterFactory<RJSAspectConstraint> adapter = (
            PolymorphicJsonAdapterFactory.of(RJSAspectConstraint.class, "type")
                    .withSubtype(SpeedLimit.class, "speed_limit")
    );

    public abstract AspectConstraint parse();

    public static class SpeedLimit extends RJSAspectConstraint {
        public final double speed;
        @Json(name = "applies_at")
        public final ConstraintPosition appliesAt;
        public final ConstraintPosition until;

        SpeedLimit(double speed, ConstraintPosition appliesAt, ConstraintPosition until) {
            this.speed = speed;
            this.appliesAt = appliesAt;
            this.until = until;
        }

        @Override
        public AspectConstraint parse() {
            return new AspectConstraint.SpeedLimit(speed, appliesAt.parse(), until.parse());
        }
    }
}
