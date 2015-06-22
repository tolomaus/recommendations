package be.bartholomeus.recommend.videos.engine;

import be.bartholomeus.recommend.videos.domain.Relationships;
import com.graphaware.reco.generic.transform.ParetoScoreTransformer;
import com.graphaware.reco.generic.transform.ScoreTransformer;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.Map;

public class VideosThroughOtherVideosByCategory extends ContentBasedEngine {
    private final Label VIDEO = DynamicLabel.label("Video");
    private final Label CATEGORY = DynamicLabel.label("Category");

    @Override
    public String name() {
        return "content-based";
    }

    @Override
    protected RelationshipType getContentRelationshipType() {
        return Relationships.WATCHED;
    }

    @Override
    protected RelationshipType getSimilarityRelationshipType() {
        return Relationships.CATEGORIZED_BY;
    }

    @Override
    protected boolean acceptableThroughNode(Node node) {
        return node.hasLabel(VIDEO);
    }

    @Override
    protected boolean acceptableSimilarNode(Node node) {
        return node.hasLabel(CATEGORY);
    }

    @Override
    protected ScoreTransformer scoreTransformer() {
        return new ParetoScoreTransformer(100, 10);
    }

    @Override
    protected int scoreNode(Node recommendation, Node throughNode, Node similarNode, Relationship r1, Relationship r2, Relationship r3) {
        int score = 1;
        if(recommendation.getProperty("series").equals(throughNode.getProperty("series"))){
            score += 1;
        }

        return score;
    }

    @Override
    protected Map<String, Object> details(Node throughNode, Node similarNode, Relationship r1, Relationship r2, Relationship r3) {
        Map<String, Object> result = new HashMap<>();
        result.put("video", throughNode.getProperty("name"));
        result.put("category", similarNode.getProperty("name"));
        //result.put("scoreTransformer", scoreTransformer().getClass());
        Node recommendationNode = r3.getOtherNode(similarNode);
        result.put("sameSeries", throughNode.getProperty("series").equals(recommendationNode.getProperty("series")));
        return result;
    }
}
