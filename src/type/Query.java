package type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Query extends Sentence implements Comparable {
	private String query, tokenSentence;
	private HashSet<String> workingset = new HashSet<String>();	
	private ArrayList<GeneOntology> topGoes;
	private double mmrScore = 0.0;
	private String goId;
	
	public Query(int psgOffset, int offset, String text, String id, HashSet<String> workingset) {
		super(psgOffset, offset, text);
		setWorkingset(workingset);
	}

	public Query(int psgOffset, int offset, String text) {
		super(psgOffset, offset, text);
	}
	
	public Query() {}

	public HashSet<String> getWorkingset() {
		return workingset;
	}

	public void setWorkingset(HashSet<String> workingset) {
		this.workingset = workingset;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getTokenSentence() {
		return tokenSentence;
	}

	public void setTokenSentence(String tokenSentence) {
		this.tokenSentence = tokenSentence;
	}
	
	public ArrayList<GeneOntology> getTopGoes() {
		return topGoes;
	}

	public void setTopGoes(ArrayList<GeneOntology> topGoes) {
		this.topGoes = topGoes;
	}
	
	public void printGoInfo() {
		if (topGoes == null) return;
		for (GeneOntology go : topGoes) {
			System.out.println(go.getGoId() + " " + go.getEvidence());
		}
	}

	public double getMmrScore() {
		return mmrScore;
	}

	public void setMmrScore(double mmrScore) {
		this.mmrScore = mmrScore;
	}

	public void printInfo() {
		printing(id, "id");
		printing(psgOffset, "psgOffset");
		printing(offset, "offset");
		printing(psgType, "psgType");
		printing(type, "type");
;
		printing(text, "text");
		printing(tokenSentence, "tokenSentence");
	}
	
	public void printing(Object content, String description) {
		if (content != null ) {
			System.out.println(description + ": " + content);
		} else {
			System.out.println(description + ": ");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || !(o instanceof Query)) return false;
		Query copy = Query.class.cast(o);
		return offset == copy.getOffset() && length == copy.getLength();
		//return goId.equals(copy.goId);
		//return evidence.equals(copy.evidence);
	}	
	
	@Override
	public int hashCode() {
		return offset + 31 * length;
	}
	
	@Override
	public int compareTo(Object o) {
		if(o == this) return 0;
		if(o == null || !(o instanceof Query)) return -1;
		Query copy = Query.class.cast(o);
		return Integer.valueOf(offset).compareTo(copy.getOffset());
	}
	
	public static void main(String[] args) {
		Query a = new Query();
		Query b = new Query();
		Query c = new Query();
		HashMap<Query, Integer> map = new HashMap<Query, Integer>();

		a.setOffset(1);a.setLength(11);map.put(a, 1);
		b.setOffset(1);b.setLength(11);map.put(b, 2);
		c.setOffset(1);c.setLength(12);map.put(c, 3);		
		
		
		
		
		for (Map.Entry<Query, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey().getOffset()+ " " + entry.getKey().getLength() + " " + entry.getValue());
		}
	}

	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}
}
