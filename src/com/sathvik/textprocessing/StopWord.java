package com.sathvik.textprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sathvik.utils.Utils;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         This is a prototype implementation of ERS module for thesis.
 *
 */

public class StopWord {
	private String filepath;
	private Pattern stopWordsPattern;

	StopWord(String path) {
		filepath = path;
	}

	public String removeFrom(String query) {
		createRegEx();
		// Remove punctuations.
		String stripped_query = query.replaceAll("\\W", " ");
		Matcher matcher = stopWordsPattern.matcher(stripped_query);
		String stripped_sentence = matcher.replaceAll("");

		// Stem the word.
		String stemmed_sentence = Utils.stem(stripped_sentence);

		return stemmed_sentence;

	}

	public void createRegEx() {

		String pattern = "\\b(?:%s)\\b";

		BufferedReader br;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			br = new BufferedReader(new InputStreamReader(
					(classLoader.getResourceAsStream(filepath))));
			// File f = new File(filepath);
			// Utils.println("Abs path is:: "+f.getAbsolutePath());
			// br = new BufferedReader(new FileReader(f.getAbsolutePath()));
			String line;

			String words = "";
			while ((line = br.readLine()) != null) {
				words = words + line + "|";
			}

			pattern = String.format(pattern, words);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		stopWordsPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

	}

}
