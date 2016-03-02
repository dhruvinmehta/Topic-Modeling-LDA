package testing;

import java.io.File;

import topicModeling.LDA;
import documentHandling.Corpus;

public class ConsolTester {

	public static void main(String[] args) {
		double startTime = System.currentTimeMillis();
		File file;
		String[] entries;

		// delete previous results
		file = new File("results/word/");
		entries = file.list();
		if (entries != null) {
			for (String s : entries) {
				File currentFile = new File(file.getPath(), s);
				currentFile.delete();
			}
		}

		file = new File("results/topic/");
		entries = file.list();
		if (entries != null) {
			for (String s : entries) {
				File currentFile = new File(file.getPath(), s);
				currentFile.delete();
			}
		}

		// LDA(alpha, beta, no_of_iterations, topics)
		LDA lda = new LDA(0.5f, 0.1f, 100, 10);
		Corpus corpus = new Corpus();
		String path = "data/Documents/";
		corpus.getWords(path);
		System.out.println("Total Docs are : " + corpus.docs.size());
		lda.intializeAndRandomAssignment(corpus);
		lda.inference();
		double stopTime = System.currentTimeMillis();
		double elapsedTime = (stopTime - startTime) / 1000;
		System.out.println("Execution Time : " + elapsedTime + " seconds");
	}
}
