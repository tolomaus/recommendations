package be.bartholomeus.recommend.videos.log;

import com.graphaware.reco.generic.context.Context;
import com.graphaware.reco.generic.result.PartialScore;
import com.graphaware.reco.generic.result.Reason;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.reco.generic.result.Score;
import org.neo4j.graphdb.Node;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationsFormatter {

    public String format(Node input, List<Recommendation<Node>> recommendations, Context<Node, Node> context) {
        StringBuilder builder = new StringBuilder("Recommendations for ").append(inputToString(input)).append(":\n");
        for (Recommendation<Node> recommendation : recommendations) {
            builder.append("  ");
            builder.append(itemToString(recommendation.getItem()));
            builder.append(scoreToString(recommendation.getScore())).append("\n");
        }

        return builder.toString();
    }

    /**
     * Convert a score to String.
     *
     * @param score to convert.
     * @return converted score.
     */
    protected String scoreToString(Score score) {
        StringBuilder builder = new StringBuilder(": score ").append(score.getTotalScore()).append("\n");

        for (Map.Entry<String, PartialScore> entry : score.getScoreParts().entrySet()) {
            PartialScore partialScore = entry.getValue();

            builder.append("    ").append(entry.getKey()).append(": score ").append(partialScore.getValue()).append("\n");

            Set<Reason> reasons = partialScore.getReasons();
            if (reasons.size() > 0) {
                builder.append("      reasons:\n");
                for (Reason reason : reasons) {
                    builder.append("        -\n");
                    for (Map.Entry<String, Object> detail : reason.getDetails().entrySet()) {
                        builder.append("          ").append(detail.getKey()).append(": ").append(detail.getValue()).append("\n");
                    }
                    builder.append("          score: ").append(reason.getValue()).append("\n");
                }
            }
        }

        return builder.toString();
    }

    private String inputToString(Node input) {
        return input.getProperty("name", "unknown").toString();
    }

    private String itemToString(Node item) {
        return item.getProperty("name", "unknown").toString();
    }
}
