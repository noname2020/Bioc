package utility;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;

import com.aliasi.util.Files;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import type.Sentence;

/** Use SentenceModel to find sentence boundaries in text */
public class SentenceSplitter {

	static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
    static final SentenceModel SENTENCE_MODEL  = new MedlineSentenceModel();

    
    public static ArrayList<Sentence> split(String text, int psgOffset) {
    	List<String> tokenList = new ArrayList<String>();
    	List<String> whiteList = new ArrayList<String>();
    	Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(),0,text.length());
    	tokenizer.tokenize(tokenList,whiteList);

    	//System.out.println(tokenList.size() + " TOKENS");
    	//System.out.println(whiteList.size() + " WHITESPACES");

    	String[] tokens = new String[tokenList.size()];
    	String[] whites = new String[whiteList.size()];
    	tokenList.toArray(tokens);
    	whiteList.toArray(whites);
    	int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens,whites);
    	
    	//System.out.println(sentenceBoundaries.length + " SENTENCE END TOKEN OFFSETS");
    	ArrayList<Sentence> sentences = new ArrayList<Sentence>();
    	Sentence sentence;
    	if (sentenceBoundaries.length < 1) {
    	    //System.out.println("No sentence boundaries found.");
    		sentence = new Sentence();
    	    sentence.setText(text);
    	    sentence.setLength(text.length());
    	    sentence.setOffset(psgOffset);
    	    sentence.setPsgOffset(psgOffset);
    	    sentences.add(sentence);
    		return sentences;
    	}
    	
    	int sentStartTok = 0;
    	int sentEndTok = 0;
    	int offset = 0;
    	StringBuffer buffer;
    	String sentText;
    	for (int i = 0; i < sentenceBoundaries.length; ++i) {
    	    sentEndTok = sentenceBoundaries[i];
    		buffer = new StringBuffer();
    	    //System.out.println("SENTENCE "+(i+1)+": ");
    	    for (int j=sentStartTok; j<=sentEndTok; j++) {
    		//System.out.print(tokens[j]+whites[j+1]);
    	    	buffer.append(tokens[j]+whites[j+1]);
    	    }
    	    //System.out.println();
    	    sentText = buffer.toString();
    	    sentText = sentText.substring(0, sentText.length());
    	    while (sentText.length() > 0 && sentText.charAt(sentText.length()-1) == ' ') {
    	    	sentText = sentText.substring(0, sentText.length() - 1);
    	    }
    	    
    	    sentence = new Sentence();
    	    sentence.setText(sentText);
    	    sentence.setOffset(offset + psgOffset);
    	    sentence.setPsgOffset(psgOffset);
       	    sentence.setLength(sentText.length());
       	    sentences.add(sentence);
       	    
       	    sentStartTok = sentEndTok+1;
       	    offset += sentText.length() + 1; // !!!
    	}
    	
    	return sentences;
    }
    
    public static void main(String[] args) throws IOException {
    	String text = "Glutathione conjugation reactions are one of the principal mechanisms that plants utilize to detoxify xenobiotics. The induction by four herbicides (2,4-D, atrazine, metolachlor and primisulfuron) and a herbicide safener (dichlormid) on the expression of three genes, ZmGST27, ZmGT1 and ZmMRP1, encoding respectively a glutathione-S-transferase, a glutathione transporter and an ATP-binding cassette (ABC) transporter was studied in maize. The results demonstrate that the inducing effect on gene expression varies with both chemicals and genes. The expression of ZmGST27 and ZmMRP1 was up-regulated by all five compounds, whereas that of ZmGT1 was increased by atrazine, metolachlor, primisulfuron and dichlormid, but not by 2,4-D. For all chemicals, the inducing effect was first detected on ZmGST27. The finding that ZmGT1 is activated alongside ZmGST27 and ZmMRP1 suggests that glutathione transporters are an important component in the xenobiotic detoxification system of plants.";
    	//System.out.println(text);
    	int psgOffset = 118;
    	ArrayList<Sentence> sentences = SentenceSplitter.split(text, psgOffset);
    	Sentence sentence;
    	
    	String s = "The expression of ZmGST27 and ZmMRP1 was up-regulated by all five compounds, whereas that of ZmGT1 was increased by atrazine, metolachlor, primisulfuron and dichlormid, but not by 2,4-D.";
    	System.out.println(s.length());
    	for (int i = 0; i < sentences.size(); i++) {
    		sentence = sentences.get(i);
    		sentence.setOffset(sentence.getOffset());
    		System.out.print(sentence.getOffset() + " " + sentence.getLength() + " ");
    		System.out.println(sentence.getText());
    	}
    }
}
