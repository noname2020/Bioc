package utility;

public class TokenProcessor {
	/*
	 * Remove non-alphanumeric chars, except hyphens that are not at the first 
	 * or last position
	 */
	public static String removeNonWordChar(String text) {
		//System.out.println(text);
		text = text.replaceAll("[`~!@#$%^&*()=_+{}|\\[\\]\\/:;,.<>?\"\']", "");
		if (0 == text.length()) return text;
		
		// Remove hyphens at the beginning
		while ("-".equals(text.substring(0, 1))) {
			if (2 <= text.length()) {
				text = text.substring(1);
			} else {
				return "";
			}
		}
		
		// Remove hyphens at the end
		while ("-".equals(text.substring(text.length() - 1))) {
			if (2 <= text.length()) {
				text = text.substring(0, text.length() - 1);
			} else {
				return "";
			}
		}
		text = text.replace("\\", "");
		return text; 
	}

	/*
	 * Remove non-alphanumeric chars, except punctuation such as ",.;:!"
	 */
	public static String removeNonPunctuation(String text) {
		if (0 == text.length()) return text;
		String pre = removeNonWordChar(text.substring(0, text.length()-1));
		String last = text.substring(text.length()-1).replaceAll("[-`~@#$%^&*()=_+{}|\\[\\]\\/<>\"\']", "");
		return pre + last;
	}
	
	
	public static void main(String[] args) {
		System.out.println(removeNonWordChar("-N-"));
	}
}
