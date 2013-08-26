package retrieval;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

import type.Query;


public abstract class IndriMakeQuery {
	public String rule;
	public ArrayList<String> indexes = new ArrayList<String>();
	public int count;
	public boolean trecFormat = true;
	public boolean stop = true;
	public Set<String> stoplist = new HashSet<String>();
	public Collection<Query> queries = new ArrayList<Query>();
	
	public void setQueries(Collection<Query> queries) {
		this.queries = queries;	
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public void setTrecFormat(boolean trecFormat) {
		this.trecFormat = trecFormat;
	}
	
	public String formHeader() {
		StringBuffer header = new StringBuffer();
		header.append("<parameters>\n");
		for (String index : indexes) {
			header.append("<index>");
			header.append(index);
			header.append("</index>\n");
		}
		header.append("<trecFormat>");
		header.append(trecFormat);
		header.append("</trecFormat>\n<count>");
		header.append(Integer.toString(count));
		header.append("</count>\n<rule>");
		header.append(rule);
		header.append("</rule>\n");
		return header.toString();
	}
	
	public void addIndex(String index) {
		indexes.add(index);
	}
	
	public ArrayList<String> getIndexes() {
		return indexes;
	}

	public void setIndexes(ArrayList<String> indexes) {
		this.indexes = indexes;
	}
		
	public String formTail() {
		return "</parameters>\n";
	}

	public String wrapQuery(Query query) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("<query>\n<number>");
		queryBuffer.append(query.getId());
		queryBuffer.append("</number>\n<text>");
		queryBuffer.append(query.getQuery());
		queryBuffer.append("</text>\n");
		HashSet<String> workingSet = query.getWorkingset();
		Iterator<String> iter = workingSet.iterator();
		while (iter.hasNext()) {
			queryBuffer.append("<workingSetDocno>");
			queryBuffer.append(iter.next());
			queryBuffer.append("</workingSetDocno>\n");
		}
		queryBuffer.append("</query>\n");
		return queryBuffer.toString();
	}
	
	public void writeParam(String filepath) throws FileNotFoundException {
		PrintStream ps = new PrintStream(filepath);
		ps.print(formHeader());
		for (Query query : queries) {
			ps.print(wrapQuery(query));
		}		
		ps.print(formTail());
		ps.close();
	}
	
	public Collection<Query> getQueries() {
		return queries;
	}
	
	public abstract void makeQueries() throws IOException;
}
