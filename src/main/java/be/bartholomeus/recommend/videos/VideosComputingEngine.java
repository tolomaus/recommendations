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

import be.bartholomeus.recommend.videos.domain.Relationships;
import be.bartholomeus.recommend.videos.engine.VideosToWatch;
import com.graphaware.reco.generic.engine.RecommendationEngine;
import com.graphaware.reco.generic.filter.BlacklistBuilder;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingEngine;
import com.graphaware.reco.neo4j.filter.ExistingRelationshipBlacklistBuilder;
import org.neo4j.graphdb.Direction;
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
                new VideosToWatch()
        );
    }

    @Override
    protected List<BlacklistBuilder<Node, Node>> blacklistBuilders() {
        return Arrays.<BlacklistBuilder<Node, Node>>asList(
                new ExistingRelationshipBlacklistBuilder(Relationships.WATCHED, Direction.BOTH)//TODO pick either INCOMING or OUTGOING
        );
    }
}
