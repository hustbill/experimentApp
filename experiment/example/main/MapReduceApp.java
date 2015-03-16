package main;

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
import actor.MasterActor;
import actor.ReduceActor;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import static akka.pattern.Patterns.pipe;
import static akka.dispatch.Futures.future;

import java.util.concurrent.TimeUnit;

public class MapReduceApp {
	public static void main(String... args) throws Exception {
		long start;
		 long end;
		final Timeout timeout = new Timeout(Duration.create(1, TimeUnit.SECONDS));
		start = System.currentTimeMillis();
		final ActorSystem actorSystem = ActorSystem.create("MapReduceApp");
       // Thread.sleep(5000);
		final ActorRef master = actorSystem.actorOf(
				Props.create(MasterActor.class), "master");
		/*
		master.tell(
				"The quick brown fox tried to jump over the laze dog and fell on the dog",
				null);
		master.tell("Dog is man's best friend", null);
		master.tell("Dog and Fox belong to the same family", null);
		*/
		//Thread.sleep(5000);
		final ActorRef fileReadActor = actorSystem.actorOf(Props.create(
				FileReadActor.class));
		final String fileName = "Othello.txt";
	
		fileReadActor.tell(fileName , master);
		//Thread.sleep(5000);

		Future<Object> future = Patterns.ask(master, new Result(), timeout);
		String result = (String) Await.result(future, timeout.duration());
		
		System.out.println(result);
		
		end = System.currentTimeMillis();
		Duration duration = Duration.create(end - start, TimeUnit.MILLISECONDS);
			System.out
					.println(String
							.format("\n\tClientActor estimate: \t\t\n\tCalculation time: \t"+
									duration));
	    actorSystem.shutdown();
	}
	

}
