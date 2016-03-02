package documentHandling;

import io.ReaderWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Corpus {
	public ArrayList<Document> docs;
	public Map<String, Integer> wordToIndexMap;
	public ArrayList<String> indexToWordMap;

	public Corpus() {
		docs = new ArrayList<Document>();
		wordToIndexMap = new HashMap<String, Integer>();
		indexToWordMap = new ArrayList<String>();
	}

	public void getWords(String docPath) {
		for (File doc : new File(docPath).listFiles()) {
			Document doc1 = new Document(doc.getAbsolutePath(), wordToIndexMap,
					indexToWordMap);
			int index = doc.getName().lastIndexOf(".");
			if (index > 0)
				doc1.docname = doc.getName().substring(0, index);
			else
				doc1.docname = doc.getName();
			docs.add(doc1);
		}
		saveVocab();
	}

	public void saveVocab() {
		FileWriter writer = null;
		String path = "results/vocabulary/";
		new File(path).mkdirs();
		try {
			// throws file not found exception
			writer = new FileWriter(path + "vocabulary.txt");
			for (int i = 0; i < wordToIndexMap.size(); i++) {
				// throws IOException
				writer.write(indexToWordMap.get(i) + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class Document {
		public int[] docWords;
		public String docname;
		ReaderWriter rw = new ReaderWriter();

		public Document(String docName, Map<String, Integer> wordToIndexMap,
				ArrayList<String> indexToWordMap) {

			ArrayList<String> lines = new ArrayList<String>();
			ArrayList<String> words = new ArrayList<String>();

			rw.readLines(docName, lines);
			rw.readWords(lines, words);

			this.docWords = new int[words.size()];

			for (int i = 0; i < words.size(); i++) {
				String word = words.get(i);
				if (!wordToIndexMap.containsKey(word)) {
					int newIndex = wordToIndexMap.size();
					wordToIndexMap.put(word, newIndex);
					indexToWordMap.add(word);
					docWords[i] = newIndex;
				} else {
					docWords[i] = wordToIndexMap.get(word);
				}
			}
			lines.clear();
			words.clear();
		}
	}
}
