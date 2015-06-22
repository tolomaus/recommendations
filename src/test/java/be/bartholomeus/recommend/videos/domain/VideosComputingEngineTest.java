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
import be.bartholomeus.recommend.videos.log.RecommendationsFormatter;
import com.graphaware.common.util.IterableUtils;
import com.graphaware.reco.generic.config.SimpleConfig;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.test.integration.DatabaseIntegrationTest;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VideosComputingEngineTest extends DatabaseIntegrationTest {

    private VideosComputingEngine engine = new VideosComputingEngine();

    @Override
    protected void populateDatabase(GraphDatabaseService database) {
        database.execute(
                "CREATE " +
                        "(n:Person:Male {name:'Niek', age:9})," +
                        "(p:Person:Male {name:'Patrick', age:12})," +
                        "(j:Person:Male {name:'Jan', age:13})," +

                        "(amika1:Video {name:'Amika episode 1', series:'Amika'})," +
                        "(amika2:Video {name:'Amika episode 2', series:'Amika'})," +
                        "(amika3:Video {name:'Amika episode 3', series:'Amika', created:" + new Date().getTime() + "})," +

                        "(bob1:Video {name:'Bob De Bouwer episode 1', series:'Bob De Bouwer'})," +
                        "(bob2:Video {name:'Bob De Bouwer episode 2', series:'Bob De Bouwer'})," +
                        "(bob3:Video {name:'Bob De Bouwer episode 3', series:'Bob De Bouwer'})," +
                        "(bob4:Video {name:'Bob De Bouwer episode 4', series:'Bob De Bouwer', created:" + new Date().getTime() + "})," +

                        "(smurf1:Video {name:'De Smurfen episode 1', series:'De Smurfen'})," +
                        "(smurf2:Video {name:'De Smurfen episode 2', series:'De Smurfen'})," +
                        "(smurf3:Video {name:'De Smurfen episode 3', series:'De Smurfen'})," +
                        "(smurf4:Video {name:'De Smurfen episode 4', series:'De Smurfen'})," +
                        "(smurf5:Video {name:'De Smurfen episode 5', series:'De Smurfen', created:" + new Date().getTime() + "})," +

                        "(ice:Video {name:'Ketnet On Ice'})," +

                        "(kuifje7:Video {name:'Kuifje episode 7', series:'Kuifje'})," +

                        "(piet15:Video {name:'Piet Piraat episode 15', series:'Piet Piraat'})," +

                        "(cartoons:Category {name:'Cartoons'})," +
                        "(kids:Category {name:'Kids'})," +
                        "(liveshow:Category {name:'Live Show'})," +

                        "(amika1)-[:CATEGORIZED_BY]->(kids)," +
                        "(amika2)-[:CATEGORIZED_BY]->(kids)," +
                        "(amika3)-[:CATEGORIZED_BY]->(kids)," +

                        "(bob1)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(bob2)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(bob3)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(bob4)-[:CATEGORIZED_BY]->(cartoons)," +

                        "(smurf1)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(smurf2)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(smurf3)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(smurf4)-[:CATEGORIZED_BY]->(cartoons)," +
                        "(smurf5)-[:CATEGORIZED_BY]->(cartoons)," +

                        "(ice)-[:CATEGORIZED_BY]->(liveshow)," +

                        "(kuifje7)-[:CATEGORIZED_BY]->(cartoons)," +

                        "(piet15)-[:CATEGORIZED_BY]->(kids)," +

                        "(n)-[:WATCHED{date:" + new Date().getTime() + "}]->(amika1)," +
                        "(n)-[:WATCHED]->(amika2)," +

                        "(n)-[:WATCHED]->(bob1)," +
                        "(n)-[:WATCHED]->(bob2)," +
                        "(n)-[:WATCHED{date:" + new Date().getTime() + "}]->(bob3)," +

                        "(j)-[:WATCHED]->(amika1)," +
                        "(j)-[:WATCHED{date:" + new Date().getTime() + "}]->(amika3)," +

                        "(p)-[:WATCHED]->(amika1)," +

                        "(p)-[:WATCHED]->(bob1)," +
                        "(p)-[:WATCHED{date:" + new Date().getTime() + "}]->(bob2)," +
                        "(p)-[:WATCHED]->(bob3)" +

                        "");
    }

    @Test
    public void shouldComputeVideos() {
        try (Transaction tx = getDatabase().beginTx()) {
            RecommendationsFormatter formatter = new RecommendationsFormatter();

            Node niek = getPersonByName("Niek");
            List<Recommendation<Node>> videosForNiek = engine.recommend(niek, new SimpleConfig(Integer.MAX_VALUE));

            System.out.println(formatter.format(niek, videosForNiek, null));

            assertEquals(9, videosForNiek.size());

            Recommendation<Node> firstReco = videosForNiek.get(0);
            float score = firstReco.getScore().getTotalScore();
            assertEquals("Amika episode 3", itemToString(firstReco.getItem()));

            Node patrick = getPersonByName("Patrick");
            List<Recommendation<Node>> videosForPatrick = engine.recommend(patrick, new SimpleConfig(Integer.MAX_VALUE));

            System.out.println(formatter.format(patrick, videosForPatrick, null));

            tx.success();
        }
    }

    private Node getPersonByName(String name) {
        return IterableUtils.getSingle(getDatabase().findNodes(DynamicLabel.label("Person"), "name", name));
    }

    private String itemToString(Node item) {
        return item.getProperty("name", "unknown").toString();
    }

}
