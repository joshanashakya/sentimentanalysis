package edu.lab.sentimentanalysis.ngram;

public enum GramType {
	UNIGRAM(1), BIGRAM(2), TRIGRAM(3);

	private int value;

	GramType(int value) {
		this.value = value;
	}

	public int valueOf() {
		return this.value;
	}
}
