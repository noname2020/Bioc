package utility;

import java.util.ArrayList;
import java.util.HashMap;

import type.Triple;

public class Mapping {
	public static HashMap<String, ArrayList<Triple>> makePmidToTriples (ArrayList<Triple> triples) {
		HashMap<String, ArrayList<Triple>> pmidToTriples = new HashMap<String, ArrayList<Triple>>();
		String pmid;
		ArrayList<Triple> tripleList;
		
		for (Triple triple : triples) {
			pmid = triple.getPmid();
			if (pmidToTriples.containsKey(pmid)) {
				pmidToTriples.get(pmid).add(triple);
			} else {
				tripleList = new ArrayList<Triple>();
				tripleList.add(triple);
				pmidToTriples.put(pmid, tripleList);
			}
		}
		return pmidToTriples;
	}
}
