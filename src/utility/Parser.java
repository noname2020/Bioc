package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import type.Annotation;
import type.GeneOntology;
import type.IndriResult;
import type.Query;
import type.Triple;

import java.io.File;


public class Parser {
	private static boolean print = false;
	
	public static void printInfo(boolean print) {
		Parser.print = print;
	}
	
	public static HashSet<String> getSameWorkingset(String filepath) throws IOException {
		HashSet<String> workingset = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line = null;
		while ((line = reader.readLine()) != null) {
			workingset.add(line.trim());
		}
		reader.close();
		return workingset;
	}	
	
	public static HashMap<String, HashSet<String>> getWorkingset(String filepath) throws IOException {
		
		HashMap<String, HashSet<String>> idToWorkingset = new HashMap<String, HashSet<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line = null, qid = null, workingDoc = null;
		String[] items = null;
		
		while ((line = reader.readLine()) != null) {
			items = line.trim().split("\t");
			qid = items[0];
			workingDoc = items[1];
			HashSet<String> workingSet = null;
			if (idToWorkingset.containsKey(qid)) {
				workingSet = idToWorkingset.get(qid);
				workingSet.add(workingDoc);
			} else {
				workingSet = new HashSet<String>();
				workingSet.add(workingDoc);
				idToWorkingset.put(qid, workingSet);
			}
		}
		reader.close();
		return idToWorkingset;
	}
	
	public static void makeTokenSentences(Collection<Query> queries) throws IOException {
		String cleanedSent = null;
		ArrayList<String> tokens = null;
		StringBuffer sentBuffer = null;
		
		for (Query query : queries) {
			cleanedSent = SentProcessor.cleanSent(query.getText());
			tokens = SentProcessor.tokenizeSent(cleanedSent, null);
			sentBuffer = new StringBuffer(100);
			for (String token : tokens) {
				sentBuffer.append(token);
				sentBuffer.append(" ");
			}
			query.setTokenSentence(sentBuffer.toString().trim());
		}		
	}
	
