/**
 * 
 * 
 * @author zhu
 */


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import annotation.Annotator;
import annotation.SimpleAnnotator;

import classification.SimpleClassifier;
import evaluation.EvalClassification;
import evaluation.Validation;

import retrieval.IndriMakeQuery;
import retrieval.LMDirichlet;
import type.Annotation;
import type.GeneOntology;
import type.IndriResult;
import type.Query;
import type.ScorePR;
import type.Sentence;
import type.Triple;
import utility.Mapping;
import utility.Parser;
import utility.PsgToSentXML;


public class GoTaskMain {
	private static String dataPath = System.getProperty("user.dir") + "/data/";//"/home/zhu/workspace/Bioc/data/";
	private static String workingsetPath = dataPath + "workingset.txt";
	private static String triplePath = dataPath + "triples.unique";
	///home/zhu/.gvfs/sftp on shannon/home/dongqing/work/bioc/testquery.param";
	
	private static HashSet<String> workingset;
	private static HashMap<String, ArrayList<Triple>> pmidToTriples;
	private static SimpleClassifier classifier;
	private static Annotator annotator;
	
	private static int numTopPmid = 100;
	private static int numTopGo = 50;
		
	/**
	 * Initialization
	 * 1. Create classifier
	 * 2. Building workingset for Indri query
	 * 3. Read pmid to triples mapping
	 * 4. Create annotator and set pmidToTriples mapping
	 * 
	 * @throws IOException
	 */
	
	public static void initialize() throws IOException {
		pmidToTriples = Mapping.makePmidToTriples(Parser.getTriples(triplePath));
		workingset = Parser.getSameWorkingset(workingsetPath);
		classifier = new SimpleClassifier();
		annotator = new SimpleAnnotator();
		annotator.setPmidToTriples(pmidToTriples);
	}
	
	/**
	 * Filter queries which are less likely to be GO related
	 * @param queries 
	 */

	public static ArrayList<Query> filter(ArrayList<Query> queries) {
		printInfo("Classifying queries ");
		if (queries == null || queries.size() == 0) {
			System.out.println("No queries");
			System.exit(0);
		}
		System.out.println("# of queries: " + queries.size());
		ArrayList<Query> validQueries = classifier.filterQueries(queries);
		System.out.println("# of queries: " + validQueries.size());
		printInfo("Done");
		return validQueries;
	}
	
	public static void annotate(ArrayList<Query> queries, String resultPath, Annotator annotator, String pmid) throws IOException {
		//printInfo("Annotating " + resultPath);
		annotator.annotate(queries, resultPath, annotator, pmid, numTopGo, numTopPmid);
		//printInfo("Done");
	}
	
	public static void retrieve(Collection<Query> queries, String paramPath, String resultPath) throws IOException {
		printInfo("Formulating queries ");
		IndriMakeQuery qlmodel = new LMDirichlet(100, 2500, queries);
		qlmodel.addIndex("/home/dongqing/index/pmc-stemming/");
		qlmodel.addIndex("/home/dongqing/index/bioasq_train_indri/");
		for (Query query : qlmodel.getQueries()) {
			query.setWorkingset(workingset);
			/*
			System.out.println("Passage offset: " + query.getPsgOffset());
			System.out.println("Sentence offset: " + query.getOffset());
			System.out.println("Query: " + query.getQuery());
			System.out.println("Text: " + query.getSentence());
			System.out.println("Text length: " + query.getLength());
			System.out.println();
			*/
		}		
		System.out.println("# of queries: " + qlmodel.getQueries().size());		
		
		
		//System.out.println("Writing to " + paramPath);
		//qlmodel.writeParam(paramPath);
		if (! checkExistence(resultPath)) {
			System.out.println("Writing to " + paramPath);
			qlmodel.writeParam(paramPath);
			
		} else {
			System.out.println("File already exists: " + paramPath);
		}
		
		printInfo("Done");
		
		printInfo("Running queries ");
		
//		if (! checkExistence(resultPath)) {
//			// TODO: IndriRunQuery
//		} else {
//			System.out.println("File already exists: " + resultPath);
//		}
		
		printInfo("Done");
	}
	
