package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	
	public static HashMap<String, HashSet<String>> getSlimToGopmids(String slimpmidPath) throws IOException {
		HashMap<String, HashSet<String>> slimToGopmids = new HashMap<String, HashSet<String>>();
		System.out.println("Reading " + slimpmidPath);
		BufferedReader reader = new BufferedReader(new FileReader(slimpmidPath));
		String[] items;
		String slim, go, pmid, gopmid, line;
		HashSet<String> gopmids;
		while ((line = reader.readLine()) != null) {
			items = line.split("\t");
			slim = items[1];
			go = items[4];
			pmid = items[5];
			gopmid = go + " " + pmid;
			if (slimToGopmids.containsKey(slim)) {
				slimToGopmids.get(slim).add(gopmid);
			} else {
				gopmids = new HashSet<String>();
				gopmids.add(gopmid);
				slimToGopmids.put(slim, gopmids);
			}
		}
		reader.close();
		System.out.println("Done\n");
		return slimToGopmids;
	}
	
	public static HashMap<String, HashSet<String>> makeGeneToPmids (HashSet<String> identifiedGeneSet, HashMap<String, HashSet<String>> slimToGopmids, String pantherFile) throws IOException {
		HashMap<String, HashSet<String>> geneToGopmids = new HashMap<String, HashSet<String>>();
		BufferedReader reader;
		String line, gene, slim, pmid, go, gopmid;
		String[] items;
		HashSet<String> gopmids, slims;
		


		
		System.out.println("Reading " + pantherFile);	
		reader = new BufferedReader(new FileReader(pantherFile));
		while ((line = reader.readLine()) != null) {
			items = line.split("\t");
			gene = items[0];
			if (!identifiedGeneSet.contains(gene)) continue;
			slim = items[1];
			if (!slimToGopmids.containsKey(slim)) continue;
			if (geneToGopmids.containsKey(gene)) {
				geneToGopmids.get(gene).addAll(slimToGopmids.get(slim));
			} else {
				gopmids = new HashSet<String>();
				gopmids.addAll(slimToGopmids.get(slim));
				geneToGopmids.put(gene, gopmids);
			}
		}
		reader.close();
		System.out.println("Done\n");
		return geneToGopmids;
	}
}
