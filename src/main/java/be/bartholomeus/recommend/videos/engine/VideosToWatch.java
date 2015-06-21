package be.bartholomeus.recommend.videos.engine;


import be.bartholomeus.recommend.videos.domain.Relationships;
import com.graphaware.reco.generic.transform.ParetoScoreTransformer;
import com.graphaware.reco.generic.transform.ScoreTransformer;
import com.graphaware.reco.neo4j.engine.CollaborativeEngine;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.Map;

public class VideosToWatch extends CollaborativeEngine {

    private final Label PERSON = DynamicLabel.label("Person");
    private final Label VIDEO = DynamicLabel.label("Video");

    @Override
    public String name() {
        return "videos";
    }

    @Override
    protected RelationshipType getType() {
        return Relationships.WATCHED;
    }

    @Override
    protected Direction getDirection() {
        return Direction.OUTGOING;
    }

    @Override
    protected boolean acceptableThroughNode(Node node) {
        return node.hasLabel(VIDEO);
    }

    @Override
    protected boolean acceptableSimilarNode(Node node) {
        return node.hasLabel(PERSON);
    }

    @Override
    protected ScoreTransformer scoreTransformer() {
        return new ParetoScoreTransformer(100, 10);
    }

    @Override
    protected Map<String, Object> details(Node throughNode, Node similarNode, Relationship r1, Relationship r2, Relationship r3) {
        Map<String, Object> result = new HashMap<>();
        result.put("video", throughNode.getProperty("name"));
        result.put("person", similarNode.getProperty("name"));
        return result;
    }
}
