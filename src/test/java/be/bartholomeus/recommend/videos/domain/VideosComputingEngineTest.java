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

package be.bartholomeus.recommend.videos.domain;

import be.bartholomeus.recommend.videos.VideosComputingEngine;
import com.graphaware.common.util.IterableUtils;
import com.graphaware.reco.generic.config.Config;
import com.graphaware.reco.generic.context.SimpleContext;
import com.graphaware.reco.generic.engine.RecommendationEngine;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.test.integration.DatabaseIntegrationTest;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class VideosComputingEngineTest extends DatabaseIntegrationTest {

    private RecommendationEngine<Node, Node> engine = new VideosComputingEngine();

    @Override
    protected void populateDatabase(GraphDatabaseService database) {
        database.execute(
                "CREATE " +
                        "(n:Person:Male {name:'Niek', age:9})," +
                        "(p:Person:Male {name:'Patrick', age:12})," +

                        "(amika1:Video {name:'Amika episode 1', series:'Amika'})," +
                        "(amika2:Video {name:'Amika episode 2', series:'Amika'})," +
                        "(amika3:Video {name:'Amika episode 3', series:'Amika'})," +

                        "(bob1:Video {name:'Bob De Bouwer episode 1', series:'Bob De Bouwer'})," +
                        "(bob2:Video {name:'Bob De Bouwer episode 2', series:'Bob De Bouwer'})," +
                        "(bob3:Video {name:'Bob De Bouwer episode 3', series:'Bob De Bouwer'})," +
                        "(bob4:Video {name:'Bob De Bouwer episode 4', series:'Bob De Bouwer'})," +

                        "(smurf1:Video {name:'De Smurfen episode 1', series:'De Smurfen'})," +
                        "(smurf2:Video {name:'De Smurfen episode 2', series:'De Smurfen'})," +
                        "(smurf3:Video {name:'De Smurfen episode 3', series:'De Smurfen'})," +
                        "(smurf4:Video {name:'De Smurfen episode 4', series:'De Smurfen'})," +
                        "(smurf5:Video {name:'De Smurfen episode 5', series:'De Smurfen'})," +

                        "(ice:Video {name:'Ketnet On Ice'})," +

                        "(kuifje7:Video {name:'Kuifje episode 7', series:'Kuifje'})," +

                        "(piet15:Video {name:'Piet Piraat episode 15', series:'Piet Piraat'})," +

                        "(n)-[:WATCHED]->(amika1)," +
                        "(n)-[:WATCHED]->(amika2)," +

                        "(p)-[:WATCHED]->(amika1)");
    }

    @Test
    public void shouldComputeVideos() {
        try (Transaction tx = getDatabase().beginTx()) {

            Node patrick = getPersonByName("Patrick");
            List<Recommendation<Node>> videosForPatrick = engine.recommend(patrick, new SimpleContext<Node, Node>(patrick, Config.UNLIMITED)).get(Integer.MAX_VALUE);

            assertEquals(1, videosForPatrick.size());

            tx.success();
        }
    }

    private Node getPersonByName(String name) {
        return IterableUtils.getSingle(getDatabase().findNodes(DynamicLabel.label("Person"), "name", name));
    }

}
