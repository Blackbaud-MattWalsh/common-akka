#!/bin/sh
java\
    -Dakka.actor.provider=cluster\
    -Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")"\
    -Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT"\
    $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done)\
    -DactorSystemName=${AKKA_ACTOR_SYSTEM_NAME}\
    -Dakka.io.dns.resolver=async-dns\
    -Dakka.io.dns.async-dns.resolve-srv=true\
    -Dakka.io.dns.async-dns.resolv-conf=on\
    -Dserver.port=9001\
    -Djava.security.egd=file:/dev/./urandom\
    -jar akkademo.jar