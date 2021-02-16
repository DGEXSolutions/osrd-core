package fr.sncf.osrd.infra.parsing.railjson.schema.trackranges;

import fr.sncf.osrd.infra.parsing.railjson.schema.ApplicableDirections;

public class DirectionalRJSTrackRange extends RJSTrackRange {
    public final ApplicableDirections applicableDirections;

    DirectionalRJSTrackRange(ApplicableDirections applicableDirections, double begin, double end) {
        super(begin, end);
        this.applicableDirections = applicableDirections;
    }

    @Override
    ApplicableDirections getNavigability() {
        return applicableDirections;
    }
}