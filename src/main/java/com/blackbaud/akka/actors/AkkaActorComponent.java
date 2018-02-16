package com.blackbaud.akka.actors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public @interface AkkaActorComponent {
}
