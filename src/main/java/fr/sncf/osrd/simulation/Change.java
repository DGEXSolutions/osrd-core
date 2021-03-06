package fr.sncf.osrd.simulation;

public abstract class Change {
    public enum State {
        // the simulation doesn't yet know about this change
        UNREGISTERED,
        // the simulation knows about this change, but the change wasn't yet deemed ready
        REGISTERED,
        // the change was sent to the change log (event store)
        PUBLISHED,
    }

    // this state is only for debug, serializing it makes no sense at all
    public transient State state = State.UNREGISTERED;

    protected Change(Simulation sim) {
        sim.onChangeCreated(this);
    }

    public abstract void replay(Simulation sim);

    /** Enforce mandatory pretty-printing on all changes */
    public abstract String toString();
}
