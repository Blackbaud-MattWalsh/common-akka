package com.blackbaud.akka.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.FI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public abstract class Endpoint {

    public abstract ActorSystem getActorSystem();

    public <R> CompletableFuture<R> sendMessage(
            final Object newTopicMessage, final ActorRef actorRef, final Class<R> responseType) {
        final CompletableFuture<R> future = new CompletableFuture<>();
        final Props props = Props.create(InternalActor.class, future, newTopicMessage, actorRef, responseType);
        getActorSystem().actorOf(props);
        return future;
    }

    private static class InternalActor<R> extends AbstractActor {
        private CompletableFuture<Object> future;
        private Class<?> responseType;

        public InternalActor(
                final CompletableFuture<Object> future, final Object message, final ActorRef actorRef,
                final Class<?> responseType) {
            this.future = future;
            this.responseType = responseType;
            actorRef.tell(message, getSelf());
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .matchUnchecked(responseType, (FI.UnitApply<R>) this::processResponse)
                    .matchAny(this::error)
                    .build();
        }

        private void error(final Object o) {
            future.exceptionally(e -> {
                log.error("COULD NOT PROCESS MESSAGE {} ", o);
                shutdown();
                return e;
            });
        }

        private void processResponse(R o) {
            future.complete(o);
            shutdown();
        }

        private void shutdown() {
            getContext().stop(getSelf());
        }
    }

}
