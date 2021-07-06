package fr.sncf.osrd.api;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;

import fr.sncf.osrd.infra.SuccessionTable;
import fr.sncf.osrd.railjson.parser.RJSSuccessionParser;
import fr.sncf.osrd.railjson.parser.exceptions.InvalidSuccession;
import fr.sncf.osrd.railjson.schema.RJSSuccession;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SuccessionsHandler {
    private final HashMap<String, List<SuccessionTable>> cache = new HashMap<>();
    private final OkHttpClient client = new OkHttpClient();
    private final String baseUrl;
    private final String authorizationToken;

    public SuccessionsHandler(String baseUrl, String authorizationToken) {
        this.baseUrl = baseUrl;
        this.authorizationToken = authorizationToken;
    }

    private List<SuccessionTable> querySuccessions(String successionsId) throws IOException, InvalidSuccession {
        // create a request
        var builder = new Request.Builder();
        if (authorizationToken != null)
                builder = builder.header("Authorization", authorizationToken);
        var request = builder.url(String.format("%ssuccessions/%s/railjson/", baseUrl, successionsId)).build();

        // use the client to send the request
        var response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        // Parse the response
        var body = response.body();
        assert body != null;
        var rjsSuccessions = RJSSuccession.adapter.fromJson(body.source());
        if (rjsSuccessions == null)
            throw new IOException("RJSInfra is null");
        return RJSSuccessionParser.parse(rjsSuccessions);
    }

    /** Load a successions given an id. Cache successions for optimized future call */
    public List<SuccessionTable> load(String successionsId) throws IOException, InvalidSuccession {
        var cachedSuccessions = cache.get(successionsId);
        if (cachedSuccessions != null)
            return cachedSuccessions;

        // Query successions
        var successions = querySuccessions(successionsId);

        // Cache the successions
        cache.put(successionsId, successions);
        return successions;
    }
}
