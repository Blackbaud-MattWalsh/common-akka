package demo;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class Database {

    private Map<String, List<TopicActor.NewTopicMessage>> map = new HashMap<>();

    public void saveTopicMessage(final String topic, final TopicActor.NewTopicMessage message) {
        map.computeIfAbsent(topic, k -> new LinkedList<>(Collections.singletonList(message)));
    }

}
