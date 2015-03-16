package calc;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import command.Command;
import messages.Result;
//import scala.concurrent.Future;
//import scala.concurrent.duration.Duration;
//import actor.MasterActor;
//import actor.ReduceActor;
//import akka.actor.ActorRef;
//
//import akka.actor.Props;
//import akka.dispatch.*;
//import akka.pattern.Patterns;
//import akka.util.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import actor.FileReadActor;
import actor.MapActor;
import actor.MasterActor;
import actor.ParentActor;
import actor.ReduceActor;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import static akka.pattern.Patterns.pipe;
import static akka.dispatch.Futures.future;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

public class mainEntry {
	
	//public static final Logger log = LoggerFactory.getLogger(System.class);

	public static void main(String[] args) throws Exception {
		
		String fileName = "Othello.txt";
		if(args[0] != null) {
			System.out.println(args[0] + "");
			fileName = args[0];
		}
		
		// start caculator-lookup application 
		startCalculatorLookupApp();
		
		// start wordcount application 
		startWordCountApp(args[0]);
		
		// start parent-child application 
		//startParentChildApp();


		
	}

	public static void startWordCountApp(String fileName)
			throws Exception {

		long start;
		long end;
		final Timeout timeout = new Timeout(Duration.create(50,
				TimeUnit.SECONDS));

		final ActorSystem actorSystem = ActorSystem.create(
				"MapReduceAppSystem", ConfigFactory.load(("metaActor")));

		Thread.sleep(5000);
		start = System.currentTimeMillis();
		final ActorRef metaActor = actorSystem.actorOf(
				Props.create(MetaActor.class), "metaActor");

		final ActorRef mapActor = actorSystem.actorOf(
				Props.create(MapActor.class), "mapActor");

		// read file
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					Thread.currentThread().getContextClassLoader()
							.getResource(fileName).openStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				mapActor.tell(line, metaActor);
			}
			System.out.println("All lines send !");
			// send the EOF message..
			mapActor.tell(String.valueOf("EOF"), metaActor);
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		Thread.sleep(5000);

		Future<Object> future = Patterns.ask(metaActor, new Result(), timeout);
		String result = (String) Await.result(future, timeout.duration());

		System.out.println(result);

		end = System.currentTimeMillis();
		Duration duration = Duration.create(end - start - 5000,
				TimeUnit.MILLISECONDS);
		System.out
				.println(String
						.format("\n\tClientActor estimate: \t\t\n\tCalculation time: \t"
								+ duration));
		if (result != null)
			actorSystem.shutdown();

	}

	public static void startParentChildApp() throws InterruptedException {
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

	        //log.debug("Actor System Shutdown Starting...");

	        actorSystem.shutdown();
	}
	
	public static void startCalculatorLookupApp(){
		
		// if (args.length == 0 || args[0].equals("Calculator"))
		      startRemoteCalculatorSystem();
		  //  if (args.length == 0 || args[0].equals("Lookup"))
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
	    final String path = "akka.tcp://CalculatorSystem@ecs301c-7.labs.encs:2852/user/calculator";
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
