package edu.lab.sentimentanalysis.ngram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.lab.sentimentanalysis.common.Utilities;
import edu.lab.sentimentanalysis.document.Document;
import edu.lab.sentimentanalysis.document.DocumentService;

public class ChiSqCalculator {

	private static final Logger LOGGER = Logger.getLogger(ChiSqCalculator.class.getName());

	public Map<Integer, Double> getIdxToChisq(List<Document> docs, List<String> ngrams) {
		LOGGER.info("Computing chi-square.");
		int noOfDocs = docs.size();
		int ngramsSize = ngrams.size();

		// prepare items needed to compute chi-square
		ChiSqProperty chiSqProperty = prepare(docs, ngramsSize);

		// compute chi-square
		Map<Integer, Double> idxToChiSq = new HashMap<>();
		int noOfPosDocs = chiSqProperty.noOfPosDocs;
		int noOfNegDocs = chiSqProperty.noOfNegDocs;
		for (int i = 0; i < ngramsSize; i++) {
			int occ = chiSqProperty.occs.get(i);
			int nocc = chiSqProperty.noccs.get(i);
			int posOcc = chiSqProperty.posOccs.get(i);
			int posNocc = chiSqProperty.posNoccs.get(i);
			int negOcc = chiSqProperty.negOccs.get(i);
			int negNocc = chiSqProperty.negNoccs.get(i);
			double chiSq = computeChisq(noOfDocs, noOfPosDocs, noOfNegDocs, occ, nocc, posOcc, posNocc, negOcc,
					negNocc);
			idxToChiSq.put(i, chiSq);
		}

		// sort chi-square according to the value (ranking)
		idxToChiSq = Utilities.sortByValue(idxToChiSq);

		LOGGER.info("Completed chi-square computation.");
		return idxToChiSq;
	}

	private ChiSqProperty prepare(List<Document> docs, int ngramsSize) {
		// calculate number of +ve and -ve documents
		int noOfPosDocs = 0;
		int noOfNegDocs = 0;
		for (Document doc : docs) {
			String label = doc.getLabel();
			if (label.equalsIgnoreCase("pos")) {
				++noOfPosDocs;
			} else {
				++noOfNegDocs;
			}
		}

		// calculate number of +ve and -ve documents in which ngram terms occur
		List<Integer> posOccs = new ArrayList<>();
		List<Integer> negOccs = new ArrayList<>();
		DocumentService docService = new DocumentService();
		for (Document doc : docs) {
			String label = doc.getLabel();
			List<Integer> biOccs = docService.getBiOccs(doc);
			if (label.equalsIgnoreCase("pos")) {
				posOccs = posOccs.isEmpty() ? biOccs : add(posOccs, biOccs, ngramsSize);
			} else {
				negOccs = negOccs.isEmpty() ? biOccs : add(negOccs, biOccs, ngramsSize);
			}
		}

		// calculate number of +ve and -ve documents in which ngram terms do not occur
		List<Integer> posNoccs = subFromTotal(noOfPosDocs, posOccs, ngramsSize);
		List<Integer> negNoccs = subFromTotal(noOfNegDocs, negOccs, ngramsSize);

		// calculate total occurrences of terms in +ve and -ve documents
		List<Integer> occs = add(posOccs, negOccs, ngramsSize);
		List<Integer> noccs = add(posNoccs, negNoccs, ngramsSize);

		// prepare
		ChiSqProperty chiSqProperty = new ChiSqProperty();
		chiSqProperty.noOfPosDocs = noOfPosDocs;
		chiSqProperty.noOfNegDocs = noOfNegDocs;
		chiSqProperty.posOccs = posOccs;
		chiSqProperty.negOccs = negOccs;
		chiSqProperty.posNoccs = posNoccs;
		chiSqProperty.negNoccs = negNoccs;
		chiSqProperty.occs = occs;
		chiSqProperty.noccs = noccs;
		return chiSqProperty;
	}

	private List<Integer> add(List<Integer> list1, List<Integer> list2, int size) {
		List<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			values.add(list1.get(i) + list2.get(i));
		}
		return values;
	}

	private double computeChisq(int noOfDocs, int noPosDocs, int noNegDocs, int occ, int nocc, int posOcc, int posNocc,
			int negOcc, int negNocc) {
		double e1PosOcc = expectedValue(noOfDocs, noPosDocs, occ);
		double e2PosNocc = expectedValue(noOfDocs, noPosDocs, nocc);
		double e3NegOcc = expectedValue(noOfDocs, noNegDocs, occ);
		double e4NegNocc = expectedValue(noOfDocs, noNegDocs, nocc);

		double posVal1 = termChisq(posOcc, e1PosOcc);
		double posVal2 = termChisq(posNocc, e2PosNocc);
		double posChisq = posVal1 + posVal2;

		double negVal1 = termChisq(negOcc, e3NegOcc);
		double negVal2 = termChisq(negNocc, e4NegNocc);
		double negChisq = negVal1 + negVal2;

		double posProb = noPosDocs / (double) noOfDocs;
		double negProb = noNegDocs / (double) noOfDocs;

		// weighted average score for all classes
		return (posProb * posChisq + negProb * negChisq);
	}
	
	public static void main(String[] args) {
		new ChiSqCalculator().computeChisq(3, 2, 1, 2, 1, 1, 1, 1, 0);
	}

	private double expectedValue(int total, int val1, int val2) {
		return (val1 / (double) total) * (val2 / (double) total);
	}

	private List<Integer> subFromTotal(int total, List<Integer> items, int size) {
		List<Integer> remainings = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			remainings.add(i, total - items.get(i));
		}
		return remainings;
	}

	private double termChisq(double obs, double exp) {
		return exp == 0 ? 0 : (Math.pow((obs - exp), 2) / exp);
	}

	static class ChiSqProperty {
		int noOfPosDocs;
		int noOfNegDocs;
		List<Integer> posOccs;
		List<Integer> negOccs;
		List<Integer> posNoccs;
		List<Integer> negNoccs;
		List<Integer> occs;
		List<Integer> noccs;
	}
}