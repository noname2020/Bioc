package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

public class Stopper {
	private HashSet<String> stopwords = new HashSet<String>();
	
	public Stopper() throws IOException {
		URL url = getClass().getResource("stopwords.txt");
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(url.getPath()));
		String line;
		while ( (line = reader.readLine()) != null) {
			stopwords.add(line.trim());
		}
	}
	
	public HashSet<String> getStopwords() {
		return this.stopwords;
	}
	
	public boolean stop(String word) {
		word = word.toLowerCase();
		if (stopwords.contains(word)) {
			return true;
		}
		return false;
	}
}
