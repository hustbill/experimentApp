package main;

import actor.ReduceActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import command.Command;
import event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class MainApp {
	public static final Logger log = LoggerFactory.getLogger(System.class);

    public static void main(String... args) throws Exception {

        final ActorSystem actorSystem = ActorSystem.create("actor-system");

        Thread.sleep(5000);

        final ActorRef actorRef = actorSystem.actorOf(Props.create(ReduceActor.class), "reducer-actor");

        actorRef.tell(new Command("CMD 1"), null);
        actorRef.tell(new Command("CMD 2"), null);
        actorRef.tell(new Command("CMD 3"), null);
        actorRef.tell("echo", null);
        actorRef.tell(new Command("CMD 4"), null);
        actorRef.tell(new Command("CMD 5"), null);

        Thread.sleep(5000);

        log.debug("Actor System Shutdown Starting...");

        actorSystem.shutdown();
    }
}
//reference :  https://github.com/royrusso/akka-java-examples
/*
Akka Parent-Child Actors

parent-child : This example illustrates how you can configure an Akka cluster for hierarchical Actor relationships.
 This cluster contains Parent Actors that, given a Command, send an Event to a Child Actor for processing.

*/