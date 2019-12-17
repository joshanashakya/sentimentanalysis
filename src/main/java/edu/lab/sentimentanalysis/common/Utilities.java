package edu.lab.sentimentanalysis.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Utilities {

	public static Map<Integer, Double> sortByValue(Map<Integer, Double> unsortedMap) {
		List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(unsortedMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		Map<Integer, Double> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<Integer, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
