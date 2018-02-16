package com.blackbaud.akka.actors;

import akka.actor.Actor;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringExtension implements Extension {

    private ApplicationContext applicationContext;

    @Autowired
    public SpringExtension(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Props props(final Class<? extends Actor> actorClass) {
        return Props.create(SpringActorProducer.class, applicationContext, actorClass);
    }

}