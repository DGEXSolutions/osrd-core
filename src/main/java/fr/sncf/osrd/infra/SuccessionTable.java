package fr.sncf.osrd.infra;

import java.util.ArrayList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * The mutable succesion table for a switch (order of trains on that switch)
 * There must be a SuccesionTable instance per switch on the network.
 */

@SuppressFBWarnings({ "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD" })
public class SuccessionTable {
    /** the switch identifier whose it is the succession table */
    public final String switchID;

    /** the table itself, an ordered list of trains identifier */
    public final ArrayList<String> table;

    /** Creates a new succession table */
    public SuccessionTable(String switchID, ArrayList<String> table) {
        this.switchID = switchID;
        this.table = table;
    }

    public SuccessionTable clone() {
        return new SuccessionTable(switchID, new ArrayList<String>(table));
    }
}
