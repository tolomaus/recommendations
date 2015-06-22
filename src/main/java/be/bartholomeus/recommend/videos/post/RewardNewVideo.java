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

package be.bartholomeus.recommend.videos.post;

import com.graphaware.reco.generic.context.Context;
import com.graphaware.reco.generic.post.PostProcessor;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.reco.generic.result.Recommendations;
import com.graphaware.reco.generic.transform.ParetoFunction;
import com.graphaware.reco.generic.transform.TransformationFunction;
import org.neo4j.graphdb.Node;

import java.util.Date;

/**
 * Rewards same gender (exactly the same labels) by 10 points.
 */
public class RewardNewVideo implements PostProcessor<Node, Node> {

    private final TransformationFunction function = new ParetoFunction(25, 30); // when value is 30 (the max in our case, ie the video was created within the previous day) then the function will return 20 (= 25 * 80%)

    @Override
    public void postProcess(Recommendations<Node> recommendations, Node input, Context<Node, Node> context) {
        for (Recommendation<Node> reco : recommendations.get()) {
            long diff = new Date().getTime() - (long)reco.getItem().getProperty("created", (long)0);
            long diffDays = diff / (24 * 60 * 60 * 1000);
            reco.add("newVideo", function.transform(Math.max(30 - diffDays, 0))); // value is 30 when video was created within the previous day
        }
    }
}
