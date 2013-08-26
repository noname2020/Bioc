package evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import type.Annotation;
import type.GeneOntology;
import type.Query;
import type.ScorePR;
import utility.Parser;


public class Validation {
	private static boolean print = false;
	
	public static void printInfo(boolean print) {
		Validation.print = print;
	}
	
	public static boolean checkInitialSplit() {
		boolean valid = false;
		
		String dataPath = "/home/zhu/workspace/Bioc/data/";
		String artsentPath = dataPath + "articles_sent/";
		String goldPath = dataPath + "goldstandard/";//annotation_22792398.xml
		String artSentFilePath;
		
		File goldDir = new File(goldPath);
		ArrayList<Annotation> annotations;
		ArrayList<Query> queries;
		SortedMap<Integer, Query> offsetToQuery = new TreeMap<Integer, Query>();
		Query query;
		
		int annotOffset, annotPsgOffset, annotLength, sentOffset, sentPsgOffset;
		
		for (File file: goldDir.listFiles()) {
			if (file.getName().endsWith(".xml")) {
				System.out.println(file.getPath());
				//System.out.println(artSentFilePath);				
				
				
				if (file.getPath().equals("/home/zhu/workspace/Bioc/data/goldstandard/annotation_12707312.xml")) {
					System.out.println(file.getPath());
				} else {
					continue;
				}
				
				
				// Get query info
				artSentFilePath = artsentPath + file.getPath().split("_")[1];
				queries = new ArrayList<Query>();
				Parser.getSentences(artSentFilePath, queries);
				for (int i = 0; i < queries.size(); i++) {
					query = queries.get(i);
					sentPsgOffset = query.getPsgOffset();
					sentOffset = query.getOffset();
					/*
					System.out.println("  -----------  ");
					System.out.println(file.getPath());
					System.out.println("sentPsgOffset: " + sentPsgOffset);
					System.out.println("sentOffset: " + sentOffset);
					*/
					offsetToQuery.put(sentOffset, query);
				}
				
				// Get annotation (gold standard) info
				annotations = Parser.getAnnotations(file.getPath());
				for (Annotation annotation : annotations) {
					annotPsgOffset = annotation.getPsgOffset();
					annotOffset = annotation.getOffset();
					annotLength = annotation.getLength();
					/*
					System.out.println("  -----------  ");
					System.out.println(file.getPath());
					System.out.println("annotPsgOffset: " + annotPsgOffset);
					System.out.println("annotOffset: " + annotOffset);
					*/
					// Check if the offset + length (unique identifiers) matches.
					
					query = offsetToQuery.get(annotOffset);
					if (query == null) {
						System.out.println("Query key not found:" + annotOffset);
						continue;
					}
					if (query.getOffset() == annotOffset && query.getLength() == annotLength) {
						System.out.println("Matched at " + annotOffset + " + " + annotLength);
					} else {
						System.out.println("Not matched: annot:" + annotOffset + "+" + annotLength + " query:" + query.getOffset() + "+" + query.getLength());
					}
				}
				
				
				//System.exit(0);
			}	
		}
		return valid;
	}

	public static void getMergedGoes(ArrayList<Annotation> annotations, HashMap<Integer, ArrayList<GeneOntology>> offsetToGoldGoes) {
		int annotOffset, annotPsgOffset, annotLength;
		
		ArrayList<GeneOntology> goldGoes;
		
		// Get annotation (gold standard) info and merge them for each sentence
		
		for (Annotation annotation : annotations) {
			//annotPsgOffset = annotation.getPsgOffset();
			annotOffset = annotation.getOffset();
			//annotLength = annotation.getLength();
			
			if (offsetToGoldGoes.containsKey(annotOffset)) {
				offsetToGoldGoes.get(annotOffset).add(annotation.getGo());
			} else {
				goldGoes = new ArrayList<GeneOntology>();
				goldGoes.add(annotation.getGo());
				offsetToGoldGoes.put(annotOffset, goldGoes);
			}
		}		
	}

	public static void getMergedGoset(ArrayList<Annotation> annotations, HashMap<Integer, HashSet<GeneOntology>> offsetToGoldset) {
		int annotOffset;
		HashSet<GeneOntology> goldGoset;
		
		// Get annotations from gold standard and merge them for each sentence (i.e., offset)
		for (Annotation annotation : annotations) {
			annotOffset = annotation.getOffset();
			
			if (offsetToGoldset.containsKey(annotOffset)) {
				offsetToGoldset.get(annotOffset).add(annotation.getGo());
			} else {
				goldGoset = new HashSet<GeneOntology>();
				goldGoset.add(annotation.getGo());
				offsetToGoldset.put(annotOffset, goldGoset);
			}
		}		
	}
	
	/**
	 * Get the number of hits in the top ranked GOes.
	 * @param rankedList
	 * @param goldGoset
	 * @return
	 */
	public static int getCounts(ArrayList<GeneOntology> rankedGoes, HashSet<GeneOntology> goldGoset) {
		int count = 0;
		for (int i = 0; i < rankedGoes.size(); i++) {
			if (goldGoset.contains(rankedGoes.get(i))) {
				count++;
			}		
		}
	  
		return count;
	}
	
	public static ArrayList<ScorePR> getEvals(ArrayList<Query> results, String goldPath) {		
		int correct = 0, offset;
		ArrayList<ScorePR> scores = new ArrayList<ScorePR>();
		ScorePR score;
		HashMap<Integer, HashSet<GeneOntology>> offsetToGoldGoset = new HashMap<Integer, HashSet<GeneOntology>>(); 
		ArrayList<GeneOntology> rankedGoes;
		HashSet<GeneOntology> goldGoset;
		
		ArrayList<Annotation> annotations = Parser.getAnnotations(goldPath);
		getMergedGoset(annotations, offsetToGoldGoset);
		
		for (Query result : results) {
			offset = result.getOffset();
			if (!offsetToGoldGoset.containsKey(offset)) {
				continue;
			}
			
			rankedGoes = result.getTopGoes();
			goldGoset = offsetToGoldGoset.get(offset);			
			correct = getCounts(rankedGoes, goldGoset);
			
			score = new ScorePR();
			score.setCorrect(correct);
			score.setGoldTotal(goldGoset.size());
			score.setTotal(rankedGoes.size());
			score.setOffset(offset);
			score.compute();
			scores.add(score);

			if (print) {
				//System.out.println("-------------------------------------");
				//System.out.println("Offt=" + result.getOffset());
				System.out.println(offset + "\tSent=" + result.getText());
				System.out.println(offset + "\tGene=" + result.getGene());
				System.out.print(offset + "\tGold=");
				for (GeneOntology goldGo : goldGoset) {
					System.out.print(goldGo.getGoId() + " " + goldGo.getEvidence());
					System.out.print("||");
				}
				System.out.println();
				
				System.out.print(offset + "\tTest=");
				for (int i = 0; i < rankedGoes.size(); i++) {
					System.out.print(rankedGoes.get(i).getGoId() + " " + rankedGoes.get(i).getEvidence());
					System.out.print("||");
				}
				
				System.out.println();
				System.out.println(offset + "\tResult=" +" P:" + correct + "/" + rankedGoes.size() + " R: " + correct + "/" + goldGoset.size());
				//System.out.println("-------------------------------------");
			}
		}
		
		
//		for (ScorePR s : scores) {
//			System.out.println("Precision: " + s.getPrecision() + "\tRecall: " + s.getRecall());
//		}
		
		
		
		return scores;
	}
	
	
	
	
	public static void main(String[] args) {
		System.out.println(checkInitialSplit());
	}
}
