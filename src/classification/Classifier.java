package classification;

import java.util.ArrayList;
import java.util.HashSet;

import type.Query;
import type.Sentence;

public abstract class Classifier {	
	abstract public boolean classifyQuery(Query query);
	
	public ArrayList<Query> filterQueries(ArrayList<Query> queries) {
		ArrayList<Query> validQueries = new ArrayList<Query>();
		for (Query query : queries) {
			if (classifyQuery(query)) {
				validQueries.add(query);
			} else {
				//System.out.println("Filtered: " + query.getSentence() + "    PsgType: " + query.getPsgType());
			}
		}
		return validQueries;		
	}
}
