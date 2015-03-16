package actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import messages.MapData;
import messages.WordCount;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@SuppressWarnings("unused")

/**
 * MapAcor identify the words in the sentence,
 * Once finished, MapActor will send the mapped data list to Master actor, who 
 * will send it to Reduce actor
 * 
 * @author hua.zhang
 *
 */
public class MapActor extends UntypedActor {
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  //private final String path;
	  private ActorRef metaActor = null;
	
	@Override
	public void preStart() {
		log.info("MapActor Starting");
	}

//	public MapActor(String path) {
//		    this.path = path;
//		   
//		  }
	  
	@Override
	public void onReceive(Object msg) throws Exception {
		//log.info("Received Event: " + msg);
		if(msg instanceof String) {
			String work = (String)msg;
			// map the words in the sentence and send the result to MasterActor
			getSender().tell(evaluateExpression(work), getSelf());
		} else
			unhandled(msg);
	}
	
	/**
	 * logic to map the words in the sentences
	 * @param line
	 * @return
	 */
	private MapData evaluateExpression(String line) {
		List<WordCount> dataList = new ArrayList<WordCount>();
		// use StringTokenizer to break down the string into individual words
		StringTokenizer parser = new StringTokenizer(line);
		while (parser.hasMoreTokens()) {
			String word = parser.nextToken().toLowerCase();
			if (isAlpha(word)) {
					dataList.add(new WordCount(word, Integer.valueOf(1)));
			}
		}
		return new MapData(dataList);
		
	}
	
	private boolean isAlpha(String s) {
		s = s.toUpperCase();
		for (int i = 0; i < s.length(); i++) {
			int c = (int) s.charAt(i);
			if (c < 65 || c > 90)
				return false;
		}
		return true;
	}


}
