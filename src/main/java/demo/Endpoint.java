package demo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.FI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class Endpoint {

    private ApplicationConfiguration applicationConfiguration;

    @Autowired
    public Endpoint(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public <R> CompletableFuture<R> sendMessage(Object newTopicMessage, ActorRef actorRef, Class<R> responseType) {
        final CompletableFuture<R> future = new CompletableFuture<>();
        final Props props = Props.create(InternalActor.class, future, newTopicMessage, actorRef, responseType);
        applicationConfiguration.actorSystem().actorOf(props);
        return future;
    }

    private static class InternalActor<R> extends AbstractActor {
        private CompletableFuture<Object> future;
        private Class<?> responseType;

        public InternalActor(CompletableFuture<Object> future, Object message, ActorRef actorRef, Class<?> responseType) {
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

        private void error(Object o) {
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
