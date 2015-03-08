package messages;

/**
 * The WordCount object holds the word along with the associated number of 
 * instances occurring within the sentence.
 * @author hua.zhang
 *
 */
public final class WordCount {
	private final String word;
	private final Integer count;
	
	public WordCount(String inWord, Integer inCount) {
		word = inWord;
		count = inCount;
	}
	
	public String getWord() {
		return word;
	}
	
	public Integer getCount() {
		return count;
	}
}
