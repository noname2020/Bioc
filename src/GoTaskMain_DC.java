/**
 * 
 * 
 * @author zhu
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import retrieval.IndriMakeQuery;
import retrieval.LMDirichlet;
import type.Annotation;
import type.Query;
import type.ScorePR;
import type.Sentence;
import type.Triple;
import utility.Mapping;
import utility.Parser;
import utility.PlatformDetection;
import utility.PsgToSentXML;
import annotation.Annotator;
import annotation.SimpleAnnotator;
import classification.SimpleClassifier;
import evaluation.EvalClassification;
import evaluation.Validation;


public class GoTaskMain_DC {
	//private static String dataPath = System.getProperty("user.dir") + "/data/";
	private static int runId = 1;
	private static String dataPath;
	private static String inputDir;
	private static String modePath = "";
	private static String workingsetPath;
	private static String triplePath;
	private static String geneslimPath;
	private static String slimpmidPath;
	private static String gold1aOutPath;

	private static HashSet<String> workingset;
	private static HashMap<String, ArrayList<Triple>> pmidToTriples;
	private static HashMap<String, HashSet<String>> geneToGopmids;
	private static SimpleClassifier classifier;
	private static Annotator annotator;
	private static PsgToSentXML convertor;
	private static HashMap<String, HashSet<String>> slimToGopmids;


	private static int numTopPmid = 20;
	private static int numTopGo = 5;

	private static Pattern pattern = Pattern.compile(".*\\((\\d+)\\)");
	private static Pattern patternMultiple = Pattern.compile(".*\\(([\\d;]+)\\)");
	private static PlatformDetection platformDetect;

	/**
	 * Initialization
	 * 1. Create classifier
	 * 2. Building workingset for Indri query
	 * 3. Read pmid to triples mapping
	 * 4. Create annotator and set pmidToTriples mapping
	 * 
	 * @throws IOException
	 */

	public static void initialize(String mode) throws IOException {
		System.out.println(dataPath+" "+dataPath.indexOf("/"));
		workingsetPath = "data/workingset.txt";
		triplePath = "data//triples.unique";
		geneslimPath = "data/GeneID.2GOSLIM";
		slimpmidPath = "data/slimpmid.txt";
		//workingsetPath = dataPath.substring(0,dataPath.indexOf("/")) + "/workingset.txt";
		//triplePath = dataPath.substring(0,dataPath.indexOf("/")) + "/triples.unique";
		//geneslimPath = dataPath.substring(0,dataPath.indexOf("/")) + "/GeneID.2GOSLIM";
		//slimpmidPath = dataPath.substring(0,dataPath.indexOf("/")) + "/slimpmid.txt";
		//gold1aOutPath = dataPath.substring(0,dataPath.indexOf("/")) +"/goldtask1";
		gold1aOutPath = dataPath +"/" +inputDir;
		if (!mode.equals("train")) {
			pmidToTriples = Mapping.makePmidToTriples(Parser.getTriples(triplePath));
			workingset = Parser.getSameWorkingset(workingsetPath);
			classifier = new SimpleClassifier();
			annotator = new SimpleAnnotator();
			annotator.setPmidToTriples(pmidToTriples);
			slimToGopmids = Mapping.getSlimToGopmids(slimpmidPath);
			Parser.printInfo(false);
			annotator.printInfo(false);
			Validation.printInfo(false);
		} 
		convertor = new PsgToSentXML();
		platformDetect = new PlatformDetection();
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
		System.out.println("# of queries after filtering: " + validQueries.size());
		printInfo("Done");
		return validQueries;
	}

	public static void annotate(String pmid) throws IOException {
		String resultPath = dataPath + "results/" + pmid + ".results";
		String submissionPath = dataPath + "submissions/" + pmid + "." + numTopGo + "." + numTopPmid;
		System.out.println(submissionPath);
		//printInfo("Annotating " + resultPath);
		//		ArrayList<Query> queries = new ArrayList<Query>();
		//		String goldOut = dataPath + "goldtask1/" + pmid + ".txt";
		//		String line, geneId, goId, sentence;
		//		BufferedReader reader = new BufferedReader(new FileReader(goldOut));
		//		Query query;
		//		String[] items;
		//		HashSet<String> identifiedGeneSet = new HashSet<String>();
		//		System.out.println(pmid);
		//		String signature, queryId;
		//		HashSet<String> signatureSet = new HashSet<String>();
		//		int n = 1;
		//		while ((line = reader.readLine()) != null) {
		//			items = line.split("\\|\\|");
		//			geneId = items[1];
		//			//goId = items[2];
		//			//sentence = items[3];
		//			signature = geneId + " " + sentence;
		//			
		//			if (signatureSet.contains(signature)) continue;
		//			signatureSet.add(signature);			
		//			
		//			query = new Query();
		//			identifiedGeneSet.add(geneId);
		//			query.setId(n + "-" + geneId);
		//			query.setGene(geneId);
		//			query.setText(sentence);
		//			queries.add(query);
		//			n++;
		//		}
		//		
		//		Parser.makeTokenSentences(queries);

		//		readFromClassification(pmid, queries);
		annotator.annotate(resultPath, submissionPath, annotator, pmid, numTopGo, numTopPmid);
		//printInfo("Done");
	}

	public static void retrieve(Collection<Query> queries, String paramPath, String resultPath, boolean rewrite) throws IOException {
		printInfo("Formulating queries ");
		IndriMakeQuery qlmodel = new LMDirichlet(50, 500, queries);

		if (runId == 1) {
			qlmodel.addIndex("/data5/bsi/nlp/s110067.sharp/BioCreative/index/pmc-stemming/");
			qlmodel.addIndex("/data5/bsi/nlp/s110067.sharp/BioCreative/index/bioasq_train_indri/");
		} else if (runId == 2) {
			
			qlmodel.addIndex("../index/generif.index");
		}
		//qlmodel.addIndex("~/index/pmc-stemming/");
		//qlmodel.addIndex("~/index/bioasq_train_indri/");
		//for (Query query : qlmodel.getQueries()) {
		//query.setWorkingset(workingset);
		/*
			System.out.println("Passage offset: " + query.getPsgOffset());
			System.out.println("Sentence offset: " + query.getOffset());
			System.out.println("Query: " + query.getQuery());
			System.out.println("Text: " + query.getSentence());
			System.out.println("Text length: " + query.getLength());
			System.out.println();
		 */
		//}		
		System.out.println("# of queries: " + qlmodel.getQueries().size());		

		if ((!checkExistence(paramPath)) || rewrite) {
			System.out.println("Writing to " + paramPath);
			qlmodel.writeParam(paramPath);
		} else {
			System.out.println("File already exists: " + paramPath);
		}

		printInfo("Done");

		printInfo("Running queries ");

		if ((!checkExistence(resultPath)) || rewrite) {
			// TODO: IndriRunQuery
		} else {
			System.out.println("File already exists: " + resultPath);
		}

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

	public static HashSet<String> getReducedWorkingset(HashSet<String> gopmids) {
		HashSet<String> reducedWorkingset = new HashSet<String>();
		String[] items;
		String pmcpmid, pmid;
		for(String gopmid : gopmids) {
			items = gopmid.split(" ");
			pmid = items[1];
			pmcpmid = "/home/dongqing/data/pmc/fulltext-pmid/" + pmid;
			if (workingset.contains(pmcpmid)) reducedWorkingset.add(pmid);
			else reducedWorkingset.add(pmid);
			//System.out.println(pmid);
		}
		//.out.println();
		//System.exit(0);
		return reducedWorkingset;
	}

	public static void readFromClassification(String pmid, ArrayList<Query> queries) throws IOException {
		//String goldOut = gold1aOutPath +"/" + pmid + ".txt";
		String goldOut = gold1aOutPath +"/" + pmid + ".txt";
		HashMap<String, String> pmidToLines = new HashMap<String, String>();
		String line, geneId, goId, sentence;
		File goldOutFile = new File(goldOut);
		if(!goldOutFile.exists()){
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(goldOut));
		Query query;
		String[] items;
		HashSet<String> identifiedGeneSet = new HashSet<String>();
		System.out.println(pmid);
		String signature, queryId;
		HashSet<String> signatureSet = new HashSet<String>();
		int n = 1;
		while ((line = reader.readLine()) != null) {
			//items = line.split("\\|\\|");
			items = line.split("\\t");
			geneId = items[1];
			//goId = items[2];
			sentence = items[2];
			signature = geneId + " " + sentence;

			if (signatureSet.contains(signature)) continue;
			signatureSet.add(signature);			

			query = new Query();
			identifiedGeneSet.add(geneId);
			query.setId(n + "-" + geneId);
			query.setGene(geneId);
			query.setText(sentence);
			queries.add(query);
			n++;
		}

		geneToGopmids = Mapping.makeGeneToPmids(identifiedGeneSet, slimToGopmids, geneslimPath);

		for (int i = 0; i < queries.size(); i++) {
			query = queries.get(i);
			geneId = query.getGene();
			if(runId==2){
				workingset = null;
				continue;
			}
			//System.out.println(geneId);
			if ( !geneToGopmids.containsKey(query.getGene()) ) {
				query.setWorkingset(workingset);
			} else {
				query.setWorkingset(getReducedWorkingset(geneToGopmids.get(geneId)));
			}
			System.out.println("queryId: " + query.getId() + "\tWorkingsetLength: " + query.getWorkingset().size() + "\t query:" + query.getText());
		}


		Parser.makeTokenSentences(queries);


		//		for (String key : geneToGopmids.keySet()) {
		//			System.out.println(key + " " + geneToGopmids.get(key));
		//		}
		//		System.exit(0);
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

	public static void runTest(String pmid) throws IOException, XMLStreamException {
		// Split and output XML files that contains sentences
		ArrayList<Query> queries = new ArrayList<Query>();
		//<<<<<<< HEAD
		//String inXML = System.getProperty("user.dir") + "/data/articles/" + pmid + ".xml";
		//String outXML = System.getProperty("user.dir") + "/data/articles_sent/" + pmid + ".xml";
		//String inXML = dataPath + "/bc4go_dev_v081213/articles/" + pmid + ".xml";
		//String outXML = dataPath + "/bc4go_dev_v081213/articles_sent/" + pmid + ".xml";
		//String inXML = dataPath +"/" + modePath +"articles_missingGenes/" + pmid + ".xml";
		//String outXML = dataPath +"/" + modePath+"/articles_sent_missingGenes/" + pmid + ".xml";
		String inXML = dataPath + "articles/" + pmid + ".xml";
		String outXML = dataPath + "articles_sent/" + pmid + ".xml";
		//>>>>>>> branch 'master' of https://github.com/noname2020/Bioc.git
		//System.out.println("inXML: "+inXML + " outXML:"+outXML);
		if (! checkExistence(outXML)) {
			convertor.split(inXML, outXML);
			/* Alternative: get sentences out of the convertor directly
	        sentences = convertor.getSentences();
	    	for (int i = 0; i < sentences.size(); i++) {
	    		sentence = sentences.get(i);
	    		System.out.print("psgOff:" + sentence.getPsgOffset() + " off:" + sentence.getOffset() + " len:" + sentence.getLength() + " ");
	    		System.out.println(sentence.getText());
	    	}
			 */
		}

		if(!platformDetect.getOs().equals(platformDetect.OS_LINUX)){
			return;
		}

		System.out.println("Formulating queries ... ");
		readFromClassification(pmid, queries);
		//read(outXML, queries);
		//queries = filter(queries);

		String paramPath = dataPath + "queries/" + pmid + ".param";
		String resultPath = dataPath + "results/" + pmid + ".result";

		retrieve(queries, paramPath, resultPath, true);
		//System.exit(0);
		//annotate(queries, resultPath, annotator, pmid);

	}

	public static void runTrain(String pmid) throws IOException {
		HashMap<Query, Query> queryMap;
		ArrayList<ScorePR> allScores = new ArrayList<ScorePR>();
		ArrayList<Sentence> sentences;
		//PsgToSentXML convertor = new PsgToSentXML();
		//Sentence sentence;
		//String paramPath = dataPath + "queries/" + pmid + ".param";
		//String resultPath = dataPath + "results/" + pmid + ".result";	
		String goldPath = dataPath + "goldstandard/annotation_" + pmid + ".xml";
		String goldOut = dataPath + "goldtask1/" + pmid + ".txt";
		PrintStream goldOutTrain = new PrintStream(goldOut);

		Matcher matcher;
		String geneID = "";
		HashSet<String> goldSet = new HashSet<String>();

		ArrayList<Annotation> annots = Parser.getAnnotations(goldPath);	    
		String gold;
		if (annots.size() == 0) System.out.print(pmid + " has no annotations!");
		//goldOutTrain.println(pmid);
		for (Annotation annot : annots) {
			//gold = annot.getOffset() + " " + annot.getGene() + " " + annot.getGo().getGoId() + " " + annot.getGo().getEvidence();
			matcher = pattern.matcher(annot.getGene());
			if (matcher.find( )) {
				geneID = matcher.group(1);
			} else {
				//System.out.println("NO MATCH:" + annot.getGene());

				matcher = patternMultiple.matcher(annot.getGene());
				if (matcher.find( )) {
					geneID = matcher.group(1);
				} else {
					//TODO
					//System.out.println("Not matched: " + pmid + " " + annot.getGene());
					//System.exit(0);
					continue;
				}
				String[] geneIDs = geneID.split(";");
				for (String id : geneIDs) {
					gold = pmid + "||" + id + "||" + annot.getGo().getGoId() + "||" + annot.getText();
					if (goldSet.contains(gold)) continue;
					System.out.println(pmid + "||" + id + "||" + annot.getGo().getGoId() + "||" + annot.getText());
					goldSet.add(gold);
					goldOutTrain.println(gold);
				}
				continue;
				//System.exit(0);
				// 22253607 843513;840320;829480;815862 GO:0009742
			}

			gold = pmid + "||" + geneID + "||" + annot.getGo().getGoId() + "||" + annot.getText();
			if (goldSet.contains(gold)) continue;
			System.out.println(pmid + "||" + geneID + "||" + annot.getGo().getGoId() + "||" + annot.getText());
			goldSet.add(gold);
			//System.out.println(gold);
			goldOutTrain.println(gold);
		}

		goldOutTrain.close();
		System.out.println("Saved to " + goldOut);
		//System.exit(0);
		//ArrayList<Query> queries = Parser.getUniqueQueryFromAnnotation(annots);
		//Parser.makeTokenSentences(queries);


		//retrieve(queries, paramPath, resultPath, false);

		//annotate(queries, resultPath, annotator, pmid);





		//allScores.addAll(evalRanking(queries, goldPath));

		//System.out.println("Processing: " + goldPath + " ... ");

		//evalClassification(goldPath, queries);
		//		PrintStream outStream = new PrintStream("/home/zhu/workspace/Bioc/data/forpanther/" + pmid + ".goldannot");
		//		String temp;
		//		for (Annotation annot : annots) {
		//			temp = annot.getOffset() + " " + annot.getGene() + " " + annot.getGo().getGoId() + " " + annot.getGo().getEvidence();
		//			outStream.println(temp);
		//			System.out.println(temp);
		//		}			
		//		outStream.close();

	}


	public static void main(String[] args) throws IOException, XMLStreamException
	{			
		String mode="";//args[0]; //"test";

		if(args.length>3){
			mode = args[0];
			dataPath = args[1];
			inputDir = args[2];
			numTopPmid = Integer.parseInt(args[3]);
			numTopGo = Integer.parseInt(args[4]);
			runId = Integer.parseInt(args[5]);
		}else if(args.length==3){
			mode = args[0];
			dataPath = args[1];
			inputDir = args[2];
			modePath = "";
		}

		System.out.println(mode);
		//System.exit(0);
		initialize(mode); // Initialization		


		//for (numTopGo = 10; numTopGo <= 100; numTopGo += 10) {
		//for (numTopPmid = 10; numTopPmid <= 100; numTopPmid += 10) {

		String articlePath, pmid;
		String[] parts, items;
		//File articleDir = new File(dataPath + "/bc4go_dev_v081213/articles/");
		System.out.println("dataPath: "+dataPath);
		//File articleDir = new File(dataPath + "/articles_missingGenes/");
		File articleDir = new File(dataPath + "/articles/");
		for (File articleFile : articleDir.listFiles()) {
			articlePath = articleFile.getPath();
			parts = articlePath.split("/");
			items = parts[parts.length - 1].split("\\.");
			if (!items[1].equals("xml")) continue;
			pmid = items[0];
			if ("21901818".equals(pmid) || ("22870182".equals(pmid))) continue; //19074149 12213836 22792398

			if (mode.equals("test")) {
				runTest(pmid);
			} else if (mode.equals("train")) {
				System.out.println("Generating gold standards for " + pmid);
				runTrain(pmid);
				System.out.println();
			} else { //here, mode must be annot
				if(args.length==3){ //repeat the if condition, but we need to do different things.
					for(int i=1;i<=20;i++){
						numTopPmid = i;
						for(int j=5;j<=150;j+=5){
							numTopGo = j;
							annotate(pmid); //in this case, we will do i * j times annotate
						}
					}
				}else{//in this case, it means that numTopPmid and numTopGo are fixed
					annotate(pmid);
				}

			}
		}
	}
}
