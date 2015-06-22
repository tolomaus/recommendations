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

package be.bartholomeus.recommend.videos.filter;

import be.bartholomeus.recommend.videos.domain.Relationships;
import com.graphaware.reco.generic.config.Config;
import com.graphaware.reco.generic.filter.BlacklistBuilder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * {@link BlacklistBuilder} blacklisting items with which the subject of the recommendation (input) has a relationship.
 */
public class AlreadyWatchedVideoBlacklistBuilder implements BlacklistBuilder<Node, Node> {
    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<Node> buildBlacklist(Node input, Config config) {
        notNull(input);

        Set<Node> excluded = new HashSet<>();

        for (Relationship r : input.getRelationships(Relationships.WATCHED, Direction.OUTGOING)) {
            excluded.add(r.getOtherNode(input));
        }

        return excluded;
    }
}
