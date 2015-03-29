package actor;

import java.util.*;
import java.util.Map.Entry;

import akka.actor.UntypedActor;
import  messages.*;

/** 
 * Aggregate actor receives the reduced data list from the Master actor 
 * and aggregates it into one big list. Aggregate actor will maintain a state variable that will 
 * hold the list of words and get updated on receipt of the reduced data list message
 * @author hua.zhang
 *
 */
public class AggregateActor extends UntypedActor {
	private Map<String, Integer> finalReducedMap = 
			new HashMap<String, Integer>();

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof ReduceData) {
			ReduceData reduceData = (ReduceData) message;
			aggregateInMemoryReduce(reduceData.getReduceDataList());
		} else if (message instanceof Result) {
			getSender().tell(finalReducedMap.toString(), getSelf());
			  //System.out.println(entriesSortedByValues(finalReducedMap));		
		} else
			unhandled(message);
	}
	
	/**
	 * add the data to the existing data set stored in the 
	 * finalReducedMap variable.
	 * @param reducedList
	 */
	private void aggregateInMemoryReduce(Map<String, Integer> reducedList) {
		Integer count = null;
		for (String key : reducedList.keySet()) {
			if (finalReducedMap.containsKey(key)) {
				count = reducedList.get(key) + 
						finalReducedMap.get(key);
				finalReducedMap.put(key, count);
			} else {
				finalReducedMap.put(key, reducedList.get(key));
			}
		}
	}
	
	//Refer to http://stackoverflow.com/questions/11647889/sorting-the-mapkey-value-in-descending-order-based-on-the-value
	static <K,V extends Comparable<? super V>>  List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		Collections.sort(sortedEntries, 
				new Comparator<Entry<K,V>>() {
					public int compare(Entry<K,V> e1, Entry<K,V> e2) {
					return e2.getValue().compareTo(e1.getValue());
						}
			});
		return sortedEntries;
     }

}
