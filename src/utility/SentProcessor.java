package utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class SentProcessor {
	/*
	 * Remove non-alphanumeric chars within a sentence, but keep the valid 
	 * punctuation.
	 */
	public static String cleanSent(String text) {
		//System.out.println(text);
		String[] terms = text.split(" ");
		StringBuffer cleanText = new StringBuffer(200);
		for (String term : terms) {
			term = term.trim();
			if (term.length() == 1) {
				term = TokenProcessor.removeNonWordChar(term);
			} else {
				term = TokenProcessor.removeNonPunctuation(term);
			}
			
			if ("".equals(term)) {
				//System.out.println("Empty term");
				continue;
			}
			cleanText.append(term);
			cleanText.append(" ");
		}
		return cleanText.toString().trim();
	}

	/*
	 * Remove non-alphanumeric chars within a sentence. Lower case and tokenize.
	 */
	public static ArrayList<String> tokenizeSent(String text) {
		String[] terms = text.split(" ");
		ArrayList<String> tokens = new ArrayList<String>();
		for (String term : terms) {
			term = term.trim();
			term = TokenProcessor.removeNonWordChar(term);
			if ("".equals(term)) {
				//System.out.println("Empty term");
				continue;
			}
			term = term.toLowerCase();
			tokens.add(term);
			//System.out.println(term);
		}
		return tokens;
	}

	/*
	 * Remove non-alphanumeric chars within a sentence. Lower case and tokenize.
	 */
	public static ArrayList<String> tokenizeSent(String text, HashSet<String> stopwords) {
		ArrayList<String> tokens = tokenizeSent(text);
		/*
		for (String token : tokens) {
			System.out.println(token);
		}
		*/
		ArrayList<String> keptTokens = new ArrayList<String>();
		for (int i = 0; i < tokens.size(); i++) {
			if (stopwords != null && stopwords.contains(tokens.get(i))) {
				//System.out.println(tokens.get(i) + " removed");
				continue;
			}
			keptTokens.add(tokens.get(i));
		}
		return keptTokens;
	}
	
	public static void main(String [] args) throws IOException {
		
		String inputStr = "Th[is is at @#$^ca?t&??!"; //"This is ` ~ ! @ # $ % ^ & * ( ) - = _ + { } | [ ] \\ cat. : ; \" ' < > , . ? /";
		//System.out.println(cleanSent(inputStr));
		//tokenizeSent(inputStr);
		Stopper stop = new Stopper();
		tokenizeSent(inputStr, stop.getStopwords());
	}
}
