package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.blackbaud.akka.actors.AkkaActorComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@AkkaActorComponent
public class TopicActor extends AbstractActor {

    @Autowired
    private Database database;

    private final List<NewTopicMessage> topicMessages = new ArrayList<>();

    @SuppressWarnings("WeakerAccess")
    public abstract static class TopicMsg implements Serializable {
        private static final long serialVersionUID = -8164492162228866838L;
        public final String uuid;
        public final String topicId;

        public TopicMsg(final String uuid, final String topicId) {
            this.uuid = uuid;
            this.topicId = topicId;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class NewTopicMessage extends TopicMsg {
        private static final long serialVersionUID = 3485467686802992238L;
        public final String message;
        public final Instant created = Instant.now();

        public NewTopicMessage(final String topicId, final String message) {
            super(UUID.randomUUID().toString(), topicId);
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewTopicMessage.class, this::processNewTopicMessage)
                .matchAny(this::unhandled)
                .build();
    }

    private void processNewTopicMessage(final NewTopicMessage newTopicMessage) {
        topicMessages.add(newTopicMessage);
        database.saveTopicMessage(newTopicMessage.topicId, newTopicMessage);
        log.info(String.format("\n[%s], %d messages", newTopicMessage.topicId, topicMessages.size()));
        topicMessages.forEach(msg ->
                log.info(String.format("%s -> %s (%s)", msg.uuid, msg.message, msg.created)));
        getSender().tell("SUCCESS", ActorRef.noSender());
    }

}
