package demo;

import akka.actor.ActorRef;
import com.blackbaud.akka.actors.Endpoint;
import com.blackbaud.akka.actors.ShardRegions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController
public class EndpointsController {

    @Autowired
    private ShardRegions shardRegions;

    @Autowired
    private Endpoint endpoint;

    @Async
    @GetMapping("/newTopicMessage")
    public CompletableFuture<String> newTopicMessage() {
        TopicActor.NewTopicMessage newTopicMessage = new TopicActor.NewTopicMessage(randomThing(topics), randomThing(comments));
        ActorRef shardRegion = shardRegions.getShardRegion(TopicActor.class);
        return endpoint.sendMessage(newTopicMessage, shardRegion, String.class)
                .thenApply(response -> "COMPLETED => " + response);
    }

    private final List<String> topics = Collections.unmodifiableList(Arrays.asList(
            "Programming",
            "Politics",
            "Beer"
    ));

    private final List<String> comments = Collections.unmodifiableList(Arrays.asList(
            "Don't steal, don't lie, don't cheat, don't sell drugs. The government hates competition!",
            "Some people come into our lives and leave footprints on our hearts, while others come into our lives and make us wanna leave footprints on their face.",
            "Men are simple things. They can survive a whole weekend with only three things: beer, boxer shorts and batteries for the remote control.",
            "Thank you Facebook, I can now farm without going outside, cook without being in my kitchen, feed fish I don't have & waste an entire day without having a life.",
            "After one look at this planet any visitor from outer space would say “I WANT TO SEE THE MANAGER.”",
            "Sorry, I can't hangout. My uncle's cousin's sister in law's best friend's insurance agent's roommate's pet goldfish died. Maybe next time."
    ));

    private String randomThing(List<String> stringList) {
        return stringList.get(new Random().nextInt(stringList.size()));
    }

}
