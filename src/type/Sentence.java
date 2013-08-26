package type;

//import java.util.ArrayList;

public class Sentence {
	protected String id;
	protected String text;
	private String gene;
	protected int psgOffset;
	protected int offset;
	protected int length;
	private GeneOntology go;
	protected String psgType = null;
	//private ArrayList<GeneOntology> goes;
	protected String type;
	
	public Sentence() {
		//setId(offset+"");
	}
	
	public Sentence(int psgOffset, int offset, String text) {
		setPsgOffset(psgOffset);
		setOffset(offset);
		setText(text);
		//setId(offset+"");
		//setGoes(new ArrayList<GeneOntology>());
	}
	
	public int getPsgOffset() {
		return psgOffset;
	}

	public void setPsgOffset(int v) {
		psgOffset = v;
	}	

	public int getOffset() {
		return offset;
	}

	public void setOffset(int v) {
		offset = v;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getLength() {
		return length;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setId() {
		this.id = offset + "";
	}
	/*
	public void addGo(GeneOntology go) {
		goes.add(go);
	}
	
	public void setGoes(ArrayList<GeneOntology> goes) {
		this.goes = goes;
	}
	*/
	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public GeneOntology getGo() {
		return go;
	}

	public void setGo(GeneOntology go) {
		this.go = go;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPsgType() {
		return psgType;
	}

	public void setPsgType(String psgType) {
		this.psgType = psgType;
	}	
}