	public static void extractSentences(int psgOffset, String psgType, Element psgElement, ArrayList<Query> queries) {
    	NodeList sentNodes = psgElement.getElementsByTagName("sentence");
    	NodeList offsetNodes = null, textNodes = null;
    	Element eElement = null;
    	Node nNode = null;
    	Query query;
    	
    	for (int i = 0; i < sentNodes.getLength(); i++) {
    		nNode = sentNodes.item(i);
    		//System.out.println("\nCurrent Element :" + nNode.getNodeName());
     
    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
    			eElement = (Element) nNode;
    			//System.out.println("id: " + eElement.getAttribute("id"));
    			offsetNodes =  eElement.getElementsByTagName("offset");
    			textNodes = eElement.getElementsByTagName("text");
    			if (textNodes.getLength() > 0 && !"".equals(textNodes.item(0).getTextContent().trim())) {
    				StringBuffer textBuffer = new StringBuffer();
    				for (String part : textNodes.item(0).getTextContent().split("\n")) {
    					textBuffer.append(part.trim());
    					textBuffer.append(" ");
    				}
    				query = new Query();
    				query.setPsgOffset(psgOffset);
    				query.setOffset(Integer.parseInt(offsetNodes.item(0).getTextContent()));
    				query.setId();
    				query.setText(textBuffer.toString().trim());
    				query.setPsgType(psgType);
    				queries.add(query);
    			}
    		}
    	}
	}
	
	/**
	 * 
	 * 
	 * @param filepath
	 * @param queries
	 */
	public static void getSentences(String filepath, ArrayList<Query> queries) {
		try {
	    	File xmlFile = new File(filepath);
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(xmlFile);
	    	doc.getDocumentElement().normalize();
	    	//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	    	Node psgNode, infoNode, attrNode;
	    	Element psgElement;
	    	NodeList offsetNodes, infoNodes;
	    	int[] offsets;
	    	String psgType, attrName;
	    	NamedNodeMap nodeMap;
	    	
	    	NodeList psgNodes = doc.getElementsByTagName("passage"); // Get passages
	    	for (int i = 0; i < psgNodes.getLength(); i++) {
	    		psgNode = psgNodes.item(i);
	    		if (psgNode.getNodeType() == Node.ELEMENT_NODE) {
	    			psgElement = (Element) psgNode;
	    			offsetNodes = psgElement.getElementsByTagName("offset");
	    			infoNodes = psgElement.getElementsByTagName("infon"); 
	    			
	    			// Get passage type
	    			psgType = "";
	    			// Need to check all the infoNodes, though sometimes there is only one infoNode
	    			outerloop:
	    			for (int j = 0; j < infoNodes.getLength(); j++) {
	    				infoNode = infoNodes.item(j);
	    				if (infoNode.getNodeType() == Node.ELEMENT_NODE) {
	    					nodeMap = infoNode.getAttributes();
							for (int k = 0; k < nodeMap.getLength(); k++) {
	    						attrNode = nodeMap.item(k);
	    						attrName = attrNode.getNodeValue();
	    						if (attrName.equals("type")) {
	    							psgType = infoNode.getTextContent();
	    							break outerloop;
	    						}
							}
	    				}
	    			}
	    			
	    			// Get offset and sentences
	    			if (offsetNodes.getLength() > 0) {
	    				//System.out.println(offsetNodes.getLength());
	    				offsets = new int[offsetNodes.getLength()];
	    				for (int j = 0; j < offsetNodes.getLength(); j++) {
	    					offsets[j] = Integer.parseInt(offsetNodes.item(j).getTextContent());
	    				}
	    				Arrays.sort(offsets);
	    				//System.out.println(Arrays.toString(offsets));
	    				extractSentences(offsets[0], psgType, psgElement, queries); // Get sentences
	    			}	    			
	    		}
	    	}
	    	makeTokenSentences(queries);

	    } catch (Exception e) {
	    	e.printStackTrace();		
	    }
	}
	
	public static void extractAnnotations(int psgOffset, String psgType, Element psgElement, ArrayList<Annotation> annotations) {
		NodeList annotationNodes = psgElement.getElementsByTagName("annotation");
		Node annotationNode, infoNode, attrNode, locationNode;
		Element annotationElement, locationElement;
		Annotation annotation;
		NodeList textNodes, infoNodes, locationNodes;
		NamedNodeMap nodeMap;
		String attrName, id, text, gene = null;
		GeneOntology go;
		String[] partsOfGoterm;
		int offset, length, textPosition;
		
		// TODO: add infon key="type" in both passage and annotation
		// System.out.println(annotationNodes.getLength());
		for (int i = 0; i < annotationNodes.getLength(); i++) {
			textPosition = 0;
    		annotationNode = annotationNodes.item(i);
    		
    		if (annotationNode.getNodeType() == Node.ELEMENT_NODE) {
    			annotationElement = (Element) annotationNode;	
    			textNodes = annotationElement.getElementsByTagName("text");
    			text = textNodes.item(0).getTextContent();
    			id = annotationElement.getAttribute("id");

    			//System.out.println(annotationElement.getAttribute("id"));
    			go = new GeneOntology();
    			gene = "";
    			infoNodes = annotationElement.getElementsByTagName("infon");
    			for (int j = 0; j < infoNodes.getLength(); j++) {
    				infoNode = infoNodes.item(j);
    				if (infoNode.getNodeType() == Node.ELEMENT_NODE) {
    					nodeMap = infoNode.getAttributes(); // Only one attribute here
    					attrNode = nodeMap.item(0);
    					attrName = attrNode.getNodeValue();
    					if (attrName.equals("gene")) {
    						gene = infoNode.getTextContent();
    					} else if (attrName.equals("go-term")) {
    						//System.out.println(infoNode.getTextContent());
    						partsOfGoterm = infoNode.getTextContent().split("\\|");
    						go.setGoTerm(partsOfGoterm[0]);
    						go.setGoId(partsOfGoterm[1]);
    					} else if (attrName.equals("goevidence")) {
    						go.setEvidence(infoNode.getTextContent());   						
    					} /*else if (attrName.equals("type")) {
    						annotation.setType(infoNode.getTextContent()); 
    					}*/
    				}
    			}
    			
    			if (gene.length() == 0) continue;
	    		annotation = new Annotation();
	    		annotation.setId(annotationElement.getAttribute("id"));				
	    		annotation.setPsgType(psgType);
	    		annotation.setPsgOffset(psgOffset);
	    		annotation.setGo(go);
	    		annotation.setGene(gene);
	    		annotation.setText(text);
	    		annotations.add(annotation);    			
    			
//    			if (go.getGoId().equals("")) {
//    				if (print) {
//    					System.out.println("abnormal annotation @ " + psgOffset);
//    					System.out.println("skipping ... ");
//    				}
//    				//System.exit(0);
//    				break;
//    			}
//    			
//    			locationNodes = annotationElement.getElementsByTagName("location");
//    			
//    			
//    			for (int j = 0; j < locationNodes.getLength(); j++) {
//    				annotation = new Annotation();
//    				locationNode = locationNodes.item(j);
//    				locationElement = (Element) locationNode;
//    				offset = Integer.parseInt(locationElement.getAttribute("offset"));
//    				length = Integer.parseInt(locationElement.getAttribute("length"));
//
//        			//System.out.println(offset);
//        			//System.out.println(text);
//        			if (length != text.length()) {
//        				if (print) {
//        					System.out.println(length + " " + text.length());
//        				}
//        				annotation.setSentence(text.substring(textPosition, textPosition + text.length()));
//        				if (locationNodes.getLength() > 1) {
//        					//System.out.println(offset);
//        					//System.out.println(text);
//                			annotation.setId(annotationElement.getAttribute("id"));
//                			annotation.setGo(go);
//                			annotation.setGene(gene);
//                			annotations.add(annotation);
//                			if (print) {
//                				System.out.println("Warning: need to fix it later!");
//                			}
//        					//System.exit(0);
//        					break; //!!!!!!!!!!!
//        				}
//        			} else {
//        				//annotation.setSentence(text.substring(textPosition, textPosition + length));
//        				annotation.setSentence(text.substring(textPosition, textPosition + text.length()));
//        			}
//        			
//        			annotation.setId(annotationElement.getAttribute("id"));
//    				annotation.setOffset(offset);
//    				annotation.setLength(length);				
//        			annotation.setPsgType(psgType);
//        			annotation.setPsgOffset(psgOffset);
//        			textPosition += length;
//        			annotation.setGo(go);
//        			annotation.setGene(gene);
//        			annotations.add(annotation);
//        			//System.out.println("Annotation Offset: " + annotation.getOffset());
//        			/*
//        			System.out.println("====================================================");
//            		System.out.println("PsgOffset: " + annotation.getPsgOffset());
//            		System.out.println("PsgType: " + annotation.getPsgType());
//            		System.out.println("Annotation_id: " + annotation.getId());
//            		System.out.println("Gene: " + annotation.getGene());
//            		System.out.println("go-term: " + annotation.getGo().getGoTerm() + "|" + annotation.getGo().getGoId());
//            		System.out.println("goevidence: " + annotation.getGo().getEvidence());
//            		System.out.println("Type: " + annotation.getType());
//            		System.out.println("Length: " + annotation.getLength());
//            		System.out.println("Offset: " + annotation.getOffset());
//            		System.out.println("Text: " + annotation.getSentence());
//        			System.out.println("====================================================");
//    				*/
//        			
//        			
//        			
//        			break;
//        			
//    			}  
    		
    		}
		}
		
	}
	
	public static ArrayList<Annotation> getAnnotations(String filepath) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		try {
	    	File xmlFile = new File(filepath);
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(xmlFile);
	    	doc.getDocumentElement().normalize();
	    	//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	    	
	    	Element psgElement;
	    	NodeList offsetNodes, infoNodes;
	    	Node psgNode;
	    	NodeList psgNodes = doc.getElementsByTagName("passage"); // Get passages
	    	String psgType;
	    	for (int i = 0; i < psgNodes.getLength(); i++) {
	    		psgNode = psgNodes.item(i);
	    		if (psgNode.getNodeType() == Node.ELEMENT_NODE) {
	    			psgElement = (Element) psgNode;
	    			offsetNodes = psgElement.getElementsByTagName("offset"); // Note: only one offset tag
	    			infoNodes = psgElement.getElementsByTagName("infon");
	    			psgType = infoNodes.item(0).getTextContent();
	    			extractAnnotations(Integer.parseInt(offsetNodes.item(0).getTextContent()), psgType, psgElement, annotations);
	    		}
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();		
	    }
		
		return annotations;		
	}
	
	// Read the triples
	public static ArrayList<Triple> getTriples(String filepath) throws IOException {
		ArrayList<Triple> triples = new ArrayList<Triple>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line;
		String[] parts;
		Triple triple;
		
		while ( (line = reader.readLine())!= null) {
			parts = line.split(" ");
			triple = new Triple();
			triple.setGoId(parts[0]);
			triple.setPmid(parts[1]);
			triple.setEvidence(parts[2]);
			triples.add(triple);
		}
		reader.close();
		return triples;
	}
	
	/**
	 * Reada Indri results in TREC format and returns a map (query:result)
	 * Note that the document ID are normalized to PMID for Medline articles.
	 * 
	 * @param top Number of the top-ranked result to store in the map
	 *
	 */
	public static HashMap<String, ArrayList<IndriResult>> getIdToResults(String filepath, int top) throws IOException {
		HashMap<String, ArrayList<IndriResult>> idToResults = new HashMap<String, ArrayList<IndriResult>>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line, id, document;
		String[] parts, items;
		ArrayList<IndriResult> results;
		IndriResult result;
		
		while ( (line = reader.readLine())!= null) {
			items = line.split(" ");
			result = new IndriResult();
			id = items[0];
			result.setId(id);
			document = items[2];
			
			if ("/".equals(document.subSequence(0, 1))) {
				//System.out.println("matched");
				parts = document.split("/");
				//System.out.println(parts[parts.length-1]);
				document = parts[parts.length-1];
			}
			
			result.setDocument(document);
			result.setRank(Integer.parseInt(items[3]));
			result.setScore(Float.parseFloat(items[4]));
			if (!idToResults.containsKey(id)) {
				results = new ArrayList<IndriResult>();
				results.add(result);
				idToResults.put(id, results);
			} else {
				results = idToResults.get(id);
				if (top != -1 && results.size() < top) {
					idToResults.get(id).add(result);
				}
			}
		}
		reader.close();
		return idToResults;
	}
	
	public static ArrayList<Query> getUniqueQueryFromAnnotation(ArrayList<Annotation> annots) {
		TreeMap<Query, Query> queryMap = new TreeMap<Query, Query>();
		Query query;
		ArrayList<Query> uniqueQueries = new ArrayList<Query>();
		
		for (Annotation annot : annots) {
			query = new Query();
			query.setPsgOffset(annot.getPsgOffset());
			query.setPsgType(annot.getPsgType());
			query.setOffset(annot.getOffset());
			query.setId();
			query.setText(annot.getText());
			query.setGene(annot.getGene());
			//query.printInfo();
			
			if (queryMap.containsKey(query)) {
					continue;
			} else {
				queryMap.put(query, query);
			}
		}
		
		for (Query q : queryMap.keySet()) {
			//System.out.println(q.getOffset());
			uniqueQueries.add(queryMap.get(q));
		}
		
		return uniqueQueries;
	}

	public static ArrayList<Query> getQueryFromAnnotation(ArrayList<Annotation> annots) {
		Query query;
		ArrayList<Query> queries = new ArrayList<Query>();
		
		//System.out.println(annots.size());
		for (Annotation annot : annots) {
			query = new Query();
			query.setPsgOffset(annot.getPsgOffset());
			query.setPsgType(annot.getPsgType());
			query.setOffset(annot.getOffset());
			query.setId();
			query.setText(annot.getText());
			query.setGene(annot.getGene());
			//query.printInfo();
			//System.out.println(query.getOffset()+ " " + query.getGene());
			queries.add(query);
		}
		return queries;
	}
	
	public static void main(String[] args) throws IOException {
		String dataPath = "/home/zhu/workspace/Bioc/data/";
		//String infilePath = dataPath + "goldstandard/annotation_22792398.xml";
		//getAnnotations(infilePath);
		
		/*
		// Test triples
		String triplePath = dataPath + "triples.unique";
		ArrayList<Triple> triples = getTriples(triplePath);
		for (Triple triple : triples) {
			System.out.println(triple.getGo() + " " + triple.getPmid() + " " + triple.getEvidence());
		}
		*/
		
		// Test Indri Result
		String resultPath = dataPath + "sampletestquery.result";
		HashMap<String, ArrayList<IndriResult>> idToResults = getIdToResults(resultPath, 50);
		ArrayList<IndriResult> results;
		IndriResult result;
		for (String id: idToResults.keySet()) {
			results = idToResults.get(id);
			for (int i = 0; i < results.size(); i++) {
				result = results.get(i);
				System.out.println(result.getId() + " " + result.getDocument() + " " + result.getRank() + " " + result.getScore());
			}
		}
	}
}
