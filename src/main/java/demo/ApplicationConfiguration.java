package demo;

import akka.actor.ActorSystem;
import com.blackbaud.akka.actors.ShardRegions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@Import(ShardRegions.class)
@PropertySource("application.properties")
public class ApplicationConfiguration {

    @Autowired
    private ShardRegions shardRegions;

    @Value("${akka-cluster-name}")
    private String clusterName;

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create(clusterName);
    }

    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load();
    }

    @PostConstruct
    public void init() {
        shardRegions.startActor(actorSystem(), TopicActor.class, TopicMessageExtractor.class);
    }

}
