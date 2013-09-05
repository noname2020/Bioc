package annotation;

import java.io.File;
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
	public void annotate(String resultPath, String submissionPath, Annotator annotator, String articlePmid, int numTopGo, int numTopPmid) throws IOException {
		ArrayList<IndriResult> results;
		String pmid, goId, goEvidence, gene;
		GeneOntology go;
		ArrayList<Triple> triples;
		double score;
		int t, rank;
		String temp;
		String[] items;
		
		System.out.println(resultPath);
		
		File file = new File(resultPath);
		if (!file.exists()) {
			System.out.println("file does not exit");
			return;
		}
		
		
		HashMap<String, ArrayList<IndriResult>> idToResults = Parser.getIdToResults(resultPath, numTopPmid);
		
//		System.out.println("queryid");
//		for (Query query : queries) {
//			System.out.println(query.getId());
//		}
//		System.out.println("resultid");
//		for (String iid : idToResults.keySet()) {
//			System.out.println(iid);
//		}
		
		PrintStream outStream = new PrintStream(submissionPath);
		
		String output;
		
		
		ArrayList<GeneOntology> topGoes;
		HashMap<GeneOntology, Double> goToScore;
		Map<GeneOntology, Double> goToScoreSorted;
		
		for (String id : idToResults.keySet()) {
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
				triples = pmidToTriples.get(pmid); //getPmidToTriples()
				rank = result.getRank();
				
				if (triples == null) continue;		
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
					//temp = id + " " + pmid + " " + rank + " " + goId + " " + goEvidence;
					//System.out.println(temp);				
					//outStream.println(temp);
				}
				
//				if (print) {
//					System.out.println("\tpmid:" + pmid);
//					for (Triple triple : triples) {
//						System.out.println("\t\t" + triple.getGoId() + " " + triple.getEvidence());
//					}
//					System.out.println();
//					System.out.println();
//				}
			}
			
			
			//System.exit(0);
			
			
			goToScoreSorted = Sorting.sortByComparator(goToScore);
			
			topGoes = new ArrayList<GeneOntology>(); 
			t = 0;
			for (Entry<GeneOntology, Double> entry : goToScoreSorted.entrySet()) {
				//System.out.println(id + "\t" + entry.getKey().getGoId() + " " + entry.getKey().getEvidence() + "\t" + Math.log(entry.getValue()));
				items = id.split("-");
				System.out.println(articlePmid + "\t" + items[1] + "\t" + entry.getKey().getGoId() + " " + entry.getKey().getEvidence() + "\t" + Math.log(entry.getValue()));
				output = articlePmid + " " + items[1] + " " + entry.getKey().getGoId();
				outStream.println(output);
				//System.out.println(output);				
				t++;
				topGoes.add(entry.getKey());
				if (t == numTopGo) {
					break;
				}
				
			}
			
			System.out.println("===============================");
			
			//System.out.println(topGoes.size() + " Gosize");
			//System.out.println();
			//System.out.println();
			//System.exit(0);
		}	
		
		
		outStream.close();
	}

	
	public static void main(String[] args) throws IOException {
		String dataPath = "";
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
