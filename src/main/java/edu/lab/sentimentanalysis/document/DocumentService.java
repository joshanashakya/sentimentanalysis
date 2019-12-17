package edu.lab.sentimentanalysis.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.lab.sentimentanalysis.FileFormat;
import edu.lab.sentimentanalysis.FileReaderWriter;
import edu.lab.sentimentanalysis.Preprocessor;
import edu.lab.sentimentanalysis.Stopwords;
import edu.lab.sentimentanalysis.WeightScheme;

public class DocumentService {

	private static final Logger LOGGER = Logger.getLogger(DocumentService.class.getName());
	private static final String SRC = "stopwords.txt";

	public Document prepareDoc(String content) {
		Stopwords.populate(SRC);
		LOGGER.info("Populating stopwords completed");
		String processedContent = new Preprocessor().process(content);
		LOGGER.info("Preparing document completed.");
		return new Document(1, processedContent);
	}

	public List<Document> prepareDocs(List<List<String>> data) {
		Stopwords.populate(SRC);
		LOGGER.info("Populating stopwords completed");
		List<Document> docs = new ArrayList<>();
		int i = 1;
		for (List<String> d : data) {
			String content = d.get(0);
			String label = d.get(1);
			String processedContent = new Preprocessor().process(content);
			docs.add(new Document(i, processedContent, label));
			++i;
		}
		LOGGER.info("Preparing documents completed.");
		return docs;
	}

	public void setWeights(List<Document> docs, List<String> ngrams, WeightScheme weightType, String gramType, boolean isTrain) {
		LOGGER.info("Setting weights of documents.");
		switch (weightType) {
		case TF_IDF:
			List<Double> idfs;
			WeightCalculator cal = new WeightCalculator();
			if (isTrain) {
				idfs = cal.calIdfs(docs, ngrams);
			} else {
				idfs = FileReaderWriter.readDataFile(String.format(FileFormat.IDF, gramType));
			}
			setTfIdfs(docs, ngrams, idfs);
			break;
		case TERM_OCCUR:
			setTermOccs(docs, ngrams);
			break;
		case BI_OCCR:
			setBiOccs(docs, ngrams);
			break;
		}
	}

	public List<Document> updateWeights(List<Document> docs, Map<Integer, Double> idxToChiSq, int featureSize) {
		LOGGER.info("Updating document weights based on chi-square.");
		for (Document doc : docs) {
			List<Double> weights = doc.getWeights();
			List<Integer> termCounts = doc.getTermCounts();

			List<Double> newWeights = new ArrayList<>();
			List<Integer> newTermCounts = new ArrayList<>();
			int i = 1;
			for (Map.Entry<Integer, Double> entry : idxToChiSq.entrySet()) {
				if (i > featureSize) {
					break;
				}
				int idx = entry.getKey();
				newWeights.add(weights.get(idx));
				newTermCounts.add(termCounts.get(idx));
				i = i + 1;
			}
			doc.setWeights(newWeights);
			doc.setTermCounts(newTermCounts);
		}
		LOGGER.info("Completed updating document weights.");
		return docs;
	}

	public List<Integer> getBiOccs(Document doc) {
		List<Integer> biOccs = new ArrayList<>();
		List<Integer> termCounts = doc.getTermCounts();
		for (Integer count : termCounts) {
			biOccs.add(count > 0 ? 1 : 0);
		}
		return biOccs;
	}

	private void setTfIdfs(List<Document> docs, List<String> ngrams, List<Double> idfs) {
		WeightCalculator cal = new WeightCalculator();
		for (Document doc : docs) {
			List<Integer> termCounts = cal.calTermOccs(doc, ngrams);
			doc.setTermCounts(termCounts);
			List<Double> tfIdfs = cal.calTfIdfs(termCounts, idfs);
			List<Double> weights = cal.calNormTfIdfs(tfIdfs);
			doc.setWeights(weights);
		}
	}

	private void setTermOccs(List<Document> docs, List<String> ngrams) {
		WeightCalculator cal = new WeightCalculator();
		for (Document doc : docs) {
			List<Integer> termCounts = cal.calTermOccs(doc, ngrams);
			doc.setTermCounts(termCounts);
			doc.setWeights(cast(termCounts));
		}
	}

	private void setBiOccs(List<Document> docs, List<String> ngrams) {
		WeightCalculator cal = new WeightCalculator();
		for (Document doc : docs) {
			List<Integer> termCounts = cal.calTermOccs(doc, ngrams);
			doc.setTermCounts(termCounts);
			List<Integer> biOccs = getBiOccs(doc);
			doc.setWeights(cast(biOccs));
		}
	}

	private List<Double> cast(List<Integer> list1) {
		List<Double> list2 = new ArrayList<>();
		for (Integer l : list1) {
			list2.add(l.doubleValue());
		}
		return list2;
	}
}