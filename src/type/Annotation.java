package type;

public class Annotation extends Sentence {
	private String type;
	
			
	public Annotation() {
		setType("GOA");
	}
	
	public Annotation(int psgOffset, int offset, String text) {
		super(psgOffset, offset, text);
		setType("GOA");
	}
	
	public String toXmlString() {
		return "";
	}
	
}
