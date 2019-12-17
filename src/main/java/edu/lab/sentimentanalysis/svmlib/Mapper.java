package edu.lab.sentimentanalysis.svmlib;

import java.util.List;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Problem;
import edu.lab.sentimentanalysis.document.Document;

public class Mapper {

	public Problem mapData(List<Document> docs, int dimension) {
		int dataNum = docs.size();
		Problem problem = new Problem();
		problem.l = dataNum;
		problem.n = dimension;
		Feature[][] featureVec = new Feature[dataNum][dimension];
		double label[] = new double[dataNum];

		int idx = 0;
		for (Document doc : docs) {
			List<Double> weights = doc.getWeights();
			for (int j = 0; j < dimension; j++) {
				featureVec[idx][j] = new FeatureNode(j + 1, weights.get(j));
			}
			label[idx] = getLabel(doc.getLabel());
			++idx;
		}
		problem.x = featureVec;
		problem.y = label;
		return problem;
	}

	public Feature[][] mapTestData(List<Document> docs, int dimension) {
		int dataNum = docs.size();
		Feature[][] featureVec = new Feature[dataNum][dimension];
		int idx = 0;
		for (Document doc : docs) {
			List<Double> weights = doc.getWeights();
			for (int j = 0; j < dimension; j++) {
				featureVec[idx][j] = new FeatureNode(j + 1, weights.get(j));
			}
			++idx;
		}
		return featureVec;
	}

	private int getLabel(String label) {
		return "pos".equalsIgnoreCase(label) ? 1 : -1;
	}
}
