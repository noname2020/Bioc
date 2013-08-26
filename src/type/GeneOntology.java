package type;

import java.util.HashMap;
import java.util.Map;

public class GeneOntology {
	private String goId, goTerm, evidence;

	public GeneOntology() {
		goId = "";
		goTerm = "";
		evidence = "";
	}
	
	public String getGoId() {
		return goId;
	}

	public void setGoId(String goId) {
		this.goId = goId;
	}

	public String getGoTerm() {
		return goTerm;
	}

	public void setGoTerm(String goTerm) {
		this.goTerm = goTerm;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(o == null || !(o instanceof GeneOntology)) return false;
		GeneOntology copy = GeneOntology.class.cast(o);
		return goId.equals(copy.goId) && evidence.equals(copy.evidence);
		//return goId.equals(copy.goId);
		//return evidence.equals(copy.evidence);
	}	
	
	@Override
	public int hashCode() {
		return goId.hashCode() + 31 * evidence.hashCode();
	}
	
	public static void main(String[] args) {
		GeneOntology a = new GeneOntology();
		GeneOntology b = new GeneOntology();
		GeneOntology c = new GeneOntology();
		HashMap<GeneOntology, Integer> map = new HashMap<GeneOntology, Integer>();

		a.setGoId("G1");a.setEvidence("E1");map.put(a, 1);
		b.setGoId("G2");b.setEvidence("E2");map.put(b, 2);
		c.setGoId("G2");c.setEvidence("E2");map.put(c, 3);		
		
		
		
		
		for (Map.Entry<GeneOntology, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey().getGoId() + " " + entry.getKey().getEvidence() + " " + entry.getValue());
		}
	}
}
