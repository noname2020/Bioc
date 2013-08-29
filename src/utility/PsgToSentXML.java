package utility;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import type.Sentence;
import utility.SentenceSplitter;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.BioCSentence;
import bioc.io.BioCDocumentReader;
import bioc.io.BioCDocumentWriter;
import bioc.io.BioCFactory;
import bioc.util.CopyConverter;



public class PsgToSentXML {
	
	private ArrayList<Sentence> sentences = new ArrayList<Sentence>();
	
	
	
	public class SentenceConverter extends CopyConverter {

	    @Override
	        public BioCPassage getPassage(BioCPassage in) {
	        BioCPassage out = new BioCPassage();
	        int psgOffset = in.getOffset();
	        out.setOffset( psgOffset );
	        out.setInfons( in.getInfons() );

	        String text = in.getText();
	        BioCSentence boicSentence;
	        
	    	Sentence sentence;
	    	ArrayList<Sentence> sents = SentenceSplitter.split(text, psgOffset);
	    	for (int i = 0; i < sents.size(); i++) {
	    		sentence = sents.get(i);
	    		sentence.setOffset(sentence.getOffset());
	    		//System.out.print(sentence.getOffset() + " " + sentence.getLength() + " ");
	    		//System.out.println(sentence.getText());
	    		boicSentence = new BioCSentence();
	    		boicSentence.setOffset( sentence.getOffset() );
	    		boicSentence.setText( sentence.getText() );
	    		out.addSentence( boicSentence );
	    	}
	    	
	    	sentences.addAll(sents);
	        return out;
	    }
	}
	
	public void split(String inXML, String outXML)
            throws XMLStreamException, IOException {

        	BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);
        	BioCDocumentReader reader =
        			factory.createBioCDocumentReader(new FileReader(inXML));
        	BioCDocumentWriter writer =
        			factory.createBioCDocumentWriter(
        					new OutputStreamWriter(
        							new FileOutputStream(outXML), "UTF-8"));

        	BioCCollection collection = reader.readCollectionInfo();

        	SentenceConverter converter = new SentenceConverter();
        	BioCCollection outCollection = converter.getCollection(collection);
            outCollection.setKey("sentence.key");
        	writer.writeCollectionInfo(outCollection);

            for ( BioCDocument document : reader ) {
            	BioCDocument outDocument = converter.getDocument(document);
            	writer.writeDocument(outDocument);
            }

            reader.close();
            writer.close();
        }
    
    public static void main(String[] args)
        throws XMLStreamException, IOException {

    	//if (args.length != 2) {
    	//    System.err.println("usage: java --jar SentenceSplit in.xml out.xml");
    	//    System.exit(-1);
    	//}

    	
        //split.split(args[0], args[1]);
    	PsgToSentXML convertor = new PsgToSentXML();
        String inXML = "/home/zhu/.gvfs/sftp on shannon/home/dongqing/data/bioc/articles/22792398.xml";
        String outXML = System.getProperty("user.dir") + "/data/out.xml";
        convertor.split(inXML, outXML);
        ArrayList<Sentence> sentences = convertor.getSentences();
        Sentence sentence;
    	for (int i = 0; i < sentences.size(); i++) {
    		sentence = sentences.get(i);
    		sentence.setOffset(sentence.getOffset());
    		System.out.print("psgOff:" + sentence.getPsgOffset() + " off:" + sentence.getOffset() + " len:" + sentence.getLength() + " ");
    		System.out.println(sentence.getText());
    	}
        
    }

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
}

