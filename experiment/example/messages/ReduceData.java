package messages;

import java.util.HashMap;

/**
 * ReduceData is the message passed between the Reduce actor
 * and the Aggregate actor.
 * @author hua.zhang
 *
 */
public final class ReduceData {
	private final HashMap<String, Integer> reduceDataList; 
	public HashMap<String, Integer> getReduceDataList(){
		return reduceDataList;
	}
	public ReduceData(HashMap<String, Integer> reduceDataList) {
		this.reduceDataList = reduceDataList;
	}
}
