package fr.sncf.osrd.simulation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fr.sncf.osrd.config.Config;
import fr.sncf.osrd.infra.Infra;
import fr.sncf.osrd.simulation.utils.PassiveEntity;
import fr.sncf.osrd.train.Train;

import java.util.HashSet;
import java.util.Objects;

/**
 * The world object can be read everywhere in the simulation.
 * It's meant as a centralized repository for the state of the simulation.
 */
@SuppressFBWarnings(value = "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public final class World {
    public final Infra infra;
    public final Config config;
    public SchedulerSystem scheduler = null;
    public final HashSet<Train> trains = new HashSet<>();

    /**
     * Create the world from a configuration
     * @param config the configuration of the simulation
     */
    public World(Config config) {
        // it's redundant but useful
        this.infra = config.infra;
        this.config = config;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj.getClass() != World.class)
            return false;

        var other = (World)obj;
        if (this.infra != other.infra)
            return false;

        if (this.config != other.config)
            return false;

        return this.trains.equals(other.trains);
    }

    @Override
    public int hashCode() {
        // as we compare infra by reference above, we must also hash the object reference,
        // and not use the infra hash code (we don't want to hash the whole infra)
        //
        // var a = Infra(stuff);
        // var b = Infra(stuff);

        // a.equals(b)
        // a.hashCode() == b.hashCode();

        // a != b
        // System.identityHashCode(a) != System.identityHashCode(b)
        return Objects.hash(System.identityHashCode(infra), System.identityHashCode(config), trains);
    }
}