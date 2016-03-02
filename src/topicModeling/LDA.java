package topicModeling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import documentHandling.Corpus;

public class LDA {
	int K, D, V; // total no of topics, documents, vocabulary
	int iteration; // no of iteration
	int[][] doc; // word index
	int[][] z; // topic assignment D*N
	float alpha; // document-topic dirichlet prior parameter
	float beta; // topic-word dirichlet prior parameter
	int[][] ndk; // the number of words assigned to topic k in document d. D*K
	int[][] nkw; // the number of times word w is assigned to topic k. K*V
	int[] nk; // total no of words in topic
	int[] ndkSum; // total no of words in document
	double[][] theta; // Parameters for doc-topic distribution D*K
	double[][] phi; // Parameters for topic-word distribution K*V
	Corpus corpus;

	public LDA() {
	}

	public LDA(float alpha, float beta, int iteration, int K) {
		this.alpha = alpha;
		this.beta = beta;
		this.iteration = iteration;
		this.K = K;
	}

	public void intializeAndRandomAssignment(Corpus corpus) {
		this.corpus = corpus;
		int N, initTopic;
		D = corpus.docs.size();
		V = corpus.wordToIndexMap.size();
		ndk = new int[D][K];
		nkw = new int[K][V];
		ndkSum = new int[D];
		nk = new int[K];
		z = new int[D][];
		doc = new int[D][];
		phi = new double[K][V];
		theta = new double[D][K];

		for (int d = 0; d < D; d++) {
			N = corpus.docs.get(d).docWords.length;

			doc[d] = new int[N];
			for (int n = 0; n < N; n++) {
				doc[d][n] = corpus.docs.get(d).docWords[n];
			}
		}

		for (int d = 0; d < D; d++) {
			N = corpus.docs.get(d).docWords.length;
			z[d] = new int[N];

			for (int n = 0; n < N; n++) {
				initTopic = (int) (Math.random() * K);
				z[d][n] = initTopic;

				// System.out.println(corpus.indexToWordMap.get(doc[d][n])
				// + " -> topic " + z[d][n]);
				ndk[d][initTopic]++;
				nkw[initTopic][doc[d][n]]++;
				nk[initTopic]++;
			}
			ndkSum[d] = N;
		}
	}

	public void inference() {
		int newTopic, N;
		for (int i = 0; i < iteration; i++) {
			for (int d = 0; d < D; d++) {
				N = doc[d].length;
				for (int n = 0; n < N; n++) {
					newTopic = getSample(d, n);
					z[d][n] = newTopic;
					// System.out.println(phi[d][n]);
				}
			}
			updateEstimatedParameters();
			System.out.println("Iteration " + (i + 1));
			if(i == 0) {
				try {
					csvTopicWriter("random");
					csvWordWriter("random");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			saveOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Output Saved");
	}

	public int getSample(int d, int n) {
		double[] p = new double[K];
		int oldTopic, newTopic;
		double u;

		oldTopic = z[d][n];
		ndk[d][oldTopic]--;
		nkw[oldTopic][doc[d][n]]--;
		ndkSum[d]--;
		nk[oldTopic]--;

		for (int k = 0; k < K; k++) {
			p[k] = (nkw[k][doc[d][n]] + beta) / (nk[k] + V * beta)
					* (ndk[d][k] + alpha) / (ndkSum[d] + K * alpha);
			// System.out.println("topic " + k + " " + p[k]);
		}

		for (int k = 1; k < K; k++) {
			p[k] += p[k - 1];
		}

		u = Math.random() * p[K - 1];
		for (newTopic = 0; newTopic < K; newTopic++) {
			if (u < p[newTopic]) {
				break;
			}
		}

		ndk[d][newTopic]++;
		nkw[newTopic][doc[d][n]]++;
		ndkSum[d]++;
		nk[newTopic]++;
		return newTopic;
	}

	private void updateEstimatedParameters() {
		// TODO Auto-generated method stub
		for (int k = 0; k < K; k++) {
			for (int n = 0; n < V; n++) {
				phi[k][n] = (nkw[k][n] + beta) / (nk[k] + V * beta);
			}
		}

		for (int d = 0; d < D; d++) {
			for (int k = 0; k < K; k++) {
				theta[d][k] = (ndk[d][k] + alpha) / (ndkSum[d] + K * alpha);
			}
		}
	}

	public void saveOutput() throws IOException {
		FileWriter writer = null;
		String path;
		String formatted;

		// write topic document assignment
		path = "results/topic/";
		new File(path).mkdirs();
		for (int d = 0; d < D; d++) {
			writer = new FileWriter(path + "Topic proportion "
					+ corpus.docs.get(d).docname + ".txt");
			for (int k = 0; k < K; k++) {
				formatted = String.format("%-15s\t%s", "topic" + (k + 1),
						theta[d][k] + "\n");
				writer.write(formatted);
			}
			writer.close();
		}

		// write topic to word assignment
		path = "results/word/";
		new File(path).mkdirs();

		for (int k = 0; k < K; k++) {
			writer = new FileWriter(path + "Words Topic " + (k + 1) + ".txt");
			ArrayList<Integer> sortedWordsIndexArray = new ArrayList<Integer>();
			for (int j = 0; j < V; j++) {
				sortedWordsIndexArray.add(new Integer(j));
			}
			Collections.sort(sortedWordsIndexArray, new LDA.wordsComparable(
					phi[k]));
			for (int t = 0; t < V; t++) {
				formatted = String
						.format("%-15s\t%s", corpus.indexToWordMap
								.get(sortedWordsIndexArray.get(t)),
								phi[k][sortedWordsIndexArray.get(t)] + "\n");
				writer.write(formatted);
			}
			writer.close();
		}
		csvTopicWriter("final");
		csvWordWriter("final");
	}

	public void csvTopicWriter(String state) throws IOException {
		FileWriter w;
		int d, k;
		w = new FileWriter("results/Topic_" + state + ".csv");
		for (k = -1; k < K; k++) {
			if (k == -1) {
				w.append("topic/doc,");
				for (d = 0; d < (D - 1); d++) {
					w.append(corpus.docs.get(d).docname + ",");
				}
				w.append(corpus.docs.get(d).docname + "\n");
			} else {
				w.append("topic " + (k + 1) + ",");
				for (d = 0; d < (D - 1); d++) {
					w.append(theta[d][k] + ",");
				}
				w.append(theta[d][k] + "\n");
			}
		}
		w.close();
	}

	public void csvWordWriter(String state) throws IOException {
		FileWriter w;
		int k, n;
		w = new FileWriter("results/Word_" + state + ".csv");
		for (n = -1; n < V; n++) {
			if (n == -1) {
				w.append("word/topic,");
				for (k = 0; k < (K - 1); k++) {
					w.append("topic " + (k + 1) + ",");
				}
				w.append("topic " + (k + 1) + "\n");
			} else {
				w.append(corpus.indexToWordMap.get(n) + ",");
				for (k = 0; k < (K - 1); k++) {
					w.append(phi[k][n] + ",");
				}
				w.append(phi[k][n] + "\n");
			}
		}
		w.close();
	}

	public class wordsComparable implements Comparator<Integer> {

		public double[] sortProb; // Store probability of each word in topic k

		public wordsComparable(double[] sortProb) {
			this.sortProb = sortProb;
		}

		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			// Sort topic word index according to the probability of each word
			// in topic k
			if (sortProb[o1] > sortProb[o2])
				return -1;
			else if (sortProb[o1] < sortProb[o2])
				return 1;
			else
				return 0;
		}
	}
}