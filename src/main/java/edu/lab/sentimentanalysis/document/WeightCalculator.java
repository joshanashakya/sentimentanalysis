package edu.lab.sentimentanalysis.document;

import java.util.ArrayList;
import java.util.List;

public class WeightCalculator {

	/**
	 * \\bdog\\b matches dog in 'The dog plays in the yard.' (boundary match)
	 * 
	 * @param doc
	 * @param ngrams
	 * @return
	 */
	public List<Integer> calTermOccs(Document doc, List<String> ngrams) {
		String content = doc.getContent();
		List<Integer> termCount = new ArrayList<>();
		for (String ngram : ngrams) {
			int count = content.split(String.format("\\b%s\\b", ngram)).length - 1;
			termCount.add(count);
		}
		return termCount;
	}

	public List<Integer> calTermsFreqInDoc(List<Document> docs, List<String> ngrams) {
		List<Integer> termsFreq = new ArrayList<>();
		int count;
		for (String ngram : ngrams) {
			count = 0;
			for (Document doc : docs) {
				String content = doc.getContent();
				count = content.contains(ngram) ? count + 1 : count;
			}
			termsFreq.add(count);
		}
		return termsFreq;
	}

	public List<Double> calTfIdfs(List<Integer> termOccs, List<Double> idfs) {
		List<Double> tfIdfs = new ArrayList<>();
		int totalTerms = termOccs.stream().mapToInt(Integer::intValue).sum();
		int termOccsSize = termOccs.size();
		for (int i = 0; i < termOccsSize; i++) {
//			double tfIdf = calTfIdf(termOccs.get(i), totalTerms, idfs.get(i), noOfDocs);
			double tfIdf = termOccs.get(i)/(double) totalTerms * idfs.get(i);
			tfIdfs.add(tfIdf);
		}
		return tfIdfs;
	}

	/**
	 * 
	 * @param fij-the number of occurences of term i in document j
	 * @param fdj-the total numbers of terms occuring in document j
	 * @param fti-the total number of documents in which term i appears at
	 * @param d-total number of documents.
	 * @return
	 */
//	private double calTfIdf(int fij, int fdj, int fti, int d) {
//		return (fij / (double) fdj) * Math.log(d / (double) fti);
//	}

	public List<Double> calNormTfIdfs(List<Double> tfIdfs) {
		List<Double> normTfIdfs = new ArrayList<>();
		double sumSq = findSumSq(tfIdfs);
		for (Double tfIdf : tfIdfs) {
			normTfIdfs.add(tfIdf / sumSq);
		}
		return normTfIdfs;
	}

	private double findSumSq(List<Double> nums) {
		double sum = 0;
		for (Double num : nums) {
			sum = sum + num * num;
		}
		return sum;
	}

	public List<Integer> calBiOccs(List<Integer> termOccs) {
		List<Integer> biOccs = new ArrayList<>();
		for (Integer termOcc : termOccs) {
			biOccs.add(termOcc > 0 ? 1 : 0);
		}
		return biOccs;
	}

	public List<Double> calIdfs(List<Document> docs, List<String> ngrams) {
		int n = docs.size();
		List<Double> idfs = new ArrayList<>();
		for (String word : ngrams) {
			int count = getCount(docs, word);
			double idf = Math.log10(n / (double) count);
			idfs.add(idf);
		}
		return idfs;
	}

	private int getCount(List<Document> docs, String word) {
		int count = 0;
		for (Document doc : docs) {
			String text = doc.getContent();
			if (text.contains(word)) {
				count += 1;
			}
		}
		return count;
	}
}
