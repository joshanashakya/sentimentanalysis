package edu.lab.sentimentanalysis;

import java.util.List;

public class Stopwords {
	
	public static List<String> stop_words; 
	
	public static void populate(String path) {
		stop_words = FileReaderWriter.readFile(path);
	}
}
