package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import wordprocessing.StopWords;
import wordprocessing.Token;

public class ReaderWriter {
	FileWriter writer = null;

	public void readLines(String doc, ArrayList<String> lines) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(doc)));

			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void readWords(ArrayList<String> lines, ArrayList<String> words) {
		StopWords s = new StopWords();
		ArrayList<String> temp = new ArrayList<String>();
		// PorterStemmer p = new PorterStemmer();
		for (String line : lines) {
			Token.tokenizeAndLowerCase(line, temp);
		}

		for (String word : temp) {
			if (!s.isStopWord(word)) {
				// word = p.stem(word);
				words.add(word);
			}
		}
	}
}
