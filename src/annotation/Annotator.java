package annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import type.Query;
import type.Triple;

public abstract class Annotator {
	protected HashMap<String, ArrayList<Triple>> pmidToTriples;
	protected boolean print;
	
	public abstract void annotate(String resultPath, String submissionPath,
			Annotator annotator, String articlePmid, int limit, int top) throws IOException;

	public HashMap<String, ArrayList<Triple>> getPmidToTriples() {
		return pmidToTriples;
	}

	public void setPmidToTriples(HashMap<String, ArrayList<Triple>> pmidToTriples) {
		this.pmidToTriples = pmidToTriples;
	}
	
	public void printInfo(boolean print) {
		this.print = print;
	}
	
}
