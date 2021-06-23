package fr.sncf.osrd.railjson.parser;

import java.util.ArrayList;
import java.util.List;

import fr.sncf.osrd.SuccessionTable;
import fr.sncf.osrd.railjson.parser.exceptions.InvalidSuccession;
import fr.sncf.osrd.railjson.schema.RJSSuccession;

public class RJSSuccessionParser {
    /** Parse the description of a switch succession tables */
    public static List<SuccessionTable> parse(
            RJSSuccession rjsSuccession
    ) throws InvalidSuccession {
        var switchSuccession = new ArrayList<SuccessionTable>();
        for (var rjsSuccessionTable : rjsSuccession.successionTables) {
            var successionTable = RJSSuccessionTableParser.parse(rjsSuccessionTable);
            switchSuccession.add(successionTable);
        }
        return switchSuccession;
    }
}