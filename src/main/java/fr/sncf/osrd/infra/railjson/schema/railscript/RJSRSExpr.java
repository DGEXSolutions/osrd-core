package fr.sncf.osrd.infra.railjson.schema.railscript;

import com.squareup.moshi.Json;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fr.sncf.osrd.infra.railjson.schema.ID;
import fr.sncf.osrd.infra.railjson.schema.RJSRoute;
import fr.sncf.osrd.infra.railjson.schema.RJSSwitch;
import fr.sncf.osrd.infra.railjson.schema.signaling.RJSAspect;
import fr.sncf.osrd.infra.railjson.schema.trackobjects.RJSSignal;

import java.util.Map;

/**
 * <h1>SignalScript</h1>
 * <p>SignalScript has support for:</p>
 * <ul>
 *     <li>
 *         <p>data types:</p>
 *         <ul>
 *             <li>boolean</li>
 *             <li>AspectSet</li>
 *             <li>Signal (matchable)</li>
 *             <li>Route (matchable)</li>
 *             <li>Switch (matchable)</li>
 *         </ul>
 *     </li>
 *     <li>functions and function calls, in a strictly non recursive fashion
 *         (functions can only call already declared functions)</li>
 *     <li>purely functional conditions (mandatory else)</li>
 *     <li>a set comprehension construct for building AspectSet</li>
 *     <li>pattern matching on enum values</li>
 * </ul>
 *
 * <p>Some caveats:</p>
 * <ul>
 *     <li>no loops</li>
 *     <li>no variables</li>
 *     <li>no recursion</li>
 * </ul>
 *
 * <h2>Source code example</h2>
 * <b>No parser for this language is planned yet, it's only there to help understand things better</b>
 *
 * <pre>
 * {@code
 *
 * fn sncf_filter(aspects: AspectSet) -> AspectSet {
 *     if aspect_set_contains(aspects, RED) {
 *         AspectSet{RED}
 *     } else if aspect_set_contains(aspects, YELLOW) {
 *         AspectSet{YELLOW}
 *     } else {
 *         aspects
 *     }
 * }
 *
 * fn bal3_line_signal(
 *         master_signal: Signal,
 *         route: Route,
 * ) -> AspectSet {
 *     sncf_filter(AspectSet{
 *         RED if route_has_state(route, OCCUPIED),
 *         YELLOW if signal_has_aspect(master_signal, RED),
 *         GREEN
 *     })
 * }
 *
 * fn switch_bal3_signal(
 *         switch: Switch,
 *         left_master_signal: Signal,
 *         right_master_signal: Signal,
 *         left_route: Route,
 *         right_route: Route
 * ) -> AspectSet {
 *     match switch {
 *         LEFT: bal3_line_signal(left_master_signal, left_route)
 *         RIGHT: bal3_line_signal(right_master_signal, right_route)
 *     }
 * }
 *
 * Signal {
 *     expr: bal3_line_signal(master_signal: "test_signal_foo", route: "test_route_bar")
 * }
 *
 * }
 * </pre>
 */
@SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public abstract class RJSRSExpr {
    public static final PolymorphicJsonAdapterFactory<RJSRSExpr> adapter = (
            PolymorphicJsonAdapterFactory.of(RJSRSExpr.class, "type")
                    // boolean operators
                    .withSubtype(OrExpr.class, "or")
                    .withSubtype(AndExpr.class, "and")
                    .withSubtype(NotExpr.class, "not")
                    // value constructors
                    .withSubtype(TrueExpr.class, "true")
                    .withSubtype(FalseExpr.class, "false")
                    .withSubtype(AspectSetExpr.class, "aspect_set")
                    .withSubtype(SignalRefExpr.class, "signal")
                    .withSubtype(RouteRefExpr.class, "route")
                    .withSubtype(SwitchRefExpr.class, "switch")
                    // control flow
                    .withSubtype(IfExpr.class, "condition")
                    .withSubtype(CallExpr.class, "call")
                    .withSubtype(EnumMatchExpr.class, "match")
                    // function-specific
                    .withSubtype(ArgumentRefExpr.class, "argument_ref")
                    // primitives
                    .withSubtype(SignalAspectCheckExpr.class, "signal_has_aspect")
                    .withSubtype(RouteStateCheckExpr.class, "route_has_state")
                    .withSubtype(AspectSetContainsExpr.class, "aspect_set_contains")
    );

    // region BOOLEAN_LOGIC

    public abstract static class InfixOpExpr extends RJSRSExpr {
        public final RJSRSExpr[] exprs;

        public InfixOpExpr(RJSRSExpr[] exprs) {
            this.exprs = exprs;
        }
    }

    public static final class OrExpr extends InfixOpExpr {
        public OrExpr(RJSRSExpr[] exprs) {
            super(exprs);
        }
    }

    public static final class AndExpr extends InfixOpExpr {
        public AndExpr(RJSRSExpr[] exprs) {
            super(exprs);
        }
    }

    public static final class NotExpr extends RJSRSExpr {
        public final RJSRSExpr expr;

        public NotExpr(RJSRSExpr expr) {
            this.expr = expr;
        }
    }

    // endregion

    // region VALUE_CONSTRUCTORS

    public static final class TrueExpr extends RJSRSExpr {
        public TrueExpr() {
        }
    }

    public static final class FalseExpr extends RJSRSExpr {
        public FalseExpr() {
        }
    }

    public static final class AspectSetExpr extends RJSRSExpr {
        public static final class AspectSetMember {
            public final ID<RJSAspect> aspect;

            public final RJSRSExpr condition;

            public AspectSetMember(ID<RJSAspect> aspect, RJSRSExpr condition) {
                this.aspect = aspect;
                this.condition = condition;
            }
        }

        public final AspectSetMember[] members;

        public AspectSetExpr(AspectSetMember[] members) {
            this.members = members;
        }
    }

    public static final class SignalRefExpr extends RJSRSExpr {
        public final ID<RJSSignal> signal;

        public SignalRefExpr(ID<RJSSignal> signal) {
            this.signal = signal;
        }
    }

    public static final class RouteRefExpr extends RJSRSExpr {
        public final ID<RJSRoute> route;

        public RouteRefExpr(ID<RJSRoute> route) {
            this.route = route;
        }
    }

    public static final class SwitchRefExpr extends RJSRSExpr {
        public final ID<RJSSwitch> route;

        public SwitchRefExpr(ID<RJSSwitch> route) {
            this.route = route;
        }
    }
    // endregion

    // region CONTROL_FLOW

    public static final class IfExpr extends RJSRSExpr {
        @Json(name = "if")
        public final RJSRSExpr condition;

        @Json(name = "then")
        public final RJSRSExpr branchTrue;

        @Json(name = "else")
        public final RJSRSExpr branchFalse;

        /** If the "if" expression returns true, run the "then" branch. Otherwise, run the "else" branch. */
        public IfExpr(RJSRSExpr condition, RJSRSExpr branchTrue, RJSRSExpr branchFalse) {
            this.condition = condition;
            this.branchTrue = branchTrue;
            this.branchFalse = branchFalse;
        }
    }

    public static final class CallExpr extends RJSRSExpr {
        public final ID<RJSRSFunction> function;

        public final RJSRSExpr[] arguments;

        CallExpr(ID<RJSRSFunction> function, RJSRSExpr[] arguments) {
            this.function = function;
            this.arguments = arguments;
        }
    }

    public static final class EnumMatchExpr extends RJSRSExpr {
        public final RJSRSExpr expr;

        public final Map<String, RJSRSExpr> branches;

        public EnumMatchExpr(
                RJSRSExpr expr,
                Map<String, RJSRSExpr> branches
        ) {
            this.expr = expr;
            this.branches = branches;
        }
    }

    // endregion

    // region FUNCTION_SPECIFIC

    public static final class ArgumentRefExpr extends RJSRSExpr {
        @Json(name = "argument_name")
        public final String argumentName;

        public ArgumentRefExpr(String argumentName) {
            this.argumentName = argumentName;
        }
    }

    // endregion

    // region PRIMITIVES

    /**
     * Returns whether some signal has a given aspect.
     */
    public static final class SignalAspectCheckExpr extends RJSRSExpr {
        /**
         * The signal the condition checks for.
         */
        public final RJSRSExpr signal;

        /** The condition is true when the signal has the following aspect */
        public final ID<RJSAspect> aspect;

        public SignalAspectCheckExpr(RJSRSExpr signal, ID<RJSAspect> aspect) {
            this.signal = signal;
            this.aspect = aspect;
        }
    }

    /**
     * Returns whether some route is in a given state.
     */
    public static final class  RouteStateCheckExpr extends RJSRSExpr {
        /**
         * The signal the condition checks for.
         */
        public final RJSRSExpr route;

        /** The condition is true when the signal has the following aspect */
        public final RJSRoute.State state;

        public RouteStateCheckExpr(RJSRSExpr route, RJSRoute.State state) {
            this.route = route;
            this.state = state;
        }
    }

    /**
     * Returns whether some aspect set contains a given aspect.
     */
    public static final class AspectSetContainsExpr extends RJSRSExpr {
        /**
         * The signal the condition checks for.
         */
        @Json(name = "aspect_set")
        public final RJSRSExpr aspectSet;

        /** The condition is true when the signal has the following aspect */
        public final ID<RJSAspect> aspect;

        public AspectSetContainsExpr(RJSRSExpr aspectSet, ID<RJSAspect> aspect) {
            this.aspectSet = aspectSet;
            this.aspect = aspect;
        }
    }
    // endregion
}