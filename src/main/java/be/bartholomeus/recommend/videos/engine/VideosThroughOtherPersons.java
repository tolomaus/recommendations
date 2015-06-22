package be.bartholomeus.recommend.videos.engine;

import be.bartholomeus.recommend.videos.domain.Relationships;
import com.graphaware.reco.generic.transform.ParetoScoreTransformer;
import com.graphaware.reco.generic.transform.ScoreTransformer;
import com.graphaware.reco.neo4j.engine.CollaborativeEngine;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.Map;

public class VideosThroughOtherPersons extends CollaborativeEngine {
    private final Label PERSON = DynamicLabel.label("Person");
    private final Label VIDEO = DynamicLabel.label("Video");

    @Override
    public String name() {
        return "collaboration-based";
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
    protected int scoreNode(Node recommendation, Node throughNode, Node similarNode, Relationship r1, Relationship r2, Relationship r3) {
        //TODO: use ratings (should be a property on the WATCHED relationship):
        // if both persons have a similar rating of the video (throughNode) then the score should be increased and vice versa
        // the score should also be correlated with the other person's rating of the recommended video

        return 1;
    }

    @Override
    protected Map<String, Object> details(Node throughNode, Node similarNode, Relationship r1, Relationship r2, Relationship r3) {
        Map<String, Object> result = new HashMap<>();
        result.put("video", throughNode.getProperty("name"));
        result.put("person", similarNode.getProperty("name"));
        return result;
    }
}
