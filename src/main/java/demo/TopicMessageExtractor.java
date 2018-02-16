package demo;

import akka.cluster.sharding.ShardRegion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopicMessageExtractor implements ShardRegion.MessageExtractor {
    @Override
    public String entityId(Object message) {
        if (message instanceof TopicActor.TopicMsg) {
            return ((TopicActor.TopicMsg) message).topicId;
        }
        log.info("message not of type TopicMsg");
        return null;
    }

    @Override
    public Object entityMessage(Object message) {
        if (message instanceof TopicActor.TopicMsg) {
            return message;
        }
        log.info("message not of type TopicMsg");
        return null;
    }

    @Override
    public String shardId(Object message) {
        if (message instanceof TopicActor.TopicMsg) {
            return String.valueOf(((TopicActor.TopicMsg) message).topicId.hashCode() % 97);
        }
        log.info("message not of type TopicMsg");
        return null;
    }
}