	public static boolean checkExistence(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	
	public static void printInfo(String info) {
		int half = 40;
		StringBuffer buffer = new StringBuffer();
		int k = half - info.length()/2 - 2;
		for (int i = 0; i < k; i++) {
			buffer.append("=");
		}
		buffer.append("  ");
		buffer.append(info);
		buffer.append("  ");
		for (int i = 0; i < k; i++) {
			buffer.append("=");
		}
		buffer.append("\n");
		System.out.println(buffer.toString());			
	}

	/** 
	 * Extract sentences from the article and store them in query objects.
	 * 
	 * @param articlePath
	 * @param queries
	 */

	public static void read(String articlePath, ArrayList<Query> queries) {
		printInfo("Reading queries ");
		Parser.getSentences(articlePath, queries); // Read articles and formulate queries
		
		//queries.get(0).printInfo();queries.get(1).printInfo();System.exit(0);
		
		System.out.println("# of queries: " + queries.size());
		printInfo("Done");
	}
	
	public static ArrayList<ScorePR> evalRanking(ArrayList<Query> queries, String goldPath) {
		//printInfo("Evaluating results");
		ArrayList<ScorePR> scores = Validation.getEvals(queries, goldPath);
		//printInfo("Done");
		return scores;
	}
	
	public static void evalClassification(String goldPath, ArrayList<Query> queries) {
		printInfo("Evaluate classification");
		EvalClassification classEvaluator = new EvalClassification();
		classEvaluator.evaluate(Parser.getAnnotations(goldPath), queries);
		printInfo("Done");
	}
	
	
	public static void main(String[] args) throws IOException, XMLStreamException
	{			
		//Validation.checkInitialSplit();
		///projects/ontoBioT/data/script/GOAnnot.Generif
		
		///projects/ontoBioT/data/script/GeneID.2GOSLIM         GeneID\tPANTHERSLIM
		///projects/ontoBioT/data/script/ForDongqing.final	iProclass: GOSLIMID, GOID, PMID
		
		///projects/ontoBioT/data/script/GOPMID.stat 
		///projects/ontoBioT/data/script/addGOPath.out
		
		
/**
 GeneID.2GOSLIM
GeneID\tPANTHERSLIM
PANTHER knowledge base
Predicted GOSLIM function for the corresponding gene
ForDongqing.final
iProclass
GOSLIMID, GOID, PMID
Training
Step 1: Given Gene, we have a list of GOSLIM
For that GOSLIM, we have a list of PMIDs
I may only need to search for GO Terms in the above PMIDs
GIven a gene, from Panther, we got GOSLIM terms
GOPMID.stat
addGOPath.out

 */
		
		initialize(); // Initialization

		
		Parser.printInfo(false);
		annotator.printInfo(false);
		Validation.printInfo(false);
		
		//for (numTopGo = 10; numTopGo <= 100; numTopGo += 10) {
		//for (numTopPmid = 10; numTopPmid <= 100; numTopPmid += 10) {
			
		
		File articleDir = new File(dataPath + "articles_sent/"); 
		String articlePath, resultPath, paramPath, pmid;
		ArrayList<Query> queries = null;
		String[] parts, items;
		String goldPath;		
		ArrayList<Annotation> annots;
		HashMap<Query, Query> queryMap;
		ArrayList<ScorePR> allScores = new ArrayList<ScorePR>();
		
		
		for (File articleFile : articleDir.listFiles()) {
			articlePath = articleFile.getPath();
			
			parts = articlePath.split("/");
			items = parts[parts.length - 1].split("\\.");
			
			if (!items[1].equals("xml")) {
				continue;
			}
			
			pmid = items[0];
		
//			if (!"22016430".equals(pmid)) { //19074149
//				continue;
//			}
			
			paramPath = dataPath + "queries/" + pmid + ".param";
			
	        //split.split(args[0], args[1]);
	        
	        String inXML = "/home/zhu/.gvfs/sftp on shannon/home/dongqing/data/bioc/articles/22792398.xml";
	        String outXML = System.getProperty("user.dir") + "/data/out.xml";//"/home/zhu/.gvfs/sftp on shannon/home/dongqing/data/bioc/out.xml";
	    	
	        PsgToSentXML convertor = new PsgToSentXML();
	        convertor.split(inXML, outXML);
	        ArrayList<Sentence> sentences = convertor.getSentences();
	        Sentence sentence;
	    	for (int i = 0; i < sentences.size(); i++) {
	    		sentence = sentences.get(i);
	    		sentence.setOffset(sentence.getOffset());
	    		System.out.print("psgOff:" + sentence.getPsgOffset() + " off:" + sentence.getOffset() + " len:" + sentence.getLength() + " ");
	    		System.out.println(sentence.getText());
	    	}
	    	
	    	
			//paramPath = "/home/zhu/.gvfs/sftp on shannon/home/dongqing/work/bioc/"+ "queries/" + pmid + ".param"; 
			
			resultPath  = dataPath + "results/" + pmid + ".result";
			goldPath = dataPath + "goldstandard/annotation_" + pmid + ".xml";
			
			//System.out.println("Processing: " + goldPath + " ... ");
			annots = Parser.getAnnotations(goldPath);
			
//			for (Annotation annot : annots) {
//				System.out.println("Out Offset: " + annot.getOffset());
//			}
//			System.exit(0);
			PrintStream outStream = new PrintStream("/home/zhu/workspace/Bioc/data/forpanther/" + pmid + ".goldannot");
			String temp;
			for (Annotation annot : annots) {
				temp = annot.getOffset() + " " + annot.getGene() + " " + annot.getGo().getGoId() + " " + annot.getGo().getEvidence();
				outStream.println(temp);
				System.out.println(temp);
			}
			//System.exit(0);
			outStream.close();
			//queries = Parser.getUniqueQueryFromAnnotation(annots);
			//Parser.makeTokenSentences(queries);
			//retrieve(queries, paramPath, resultPath);
			
			
			queries = Parser.getQueryFromAnnotation(annots);
			annotate(queries, resultPath, annotator, pmid);
			allScores.addAll(evalRanking(queries, goldPath));
			
			//System.exit(0);
		}
		
		//System.exit(0);
		
		double p = 0.0, r = 0.0;
		int n = 0;
//		for (ScorePR score : allScores) {
//			System.out.println("Precision: " + score.getPrecision() + "\tRecall: " + score.getRecall());
//			p += score.getPrecision();
//			r += score.getRecall();
//			n++;
//		}
//		
//		System.out.println("TopPmid:" + numTopPmid + "\tTopGo:" + numTopGo + "\tPrec:" + p/n + "\tRecall:" + r/n);
		
		
			//}}
		
		
//		initialize(); // Initialization
//		
//		File articleDir = new File(dataPath + "articles_sent/"); 
//		String articlePath, resultPath, paramPath, pmid;
//		ArrayList<Query> queries = null;
//		String[] parts, items;
//		String goldPath;
//		
//		for (File articleFile : articleDir.listFiles()) {
//			articlePath = articleFile.getPath();
//			
//			parts = articlePath.split("/");
//			items = parts[parts.length - 1].split("\\.");
//			
//			if (!items[1].equals("xml")) {
//				continue;
//			}
//			
//			pmid = items[0];
//			paramPath = dataPath + pmid + ".param"; 
//			resultPath  = dataPath + pmid + ".result";
//			goldPath = dataPath + "goldstandard/annotation_" + pmid + ".xml";
//			
//			// Test article
////			if (!"22702356".equals(pmid)) { //22792398
////				continue;
////			}
//			
//			System.out.println("Processing: " + articlePath + " ... ");
//			
//			
//			
//			queries = new ArrayList<Query>();
//			
//			
//			//read(articlePath, queries);
//			//queries = filter(queries);
//			//evalClassification(goldPath, queries);
//			
//			
//			
//			retrieve(queries, paramPath, resultPath);
////			
////			annotate(queries, resultPath, annotator);
////			
//			validate(queries, goldPath);
//		}
	}
}
