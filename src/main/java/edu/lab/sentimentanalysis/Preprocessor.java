package edu.lab.sentimentanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.nlp.simple.Sentence;

public class Preprocessor {

	public String process(String content) {
		FileReaderWriter.print("ORIGINAL CONTENT", content, Data.PRINT);
		String tokens = tokenize(content);
		FileReaderWriter.print("AFTER TOKENIZATION", tokens, Data.PRINT);
		String text = removeStopwords(tokens);
		FileReaderWriter.print("AFTER REMOVING STOPWORDS", text, Data.PRINT);
		String lemmatizedText = lemmatize(text);
		FileReaderWriter.print("AFTER LEMMATIZATION", lemmatizedText, Data.PRINT);
		return lemmatizedText;
	}

	private String tokenize(String text) {
		return text.replaceAll("[^a-zA-Z ]", "").replaceAll("\\s+", " ").toLowerCase();
	}

	public String removeStopwords(String text) {
		ArrayList<String> textArr = Stream.of(text.split(" ")).collect(Collectors.toCollection(ArrayList<String>::new));
		textArr.removeAll(Stopwords.stop_words);
		return textArr.stream().collect(Collectors.joining(" "));
	}

	private String lemmatize(String text) {
		List<String> lemmas = getLemmas(text);
		// toLowerCase is used as getLemma turned 'me' to 'I'
		return lemmas.stream().collect(Collectors.joining(" ")).toLowerCase();
	}

	private List<String> getLemmas(String text) {
		Sentence sentence = new Sentence(text);
		return sentence.lemmas();
	}
}
