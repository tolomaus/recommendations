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

package be.bartholomeus.recommend.videos;

import be.bartholomeus.recommend.videos.engine.VideosThroughOtherPersons;
import be.bartholomeus.recommend.videos.engine.VideosThroughOtherVideosByCategory;
import be.bartholomeus.recommend.videos.filter.AlreadyWatchedVideoBlacklistBuilder;
import be.bartholomeus.recommend.videos.post.PenalizeAlreadyRecommended;
import be.bartholomeus.recommend.videos.post.RewardHotVideo;
import be.bartholomeus.recommend.videos.post.RewardNewVideo;
import com.graphaware.reco.generic.engine.RecommendationEngine;
import com.graphaware.reco.generic.filter.BlacklistBuilder;
import com.graphaware.reco.generic.post.PostProcessor;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingEngine;
import org.neo4j.graphdb.Node;

import java.util.Arrays;
import java.util.List;

/**
 * {@link Neo4jTopLevelDelegatingEngine} that computes friend recommendations.
 */
public class VideosComputingEngine extends Neo4jTopLevelDelegatingEngine {

    @Override
    protected List<RecommendationEngine<Node, Node>> engines() {
        return Arrays.<RecommendationEngine<Node, Node>>asList(
                new VideosThroughOtherPersons(),
                new VideosThroughOtherVideosByCategory()
        );
    }

    @Override
    protected List<BlacklistBuilder<Node, Node>> blacklistBuilders() {
        return Arrays.<BlacklistBuilder<Node, Node>>asList(
                new AlreadyWatchedVideoBlacklistBuilder()
        );
    }

    @Override
    protected List<PostProcessor<Node, Node>> postProcessors() {
        return Arrays.asList(
                new PenalizeAlreadyRecommended(),
                new RewardHotVideo(),
                new RewardNewVideo()
        );
    }
}
