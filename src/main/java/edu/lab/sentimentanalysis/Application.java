package edu.lab.sentimentanalysis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Problem;
import edu.lab.sentimentanalysis.corpus.CorpusService;
import edu.lab.sentimentanalysis.document.Document;
import edu.lab.sentimentanalysis.document.DocumentService;
import edu.lab.sentimentanalysis.document.WeightCalculator;
import edu.lab.sentimentanalysis.ngram.ChiSqCalculator;
import edu.lab.sentimentanalysis.ngram.NGramService;
import edu.lab.sentimentanalysis.svmlib.Mapper;
import edu.lab.sentimentanalysis.svmlib.SVMClassifier;

public class Application {

	private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		Application app = new Application();
		System.out.println("Would you like to re-construct csv file (y/n): ");
		String construct = sc.next();
		if ("y".equalsIgnoreCase(construct)) {
			app.construct();
		}
		System.out.println("Would you like to test new data (y/n): ");
		String option = sc.next();
		if ("y".equalsIgnoreCase(option)) {
			app.testData(args);
		} else {
			app.init(args);
		}
	}

	private void testData(String[] args) {
		String type = args[1];
		String scheme = args[2];
		int noOfFeatures = Integer.valueOf(args[3]);

		LOGGER.info("Reading text from file.");
		String content = FileReaderWriter.read(FileFormat.TEST_DATA);
		// read ngrams
		List<String> ngrams = FileReaderWriter.readFile(String.format(FileFormat.VOCABULARY_NEW, type));
		List<Document> docs = prepare(content, type, ngrams, scheme);

		// output data
		List<Double> weights = docs.get(0).getWeights();
		StringBuilder sb = new StringBuilder();
		int size = weights.size();
		for (int i = 0; i < size; i++) {
			double weight = weights.get(i);
			if (weight != 0) {
				sb.append(String.format("%s: %.2f, ", ngrams.get(i), weight));
			}
		}
		FileReaderWriter.print("WEIGHTS", sb.toString(), Data.PRINT);
		FileReaderWriter.write(Data.OUTPUT_DATA.toString(), FileFormat.OUTPUT);

		// classify
		Mapper mapper = new Mapper();
		SVMClassifier classifier = new SVMClassifier();
		Feature[][] featureVec = mapper.mapTestData(docs, noOfFeatures);
		classifier.test(featureVec, String.format(FileFormat.MODEL_FILE, type, scheme));
	}

	private void init(String[] args) {
		String todo = args[0];
		String type = args[1];
		String scheme = args[2];
		int noOfFeatures = Integer.valueOf(args[3]);

		System.out.println(String.format("********** %s %s %s %d **********", todo, type, scheme, noOfFeatures));
		String fileName = String.format(FileFormat.MODEL_FILE, type, scheme);
//		fileName = fileName + "(e=0.001)";
		System.out.println(fileName);
		Mapper mapper = new Mapper();
		SVMClassifier classifier = new SVMClassifier();
		List<Document> docs;
		Problem problem;
		if ("train".contentEquals(todo)) {
			docs = prepareTrain(todo, type, scheme, noOfFeatures);
			problem = mapper.mapData(docs, noOfFeatures);
			classifier.train(problem, fileName);
		} else {
			docs = prepareTest(todo, type, scheme);
			problem = mapper.mapData(docs, noOfFeatures);
			classifier.test(problem, fileName);
		}
	}

	private List<Document> prepare(String content, String type, List<String> ngrams, String scheme) {
		// prepare documents
		DocumentService docService = new DocumentService();
		Document doc = docService.prepareDoc(content);
		// compute terms
		List<Document> docs = Arrays.asList(doc);
		docService.setWeights(docs, ngrams, WeightScheme.valueOf(scheme), type, false);
		return docs;
	}

	private List<Document> prepareTest(String todo, String type, String scheme) {
		// read data from csv file
		List<List<String>> data = extract(todo);
		// prepare documents
		DocumentService docService = new DocumentService();
		List<Document> docs = docService.prepareDocs(data);
		// read ngrams
		List<String> ngrams = FileReaderWriter.readFile(String.format(FileFormat.VOCABULARY_NEW, type));
		// compute terms
		docService.setWeights(docs, ngrams, WeightScheme.valueOf(scheme), type, false);
		return docs;
	}

	private List<Document> prepareTrain(String todo, String type, String scheme, int noOfFeatures) {
		// read data from csv file
		List<List<String>> data = extract(todo);
		// prepare documents
		DocumentService docService = new DocumentService();
		List<Document> docs = docService.prepareDocs(data);
		// compute ngrams
		NGramService ngramService = new NGramService();
		List<String> ngrams = ngramService.create(docs, type, String.format(FileFormat.VOCABULARY, type));

		// compute terms
		docService.setWeights(docs, ngrams, WeightScheme.valueOf(scheme), type, true);
		// compute chisq
		ChiSqCalculator cal = new ChiSqCalculator();
		Map<Integer, Double> idxToChiSq = cal.getIdxToChisq(docs, ngrams);
		// update document properties
		docs = docService.updateWeights(docs, idxToChiSq, noOfFeatures);
		// update ngram vocabulary
		List<String> newNgrams = ngramService.updateVocab(ngrams, idxToChiSq, noOfFeatures,
				String.format(FileFormat.VOCABULARY_NEW, type));
		List<Double> newIdfs = new WeightCalculator().calIdfs(docs, newNgrams);
		if (WeightScheme.TF_IDF.name().equalsIgnoreCase(scheme))
			FileReaderWriter.writeToFile(newIdfs, String.format(FileFormat.IDF, type));
		return docs;
	}

	private void construct() {
		CorpusService construct = new CorpusService();
		LOGGER.info("Constructing train and test data from Pang corpus.");
		construct.fromPangCorpus();
		LOGGER.info("Construction completed.");
	}

	private List<List<String>> extract(String type) {
		CorpusService service = new CorpusService();
		LOGGER.info("Extracting data from file.");
		List<List<String>> data = service.extract(type);
		LOGGER.info("Data extraction completed.");
		return data;
	}
}
