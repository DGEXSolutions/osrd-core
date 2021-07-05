package fr.sncf.osrd.railjson.schema;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import fr.sncf.osrd.railjson.schema.successiontable.RJSSuccessionTable;

import java.util.Collection;

public final class RJSSuccession {
    public static final JsonAdapter<RJSSuccession> adapter = new Moshi.Builder().build().adapter(RJSSuccession.class);

    /** An incremental format version number, which may be used for migrations */
    public final int version = 1;

    /** A list of succession tables involved in this simulation */
    @Json(name = "successions")
    public Collection<RJSSuccessionTable> successionTables;

    public RJSSuccession(Collection<RJSSuccessionTable> successionTables) {
        this.successionTables = successionTables;
    }
}