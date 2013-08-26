package annotation;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import type.Annotation;
import type.GeneOntology;
import type.IndriResult;
import type.Query;
import type.Triple;
import utility.Mapping;
import utility.Parser;
import utility.Sorting;

public class SimpleAnnotator extends Annotator {
	@Override
	public void annotate(ArrayList<Query> queries, String resultPath, Annotator annotator, String articlePmid, int numTopGo, int numTopPmid) throws IOException {
		ArrayList<IndriResult> results;
		String id, pmid, goId, goEvidence, gene;
		GeneOntology go;
		ArrayList<Triple> triples;
		double score;
		int t;
		String temp;
		
		int rank;
		System.out.println(resultPath);
		
		HashMap<String, ArrayList<IndriResult>> idToResults = Parser.getIdToResults(resultPath, numTopPmid);
		
//		System.out.println("queryid");
//		for (Query query : queries) {
//			System.out.println(query.getId());
//		}
//		System.out.println("resultid");
//		for (String iid : idToResults.keySet()) {
//			System.out.println(iid);
//		}
		
		//String outPath = "/home/zhu/workspace/Bioc/data/forpanther/" + articlePmid + ".pmid100";
		//PrintStream outStream = new PrintStream(outPath);
		
		ArrayList<GeneOntology> topGoes;
		HashMap<GeneOntology, Double> goToScore;
		Map<GeneOntology, Double> goToScoreSorted;
		
		for (Query query : queries) {
			id = query.getId();
			results = idToResults.get(id);
			goToScore = new HashMap<GeneOntology, Double>();
			
			// Aggregation scores
			if (results == null) {
				System.out.println("null");
				System.out.println(articlePmid);
				System.out.println(id);
			}
			for (IndriResult result : results) {
				pmid = result.getDocument();
				score = Math.pow(Math.E, result.getScore());
				triples = getPmidToTriples().get(pmid);
				rank = result.getRank();
						
				for (Triple triple : triples) {	
					goId = triple.getGoId();
					goEvidence = triple.getEvidence();
					go = new GeneOntology();
					go.setGoId(goId);
					go.setEvidence(goEvidence);
					
					if (goToScore.containsKey(go)) {
						goToScore.put(go, goToScore.get(go) + score);
					} else {
						goToScore.put(go, score);
					}
					
					//System.out.println(goId + " " + goEvidence + " " + score);
					temp = query.getId() + " " + pmid + " " + rank + " " + goId + " " + goEvidence;
					//System.out.println(temp);				
					//outStream.println(temp);
				}
				
				if (print) {
					System.out.println("offset: " + query.getId());
					System.out.println("\tpmid:" + pmid);
					for (Triple triple : triples) {
						System.out.println("\t\t" + triple.getGoId() + " " + triple.getEvidence());
					}
					System.out.println();
					System.out.println();
				}
			}
			
			
			//System.exit(0);
			
			
			goToScoreSorted = Sorting.sortByComparator(goToScore);
			
			topGoes = new ArrayList<GeneOntology>(); 
			t = 0;
			for (Entry<GeneOntology, Double> entry : goToScoreSorted.entrySet()) {
				//System.out.println(entry.getKey().getGoId() + " " + entry.getKey().getEvidence() + "\t" + Math.log(entry.getValue()));
				t++;
				topGoes.add(entry.getKey());
				if (t == numTopGo) {
					break;
				}
			}
			
			//System.out.println(topGoes.size() + " Gosize");
			query.setTopGoes(topGoes);
			//query.printGoInfo();
			//System.out.println();
			//System.out.println();
			//System.exit(0);
		}	
		
		
		//outStream.close();
	}

	
	public static void main(String[] args) throws IOException {
		String dataPath = "/home/zhu/workspace/Bioc/data/";
		String resultPath = dataPath + "sampletestquery.result";
		int top = 50;
		
		String triplePath = dataPath + "triples.unique";
		ArrayList<Triple> triples = Parser.getTriples(triplePath);
		HashMap<String, ArrayList<Triple>> pmidToTriples = Mapping.makePmidToTriples(triples);
		
		
		HashMap<String, ArrayList<IndriResult>> idToResults = Parser.getIdToResults(resultPath, top);
		ArrayList<IndriResult> results;
		IndriResult result;
		SimpleAnnotator annotator = new SimpleAnnotator();
//		for (String id: idToResults.keySet()) {
//			annotator.annotate(idToResults.get(id));
//		}
	}
}
