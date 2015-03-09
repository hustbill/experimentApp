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

import com.typesafe.config.ConfigFactory;

public class MapReduceApp {
	
	
	public static void main(String... args) throws Exception {
		long start;
		 long end;
		final Timeout timeout = new Timeout(Duration.create(50, TimeUnit.SECONDS));
		
		//final ActorSystem actorSystem = ActorSystem.create("MapReduceAppSystem");
		  final ActorSystem actorSystem = ActorSystem.create("MapReduceAppSystem",
			        ConfigFactory.load(("metaActor")));
		
        Thread.sleep(5000);
        start = System.currentTimeMillis();
		final ActorRef metaActor = actorSystem.actorOf(
				Props.create(MetaActor.class), "metaActor");
		
		
//		 final ActorSystem system = ActorSystem.create("CalculatorSystem",
//			        ConfigFactory.load(("calculator")));
//			    system.actorOf(Props.create(CalculatorActor.class), "calculator");
	    final ActorRef  mapActor = actorSystem.actorOf(Props.create(MapActor.class), "mapActor");
	    
//		 final ActorSystem system = ActorSystem.create("LookupSystem",
//			        ConfigFactory.load("remotelookup"));
//			    final String path = "akka.tcp://MapReduceAppSystem@127.0.0.1:2552/user/metaActor";
//			    final ActorRef mapActor = system.actorOf(
//			        Props.create(MapActor.class, path), "mapActor");
	    
		    
		//final String fileName = "Othello.txt";
	    final String fileName = "book4.txt";
		// read file
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(Thread.currentThread()
							.getContextClassLoader().getResource(fileName)
							.openStream()));
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
		Duration duration = Duration.create(end - start-5000, TimeUnit.MILLISECONDS);
			System.out
					.println(String
							.format("\n\tClientActor estimate: \t\t\n\tCalculation time: \t"+
									duration));
	    //actorSystem.shutdown();
	}
	

}
