package edu.lab.sentimentanalysis.document;

import java.util.ArrayList;
import java.util.List;

public class Document {

	private int id;
	private String content;
	private String label;
	private List<Double> weights = new ArrayList<>();
	private List<Integer> termCounts = new ArrayList<>();
	
	public Document(int id, String content) {
		this.id = id;
		this.content = content;
	}

	public Document(int id, String content, String label) {
		this.id = id;
		this.content = content;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Double> getWeights() {
		return weights;
	}

	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}

	public List<Integer> getTermCounts() {
		return termCounts;
	}

	public void setTermCounts(List<Integer> termCounts) {
		this.termCounts = termCounts;
	}
}
