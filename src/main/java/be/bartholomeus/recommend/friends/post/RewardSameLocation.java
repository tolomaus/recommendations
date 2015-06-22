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

package be.bartholomeus.recommend.friends.post;

import be.bartholomeus.recommend.friends.domain.Relationships;
import com.graphaware.reco.generic.result.PartialScore;
import com.graphaware.reco.neo4j.post.RewardSomethingShared;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import java.util.Collections;

import static org.neo4j.graphdb.Direction.OUTGOING;

/**
 * Rewards same location by 10 points.
 */
public class RewardSameLocation extends RewardSomethingShared {

    @Override
    protected RelationshipType type() {
        return Relationships.LIVES_IN;
    }

    @Override
    protected Direction direction() {
        return OUTGOING;
    }

    @Override
    protected PartialScore partialScore(Node recommendation, Node input, Node sharedThing) {
        return new PartialScore(10, Collections.singletonMap("location", sharedThing.getProperty("name")));
    }

    @Override
    protected String scoreName() {
        return "sameLocation";
    }
}
