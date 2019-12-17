package edu.lab.sentimentanalysis.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.lab.sentimentanalysis.FileReaderWriter;

public class CorpusService {

	private static final Logger LOGGER = Logger.getLogger(CorpusService.class.getName());
	private static final String PANG_SRC = "corpus/pang";
	private static final String PANG_TRAIN = "files/pang_train.csv";
	private static final String PANG_TEST = "files/pang_test.csv";
	private static final String[] CORPUS_HEADERS = new String[] { "Content", "Label" };
	private static final int TRAIN_PER = 70;

	public void fromPangCorpus() {
		File posFolder = new File(getPath(PANG_SRC + "/pos"));
		File negFolder = new File(getPath(PANG_SRC + "/neg"));
		File[] posFiles = posFolder.listFiles();
		File[] negFiles = negFolder.listFiles();
		List<List<String>> posData = FileReaderWriter.readFiles(posFiles, "pos");
		List<List<String>> negData = FileReaderWriter.readFiles(negFiles, "neg");

		int posSize = posData.size();
		int negSize = negData.size();
		int posSplit = percent(posSize, TRAIN_PER);
		int negSplit = percent(negSize, TRAIN_PER);

		List<List<String>> train = new ArrayList<>();
		train.addAll(posData.subList(0, posSplit));
		train.addAll(negData.subList(0, negSplit));
		FileReaderWriter.writeToCSV(PANG_TRAIN, train, CORPUS_HEADERS);

		List<List<String>> test = new ArrayList<>();
		test.addAll(posData.subList(posSplit, posSize));
		test.addAll(negData.subList(negSplit, negSize));
		FileReaderWriter.writeToCSV(PANG_TEST, test, CORPUS_HEADERS);
		
		String info = "The size of %s data is %d.";
		LOGGER.info(String.format(info, "train", train.size()));
		LOGGER.info(String.format(info, "test", test.size()));
	}

	public List<List<String>> extract(String type) {
		String path = "train".equalsIgnoreCase(type) ? PANG_TRAIN : PANG_TEST;
		return FileReaderWriter.readCSV(path, CORPUS_HEADERS);
	}

	private int percent(int total, int n) {
		return total - (total - (total * n / 100));
	}

	private String getPath(String path) {
		ClassLoader loader = this.getClass().getClassLoader();
		return loader.getResource(path).getPath().replaceAll("%20", " ");
	}
}
