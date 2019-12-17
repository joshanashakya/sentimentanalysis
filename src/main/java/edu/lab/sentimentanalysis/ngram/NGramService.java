package edu.lab.sentimentanalysis.ngram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.lab.sentimentanalysis.FileReaderWriter;
import edu.lab.sentimentanalysis.document.Document;

public class NGramService {

	public List<String> updateVocab(List<String> ngrams, Map<Integer, Double> idxToChiSq, int featureSize,
			String path) {
		int i = 1;
		List<String> newNgrams = new ArrayList<>();
		for (Map.Entry<Integer, Double> entry : idxToChiSq.entrySet()) {
			if (i > featureSize) {
				break;
			}
			int idx = entry.getKey();
			newNgrams.add(ngrams.get(idx));
			i = i + 1;
		}
		FileReaderWriter.writeToFile(newNgrams, path);
		return newNgrams;
	}

	public List<String> create(List<Document> docs, String type, String path) {
		List<String> ngrams = create(docs, GramType.valueOf(type));
		FileReaderWriter.writeToFile(ngrams, path);
		return ngrams;
	}

	public List<String> create(Document doc, GramType gram) {
		Set<String> ngrams = new HashSet<>();
		String content = doc.getContent();
		String[] words = content.split(" ");
		switch (gram) {
		case UNIGRAM:
			ngrams.addAll(Arrays.asList(words));
			break;
		case BIGRAM:
			ngrams.addAll(getBigrams(words));
			break;
		case TRIGRAM:
			ngrams.addAll(getTrigrams(words));
			break;
		}
		List<String> ordNgrams = new ArrayList<>(ngrams);
		Collections.sort(ordNgrams);
		return ordNgrams;
	}

	public List<String> create(List<Document> docs, GramType gram) {
		Set<String> ngrams = new HashSet<>();
		for (Document doc : docs) {
			String content = doc.getContent();
			String[] words = content.split(" ");
			switch (gram) {
			case UNIGRAM:
				ngrams.addAll(Arrays.asList(words));
				break;
			case BIGRAM:
				ngrams.addAll(getBigrams(words));
				break;
			case TRIGRAM:
				ngrams.addAll(getTrigrams(words));
				break;
			}
		}
		List<String> ordNgrams = new ArrayList<>(ngrams);
		Collections.sort(ordNgrams);
		return ordNgrams;
	}

	private Set<String> getBigrams(String[] words) {
		Set<String> bigrams = new HashSet<>();
		int size = words.length;
		for (int i = 0; i < size - 1; i++) {
			String phrase = String.format("%s %s", words[i], words[i + 1]);
			bigrams.add(phrase);
		}
		return bigrams;
	}

	private Set<String> getTrigrams(String[] words) {
		Set<String> trigrams = new HashSet<>();
		int size = words.length;
		for (int i = 0; i < size - 2; i++) {
			String phrase = String.format("%s %s %s", words[i], words[i + 1], words[i + 2]);
			trigrams.add(phrase);
		}
		return trigrams;
	}
}
