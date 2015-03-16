package main;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import actor.ParentActor;
import command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main runtime class.
 */
public class ParentChildApp {

    public static final Logger log = LoggerFactory.getLogger(System.class);

    public static void main(String... args) throws Exception {

        final ActorSystem actorSystem = ActorSystem.create("actor-system");

        Thread.sleep(5000);

        final ActorRef actorRef = actorSystem.actorOf(Props.create(ParentActor.class), "parent-actor");

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
