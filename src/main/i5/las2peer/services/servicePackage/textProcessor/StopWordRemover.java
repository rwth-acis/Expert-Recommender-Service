package i5.las2peer.services.servicePackage.textProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sathvik, sathvikmail@gmail.com
 * 
 *         Removes Stop words from the text.
 *         By default, lucene stop words are removed.
 *         One can specify other included stopwords too.
 *
 */
public class StopWordRemover {
    private String mText;
    private String mFilePath;

    private Pattern stopWordsPattern;

    StringBuilder builder = new StringBuilder();
    String pattern = "\\b(?:%s)\\b";

    public StopWordRemover(String text) {
	mText = text;
    }

    public StopWordRemover(String text, String filepath) {
	mText = text;
	mFilePath = filepath;
    }

    public String getPlainText() {
	if (mFilePath != null) {
	    createPatternFromFile();
	} else {
	    createPattern();
	}

	// Remove punctuations.
	String stripped_text = mText.replaceAll("\\W", " ");
	Matcher matcher = stopWordsPattern.matcher(stripped_text);
	String cleantext = matcher.replaceAll("");

	return cleantext;
    }

    private void createPattern() {
	for (String word : StopWordList.lucene_stopwordlist) {
	    builder.append(word);
	    builder.append("|");
	}
	pattern = String.format(pattern, builder);

	stopWordsPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    // TODO: Test this pattern.
    public void createPatternFromFile() {

	if (mFilePath != null) {
	    BufferedReader br;
	    try {
		ClassLoader classLoader = getClass().getClassLoader();
		br = new BufferedReader(new InputStreamReader((classLoader.getResourceAsStream(mFilePath))));
		// File f = new File(filepath);
		// Utils.println("Abs path is:: "+f.getAbsolutePath());
		// br = new BufferedReader(new FileReader(f.getAbsolutePath()));
		String line;
		while ((line = br.readLine()) != null) {
		    builder.append(line);
		    builder.append("|");
		}

		pattern = String.format(pattern, builder);
		br.close();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	stopWordsPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

}
