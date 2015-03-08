package actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;
import messages.*;


public class MasterActor extends UntypedActor{
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void preStart() {
		log.info("MasterActor Starting");
	}
	
	ActorRef mapActor = getContext().actorOf( Props.create(MapActor.class).withRouter(
					new RoundRobinRouter(5)), "map");
	
	ActorRef reduceActor = getContext().actorOf( 
			Props.create(ReduceActor.class).withRouter(
			new RoundRobinRouter(5)), "reduce");
	
	ActorRef aggregateActor = getContext().actorOf(
			 Props.create(AggregateActor.class), "aggregate");
	
	@Override
	public void onReceive(Object message) throws Exception {
		if ( message instanceof String) {
			mapActor.tell(message, getSelf());
		} else if ( message instanceof MapData) {
			reduceActor.tell(message, getSelf());
		} else if ( message instanceof ReduceData) {
			aggregateActor.tell(message, getSelf());
		} else if ( message instanceof Result) {
			aggregateActor.forward(message, getContext());
		} else
			unhandled(message);
		
	}
}
