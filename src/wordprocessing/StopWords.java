package wordprocessing;

import io.ReaderWriter;
import java.util.ArrayList;

public class StopWords {
	public ArrayList<String> stopwords;

	// read stopword from file and stores it in an arraylist
	public StopWords() {
		stopwords = new ArrayList<String>();
		ReaderWriter rw = new ReaderWriter();
		rw.readLines("data/StopWords.txt", stopwords);
	}

	public boolean isStopWord(String word) {
		if (stopwords.contains(word))
			return true;
		else
			return false;
	}
}
