package edu.lab.sentimentanalysis.svmlib;

import java.io.File;
import java.io.IOException;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import de.vandermeer.asciitable.AsciiTable;

public class SVMClassifier {

	public void train(Problem problem, String fileName) {
		SolverType solver = SolverType.L2R_L2LOSS_SVC;
		double C = 1.0;
		double eps = 0.01;
		Parameter parameter = new Parameter(solver, C, eps);
		Model model = Linear.train(problem, parameter);
		File modelFile = new File(fileName);
		try {
			model.save(modelFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test(Problem problem, String fileName) {
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		Feature[][] featureVec = problem.x;
		double[] labels = problem.y;
		int num = featureVec.length;
		double[][] results = new double[num][2];
		try {
			Model model = Model.load(new File(fileName));
			for (int i = 0; i < num; i++) {
				double observed = labels[i];
				double prediction = Linear.predict(model, featureVec[i]);
				results[i] = new double[] { observed, prediction };
				if (observed == 1 && prediction == 1)
					++tp;
				if (observed == -1 && prediction == -1)
					++tn;
				if (observed == -1 && prediction == 1)
					++fp;
				if (observed == 1 && prediction == -1)
					++fn;
			}
			printResult(results);
			printResult(tp, tn, fp, fn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test(Feature[][] featureVec, String fileName) {
		int num = featureVec.length;
		try {
			Model model = Model.load(new File(fileName));
			for (int i = 0; i < num; i++) {
				double prediction = Linear.predict(model, featureVec[i]);
				printResult(prediction);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printResult(double result) {
		System.out.format("THE CONTENT IS %s.", result == -1 ? "NEGATIVE" : "POSITIVE");
	}

	private void printResult(double[][] results) {
		int size = results.length;
		AsciiTable table = new AsciiTable();
		table.addRule();
		table.addRow("S.N.", "Observed Value", "Predicted Value");
		table.addRule();
		for (int i = 0; i < size; i++) {
			table.addRow(i + 1, results[i][0], results[i][1]);
			table.addRule();
		}
		System.out.println(table.render());
	}

	private void printResult(int tp, int tn, int fp, int fn) {
		double accVal = accuracy(tp, tn, fp, fn);
		double recallVal = recall(tp, tn, fp, fn);
		double precisionVal = precision(tp, tn, fp, fn);
		double fMeasure = fScore(recallVal, precisionVal);
		AsciiTable table = new AsciiTable();
		table.addRule();
		table.addRow("Accuracy", "Recall", "Precision", "F-Measure");
		table.addRule();
		table.addRow(String.format("%.2f", accVal * 100), String.format("%.2f", recallVal * 100),
				String.format("%.2f", precisionVal * 100), String.format("%.2f", fMeasure * 100));
		table.addRule();
		System.out.println(table.render());
	}

	private double accuracy(int tp, int tn, int fp, int fn) {
		return (tp + tn) / (double) (tp + tn + fp + fn);
	}

	private double recall(int tp, int tn, int fp, int fn) {
		return tp / (double) (tp + fn);
	}

	private double precision(int tp, int tn, int fp, int fn) {
		return (tp + fp) == 0 ? 0 : tp / (double) (tp + fp);
	}

	private double fScore(double recall, double precision) {
		return (recall + precision) == 0 ? 0 : (2 * recall * precision) / (recall + precision);
	}
}
