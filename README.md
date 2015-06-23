# recommendations

## Description

The Recommendations project calculates recommendations based on a number of criteria.

The domain is very simple: people can watch videos. We keep a profile of the people which currently only contains their age and we also store which videos they have watched in the past. The videos have attributes like category (cartoons, live shows, kids, ...) and the series they belong to.

What we aim to do is to recommend them videos that they may be interested in.

We are combining several strategies to increase the likeliness of recommending videos that they will actually like ;-):
- collaboration-based: with this strategy we use the people's past watching histories to find people with similar interests as the person we are searching recommendations for. Any videos that such similar people have watched but not yet our person will be considered as a recommendation. The code for this strategy can be found here: [VideosThroughOtherPersons](https://github.com/tolomaus/recommendations/blob/master/src/main/java/be/bartholomeus/recommend/videos/engine/VideosThroughOtherPersons.java)
- content-based: with this strategy we use the attributes of the videos to find similarities between the videos. Any videos that are similar to videos that our person has already watched in the past will be considered as a recommendation. The code for this strategy can be found here: [VideosThroughOtherVideosByCategory](https://github.com/tolomaus/recommendations/blob/master/src/main/java/be/bartholomeus/recommend/videos/engine/VideosThroughOtherVideosByCategory.java)

![alt text](https://github.com/tolomaus/recommendations/blob/master/architecture.png "architecture")

Each strategy will attribute a score based on the importance of the similarity and the total score will define the order in which the videos will be recommended.

Some special cases can add a small bonus to the scores:
- new videos ([RewardNewVideo](https://github.com/tolomaus/recommendations/blob/master/src/main/java/be/bartholomeus/recommend/videos/post/RewardNewVideo.java))
- hot videos (videos that are being watched by many people at the moment) ([RewardHotVideo](https://github.com/tolomaus/recommendations/blob/master/src/main/java/be/bartholomeus/recommend/videos/post/RewardHotVideo.java))

And one special case will incur a little penalty to the score:
- videos that were already recommended recently ([PenalizeAlreadyRecommended(https://github.com/tolomaus/recommendations/blob/master/src/main/java/be/bartholomeus/recommend/videos/post/PenalizeAlreadyRecommended.java)])

## Architecture

This solution is based on the [Neo4j graph database](https://github.com/neo4j/neo4j) and the [GraphAware Neo4j Recommendation Engine](https://github.com/graphaware/neo4j-reco)

## Example

Consider the following data:

```sql
CREATE
(n:Person:Male {name:'Niek', age:9})
(p:Person:Male {name:'Patrick', age:12})
(j:Person:Male {name:'Jan', age:13})

(amika1:Video {name:'Amika episode 1', series:'Amika'})
(amika2:Video {name:'Amika episode 2', series:'Amika'})
(amika3:Video {name:'Amika episode 3', series:'Amika', created:" + new Date().getTime() + "})

(bob1:Video {name:'Bob De Bouwer episode 1', series:'Bob De Bouwer'})
(bob2:Video {name:'Bob De Bouwer episode 2', series:'Bob De Bouwer'})
(bob3:Video {name:'Bob De Bouwer episode 3', series:'Bob De Bouwer'})
(bob4:Video {name:'Bob De Bouwer episode 4', series:'Bob De Bouwer', created:" + new Date().getTime() + "})

(smurf1:Video {name:'De Smurfen episode 1', series:'De Smurfen'})
(smurf2:Video {name:'De Smurfen episode 2', series:'De Smurfen'})
(smurf3:Video {name:'De Smurfen episode 3', series:'De Smurfen'})
(smurf4:Video {name:'De Smurfen episode 4', series:'De Smurfen'})
(smurf5:Video {name:'De Smurfen episode 5', series:'De Smurfen', created:" + new Date().getTime() + "})

(ice:Video {name:'Ketnet On Ice'})

(kuifje7:Video {name:'Kuifje episode 7', series:'Kuifje'})

(piet15:Video {name:'Piet Piraat episode 15', series:'Piet Piraat'})

(cartoons:Category {name:'Cartoons'})
(kids:Category {name:'Kids'})
(liveshow:Category {name:'Live Show'})

(amika1)-[:CATEGORIZED_BY]->(kids)
(amika2)-[:CATEGORIZED_BY]->(kids)
(amika3)-[:CATEGORIZED_BY]->(kids)

(bob1)-[:CATEGORIZED_BY]->(cartoons)
(bob2)-[:CATEGORIZED_BY]->(cartoons)
(bob3)-[:CATEGORIZED_BY]->(cartoons)
(bob4)-[:CATEGORIZED_BY]->(cartoons)

(smurf1)-[:CATEGORIZED_BY]->(cartoons)
(smurf2)-[:CATEGORIZED_BY]->(cartoons)
(smurf3)-[:CATEGORIZED_BY]->(cartoons)
(smurf4)-[:CATEGORIZED_BY]->(cartoons)
(smurf5)-[:CATEGORIZED_BY]->(cartoons)

(ice)-[:CATEGORIZED_BY]->(liveshow)

(kuifje7)-[:CATEGORIZED_BY]->(cartoons)

(piet15)-[:CATEGORIZED_BY]->(kids)

(n)-[:WATCHED{date:" + new Date().getTime() + "}]->(amika1)
(n)-[:WATCHED]->(amika2)

(n)-[:WATCHED]->(bob1)
(n)-[:WATCHED]->(bob2)
(n)-[:WATCHED{date:" + new Date().getTime() + "}]->(bob3)

(j)-[:WATCHED]->(amika1)
(j)-[:WATCHED{date:" + new Date().getTime() + "}]->(amika3)

(p)-[:WATCHED]->(amika1)

(p)-[:WATCHED]->(bob1)
(p)-[:WATCHED{date:" + new Date().getTime() + "}]->(bob2)
(p)-[:WATCHED]->(bob3)"
```

Depending on the exact parameters that are passed, this may produce the following recommendations:

```
Recommendations for Niek:
  Amika episode 3: score 82.73459
    alreadyRecommended: score 0.0
    collaboration-based: score 14.866008
      reasons:
        -
          person: Jan
          video: Amika episode 1
          score: 1.0
    content-based: score 47.469444
      reasons:
        -
          sameSeries: true
          category: Kids
          video: Amika episode 2
          score: 2.0
        -
          sameSeries: true
          category: Kids
          video: Amika episode 1
          score: 2.0
    hotVideo: score 0.39913893
    newVideo: score 20.0

  Bob De Bouwer episode 4: score 81.926926
    alreadyRecommended: score 0.0
    content-based: score 61.92692
      reasons:
        -
          sameSeries: true
          category: Cartoons
          video: Bob De Bouwer episode 1
          score: 2.0
        -
          sameSeries: true
          category: Cartoons
          video: Bob De Bouwer episode 2
          score: 2.0
        -
          sameSeries: true
          category: Cartoons
          video: Bob De Bouwer episode 3
          score: 2.0
    hotVideo: score 0.0
    newVideo: score 20.0

Recommendations for Patrick:
  Bob De Bouwer episode 4: score 81.926926
    alreadyRecommended: score 0.0
    content-based: score 61.92692
      reasons:
        -
          sameSeries: true
          category: Cartoons
          video: Bob De Bouwer episode 1
          score: 2.0
        -
          sameSeries: true
          category: Cartoons
          video: Bob De Bouwer episode 2
          score: 2.0
        -
          sameSeries: true
          category: Cartoons
          video: Bob De Bouwer episode 3
          score: 2.0
    hotVideo: score 0.0
    newVideo: score 20.0

  Amika episode 2: score 74.99148
    alreadyRecommended: score 0.0
    collaboration-based: score 47.469444
      reasons:
        -
          person: Niek
          video: Bob De Bouwer episode 1
          score: 1.0
        -
          person: Niek
          video: Bob De Bouwer episode 3
          score: 1.0
        -
          person: Niek
          video: Bob De Bouwer episode 2
          score: 1.0
        -
          person: Niek
          video: Amika episode 1
          score: 1.0
    content-based: score 27.522034
      reasons:
        -
          sameSeries: true
          category: Kids
          video: Amika episode 1
          score: 2.0
    hotVideo: score 0.0
    newVideo: score 0.0
```

Note that the underlying reasoning for the calculation of the score is mentioned.

## Installation

Make sure you have the following tools installed:
- a JDK 1.7
- maven
- IntelliJ IDEA (or similar)

This repository uses the latest version of the GraphAware Neo4j Recommendation Engine so we will have to clone its repository and install it into the local maven store:

```
git clone https://github.com/graphaware/neo4j-reco.git
cd neo4j-reco
mvn clean install
```

Then you can clone the Recommendations repository: :
```
git clone https://github.com/tolomaus/recommendations.git
```

Finally open the repo in IntelliJ and run the [test](https://github.com/tolomaus/recommendations/blob/master/src/test/java/be/bartholomeus/recommend/videos/domain/VideosComputingEngineTest.java).

