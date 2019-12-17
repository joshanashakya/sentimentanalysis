package edu.lab.sentimentanalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

public class FileReaderWriter {

	public static List<List<String>> readFiles(File[] files, String label) {
		List<List<String>> data = new ArrayList<>();
		for (File file : files) {
			try {
				String content = FileUtils.readFileToString(file, "UTF-8");
				data.add(Arrays.asList(content, label));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}

	public static void writeToCSV(String path, List<List<String>> data, String... header) {
		BufferedWriter writer;
		CSVPrinter csvPrinter;
		try {
			writer = Files.newBufferedWriter(Paths.get(path));
			csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header));
			csvPrinter.printRecords(data);
			csvPrinter.flush();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<List<String>> readCSV(String path, String... header) {
		List<List<String>> data = new ArrayList<>();
		try {
			FileReader reader = new FileReader(path);
			CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
			for (CSVRecord csvRecord : parser) {
				List<String> records = new ArrayList<>();
				for (String h : header) {
					records.add(csvRecord.get(h));
				}
				data.add(records);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public static List<String> readFile(String path) {
		List<String> data = new ArrayList<>();
		try {
			data = FileUtils.readLines(new File(path), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public static <T> void writeToFile(List<T> list, String path) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			for (T l : list) {
				writer.write(String.valueOf(l));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Double> readDataFile(String path) {
		List<Double> list = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String str;
			while ((str = reader.readLine()) != null) {
				str = str.trim();
				list.add(Double.valueOf(str));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String read(String path) {
		String data = null;
		try {
			data = FileUtils.readFileToString(new File(getPath(path)), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public static void print(String header, String content, boolean print) {
		Data.OUTPUT_DATA.append(header);
		Data.OUTPUT_DATA.append("\n");
		Data.OUTPUT_DATA.append(content);
		Data.OUTPUT_DATA.append("\n\n");
		if (!print) {
			return;
		}
		System.out.println(header);
		System.out.println(content);
	}

	public static void write(String content, String path) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getPath(String path) {
		ClassLoader loader = FileReaderWriter.class.getClassLoader();
		return loader.getResource(path).getPath().replaceAll("%20", " ");
	}
}
