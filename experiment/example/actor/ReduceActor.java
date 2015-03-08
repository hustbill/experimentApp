package actor;

import java.util.*;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import messages.MapData;
import messages.ReduceData;
import messages.WordCount;

import command.Command;
import event.Event;

/**
 * The Reduce actor will go through the list of words and reduce
 * for duplicate words, and accordingly increase the number of 
 * instances counted for such words.
 * The reduced list is then sent back to the Master actor.
 * @author hua.zhang
 *
 */
public class ReduceActor extends UntypedActor {
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object msg) throws Exception {
		//log.info("Received string : " + msg);
		if (msg instanceof MapData) {
			MapData mapData = (MapData) msg;
			// reduce the incoming data and forward the result to Master actor
			getSender().tell(reduce(mapData.getDataList()), getSelf());			
		}  else 
			unhandled(msg);
	}
	
	/**
	 *  we extract the data list from the message and pass the same
	 *  	to the reduce() method, which reduces this list 
	 *  and returns back the ReduceData message, which is then passed on to the Master actor.
	 */
	private ReduceData reduce(List<WordCount> dataList) {
		HashMap<String, Integer> reducedMap = new HashMap<String, Integer>();
			for(WordCount wordCount : dataList) {
				if(reducedMap.containsKey(wordCount.getWord())) {
					Integer value = (Integer) 
							reducedMap.get(wordCount.getWord());
					value++;
					reducedMap.put(wordCount.getWord(), value);
				} else {
					reducedMap.put(wordCount.getWord(), Integer.valueOf(1));
				}
			}
		return new ReduceData(reducedMap);
	}

}
