package type;

public class ScorePR {
	private double precision, recall;
	private int total, correct, goldTotal;
	private int offset;
	private String pmid;

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getGoldTotal() {
		return goldTotal;
	}

	public void setGoldTotal(int goldTotal) {
		this.goldTotal = goldTotal;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}
	
	public void compute() {
		precision = (double) correct/total;
		recall = (double) correct/goldTotal;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getPmid() {
		return pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}
	
}
