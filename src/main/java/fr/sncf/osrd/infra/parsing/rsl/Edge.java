package fr.sncf.osrd.infra.parsing.rsl;

import fr.sncf.osrd.infra.parsing.railjson.schema.RJSTrackSection;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.ArrayList;

public class Edge extends RJSTrackSection
{
    private String startNodeID, endNodeID, bidirectional;
    private double grade, speed;

    public Edge(String id, double length, String startNodeID, String endNodeID, String bidirectional, double speed, double grade)
    {
        super(id, length);
        this.startNodeID = startNodeID;
        this.endNodeID = endNodeID;
        this.bidirectional = bidirectional;
        this.speed = speed;
        this.grade = grade;
    }

    public String getBidirectional(){
        return bidirectional;
    }
    public static ArrayList<Edge> parseEdges(Document document) {

        var edgesList = new ArrayList<Edge>();
        var edges = document.selectNodes("/line/links");

        for (var edgeNode : edges) {
            var edge = (Element) edgeNode;
            var startNodeID = edge.attributeValue("source");
            var endNodeID = edge.attributeValue("target");
            var bidirectional = edge.attributeValue("bidirectional");
            var lengthStr = edge.attributeValue("length");
            var speedStr = edge.attributeValue("vmax");
            var gradeStr = edge.attributeValue("gradient");
            double length = Double.parseDouble(lengthStr);
            double speed = Double.parseDouble(speedStr);
            double grade = Double.parseDouble(gradeStr);
            //create the string ID with startNodeID-endNodeID
            var id = String.join("-",startNodeID,endNodeID);
            var parsedEdge = new Edge(id,length,startNodeID,endNodeID,bidirectional,speed,grade);
            edgesList.add(parsedEdge);
        }
        return edgesList;
    }

}
