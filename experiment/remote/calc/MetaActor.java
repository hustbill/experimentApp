package calc;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;
import messages.*;
import actor.*;


public class MetaActor extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void preStart() {
		log.info("MetaActor Starting");
	}
	
//	ActorRef mapActor = getContext().actorOf( Props.create(MapActor.class).withRouter(
//					new RoundRobinRouter(5)), "map");
//	
//	ActorRef reduceActor = getContext().actorOf( 
//			Props.create(ReduceActor.class).withRouter(
//			new RoundRobinRouter(5)), "reduce");
//	
//	 final ActorRef mapActor = getContext().actorOf(Props.create(CalculatorActor.class), "mapActor");
//	
//    final String path = "akka.tcp://CalculatorSystem@127.0.0.1:2752/user/mapActor";
//    final ActorRef reduceActor = getContext().actorOf(
//    		Props.create(MapActor.class, path), "reduceActor");
	 
//    final ActorSystem reduceSystem = ActorSystem.create("ReduceActorSystem",
//	        ConfigFactory.load("remotelookup"));
//	    final String path = "akka.tcp://CalculatorSystem@127.0.0.1:2752/user/mapActor";
//	    final ActorRef reduceActor = getContext().actorOf(
//	    		Props.create(ReduceActor.class, path), "reduceActor");
	
	ActorRef reduceActor = getContext().actorOf(
			 Props.create(ReduceActor.class), "reduceActor");
	
	 	
	ActorRef aggregateActor = getContext().actorOf(
			 Props.create(AggregateActor.class), "aggregate");
	
	@Override
	public void onReceive(Object message) throws Exception {
//		if ( message instanceof String) {
//			mapActor.tell(message, getSelf());
//		} else 
			
			if ( message instanceof MapData) {
			reduceActor.tell(message, getSelf());
		} else if ( message instanceof ReduceData) {
			aggregateActor.tell(message, getSelf());
		} else if ( message instanceof Result) {
			aggregateActor.forward(message, getContext());
		} else
			unhandled(message);
		
	}
}
