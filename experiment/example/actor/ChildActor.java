package actor;


import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;


/**
 * Child Actor receives an Event from its Parent and logs out the message.
 */
public class ChildActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() {
        log.info("Starting");
    }

    @Override
    public void onReceive(Object msg) {
        log.info("Received Event: " + msg);
    }


}
