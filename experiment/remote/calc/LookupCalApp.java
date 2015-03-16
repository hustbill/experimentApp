package calc;

import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Random;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

import actor.*;
import messages.*;


public class LookupCalApp {
  public static void main(String[] args) {
    if (args.length == 0 || args[0].equals("Calculator"))
      startRemoteCalculatorSystem();
    if (args.length == 0 || args[0].equals("Lookup"))
      startRemoteLookupSystem();
  }

  public static void startRemoteCalculatorSystem() {
    final ActorSystem system = ActorSystem.create("CalculatorSystem",
        ConfigFactory.load(("calculator")));
    system.actorOf(Props.create(CalculatorActor.class), "calculator");
    System.out.println("Started CalculatorSystem");
  }

  public static void startRemoteLookupSystem() {

    final ActorSystem system = ActorSystem.create("LookupSystem",
        ConfigFactory.load("remotelookup"));
    final String path = "akka.tcp://CalculatorSystem@127.0.0.1:2752/user/calculator";
    final ActorRef actor = system.actorOf(
    		Props.create(LookupActor.class, path), "lookupActor");
    final ActorRef actor2 = system.actorOf(
            Props.create(LookupActor.class, path), "lookupActor2");

    
    System.out.println("Started LookupSystem");
    final Random r = new Random();
    system.scheduler().schedule(Duration.create(1, SECONDS),
        Duration.create(1, SECONDS), new Runnable() {
          @Override
          public void run() {
            if (r.nextInt(100) % 2 == 0) {
              actor.tell(new Op.Add(r.nextInt(100), r.nextInt(100)), null);
              actor2.tell(new Op.Add(r.nextInt(100), r.nextInt(100)), null);
            } else {
              actor.tell(new Op.Subtract(r.nextInt(100), r.nextInt(100)), null);
              actor2.tell(new Op.Subtract(r.nextInt(100), r.nextInt(100)), null);
            }

          }
        }, system.dispatcher());

  }
}
