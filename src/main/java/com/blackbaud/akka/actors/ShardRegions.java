package com.blackbaud.akka.actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterSharding$;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion.MessageExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Import(SpringExtension.class)
public final class ShardRegions {

    @Autowired
    private final SpringExtension springExtension;

    private final Map<Class<? extends Actor>, ActorRef> actorRefHashMap = new ConcurrentHashMap<>();

    @Autowired
    public ShardRegions(final SpringExtension springExtension) {
        this.springExtension = springExtension;
    }

    public void startActor(final ActorSystem system, final Class<? extends Actor> actorClass, final Class<? extends MessageExtractor> msgExtractor) {
        final Props props = springExtension.props(actorClass);
        final String simpleName = actorClass.getSimpleName();
        final ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        final ClusterSharding clusterSharding = ClusterSharding$.MODULE$.get(system);
        final ActorRef start;
        try {
            start = clusterSharding.start(simpleName, props, settings, msgExtractor.newInstance());
            actorRefHashMap.put(actorClass, start);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("There was a problem creating an actor.", e);
        }
    }

    public ActorRef getShardRegion(final Class<? extends Actor> clazz) {
        return actorRefHashMap.get(clazz);
    }

}
