package evaluation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import type.Annotation;
import type.Query;


public class EvalClassification {	
	public void evaluate(ArrayList<Annotation> goldAnnotations,  ArrayList<Query> queries) {
		HashMap<Integer, Annotation> offsetToGoldAnnotation = new HashMap<Integer, Annotation>();
		//SortedMap<Integer, Query> offsetToQuery = new TreeMap<Integer, Query>();
		Query query;
		int annotOffset, annotLength, minAnnotLength = 100;
		Annotation annotation;
		HashSet<String> psgTypes = new HashSet<String>();
		
		// Gold annotations
		for (int i = 0; i < goldAnnotations.size(); i++) {
			annotation = goldAnnotations.get(i);
			annotOffset = annotation.getOffset();
			annotLength = annotation.getLength();
			psgTypes.add(annotation.getPsgType());
			offsetToGoldAnnotation.put(annotOffset, annotation);
			if (minAnnotLength > annotLength) {
				minAnnotLength = annotLength;
			}
		}
		
		// Queries
		int numHitAnnots = 0;
		for (int i = 0; i < queries.size(); i++) {
			query = queries.get(i);
			if (offsetToGoldAnnotation.containsKey(query.getOffset())) {
				numHitAnnots++;
			}
		}
		
		/**
		 * Note that one sentence can have multiple annotation. But for 
		 * classification, we only evaluate based on the number of unique 
		 * sentences.
		 */
		System.out.println("Classification:\t P:" + numHitAnnots + "/"+ 
				queries.size() + " = " + 100.0 * numHitAnnots/queries.size() + 
				"%\tR:" + numHitAnnots + "/"+ offsetToGoldAnnotation.size() + " = " + 100.0 * numHitAnnots/offsetToGoldAnnotation.size() + "%");
		
		System.out.print("Observed passage types: ");
		for (String psgType : psgTypes) {
			System.out.print(psgType + " ");
		}
		System.out.println();
		System.out.println("minAnnotLength: " + minAnnotLength);
		System.out.println();
		
	}
}
