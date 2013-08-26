package classification;

import java.util.HashSet;

import type.Query;

public class SimpleClassifier extends Classifier {
	private HashSet<String> keptPsgTypes;
	
	public SimpleClassifier() {
		keptPsgTypes = new HashSet<String>();
		keptPsgTypes.add("abstract");
		keptPsgTypes.add("paragraph");
		keptPsgTypes.add("fig_caption");
		keptPsgTypes.add("title_2");
		keptPsgTypes.add("front");
		/*
		
		keptPsgTypes.add("abstract_title_1");
		keptPsgTypes.add("title");
		keptPsgTypes.add("title_1");
		
		keptPsgTypes.add("fig_title_caption");
		keptPsgTypes.add("footnote_title");
		keptPsgTypes.add("table");
		keptPsgTypes.add("table_title_caption");
		*/
		
	}	
	
	
	@Override
	public boolean classifyQuery(Query query) {
		if ((query.getText().split(" ")).length < 8) {
			return false;
		} 

		if (!keptPsgTypes.contains(query.getPsgType())) {
			return false;
		}
			
		return true;
	}

}
