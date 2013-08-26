package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class GeneSynsReader {

	/**
	 * Construct the mapping of GeneID to Gene synonym list for a specified set 
	 * of genes.
	 * 
	 * @param filePath
	 * @param reducedGeneIdSet
	 * @throws IOException
	 */
	
	public static void read(String filePath, HashSet<String> reducedGeneIdSet) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		HashMap<String, ArrayList<String>> geneIdToSyns = new HashMap<String, ArrayList<String>>();
		String line, geneId;
		String[] parts;
		ArrayList<String> syns;
		
		while ((line = reader.readLine()) != null) {
			parts = line.trim().split("\\|\\|");
			geneId = parts[0].split(":")[1];
			if (reducedGeneIdSet.contains(geneId)) {
				syns = new ArrayList<String>();
				for (int i = 1; i < parts.length; i++) {
					syns.add(parts[i]);
				}
				//System.out.println(parts[0].split(":")[1]);
				geneIdToSyns.put(geneId, syns);
			}
		}
		
		syns = geneIdToSyns.get("173945");
		for (int i = 0; i < syns.size(); i++) {
			System.out.println(syns.get(i));
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		String filePath = "/home/zhu/workspace/Bioc/data/genesynlist.txt";
		HashSet<String> reducedGeneIdSet = new HashSet<String>();
		reducedGeneIdSet.add("173945");
		read(filePath,reducedGeneIdSet);
	}
}
