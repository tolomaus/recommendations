/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package be.bartholomeus.recommend.friends;

import be.bartholomeus.recommend.friends.engine.FriendsInCommon;
import be.bartholomeus.recommend.friends.engine.RandomPeople;
import be.bartholomeus.recommend.friends.post.PenalizeAgeDifference;
import be.bartholomeus.recommend.friends.post.RewardSameLabels;
import be.bartholomeus.recommend.friends.post.RewardSameLocation;
import com.graphaware.reco.generic.context.Context;
import com.graphaware.reco.generic.engine.RecommendationEngine;
import com.graphaware.reco.generic.filter.BlacklistBuilder;
import com.graphaware.reco.generic.filter.Filter;
import com.graphaware.reco.generic.policy.ParticipationPolicy;
import com.graphaware.reco.generic.post.PostProcessor;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingEngine;
import com.graphaware.reco.neo4j.filter.ExcludeSelf;
import com.graphaware.reco.neo4j.filter.ExistingRelationshipBlacklistBuilder;
import org.neo4j.graphdb.Node;

import java.util.Arrays;
import java.util.List;

import static be.bartholomeus.recommend.friends.domain.Relationships.FRIEND_OF;
import static org.neo4j.graphdb.Direction.BOTH;

/**
 * {@link com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingEngine} that computes friend recommendations.
 */
public class FriendsComputingEngine extends Neo4jTopLevelDelegatingEngine {

    @Override
    protected List<RecommendationEngine<Node, Node>> engines() {
        return Arrays.<RecommendationEngine<Node, Node>>asList(
                new FriendsInCommon(),
                new RandomPeople()
        );
    }

    @Override
    protected List<PostProcessor<Node, Node>> postProcessors() {
        return Arrays.asList(
                new RewardSameLabels(),
                new RewardSameLocation(),
                new PenalizeAgeDifference()
        );
    }

    @Override
    protected List<BlacklistBuilder<Node, Node>> blacklistBuilders() {
        return Arrays.<BlacklistBuilder<Node, Node>>asList(
                new ExistingRelationshipBlacklistBuilder(FRIEND_OF, BOTH)
        );
    }

    @Override
    protected List<Filter<Node, Node>> filters() {
        return Arrays.<Filter<Node, Node>>asList(
                new ExcludeSelf()
        );
    }

    @Override
    public ParticipationPolicy<Node, Node> participationPolicy(Context<Node, Node> context) {
        //noinspection unchecked
        return ParticipationPolicy.IF_MORE_RESULTS_NEEDED;
    }
}
