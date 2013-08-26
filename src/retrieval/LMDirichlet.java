package retrieval;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import type.Query;
import utility.SentProcessor;
import utility.Stopper;

public class LMDirichlet extends IndriMakeQuery {

	public LMDirichlet(int count, int mu, Collection<Query> queries) throws IOException {
		setCount(count);
		setRule("mu:" + Integer.toString(mu));
		setTrecFormat(true);
		setQueries(queries);
		makeQueries();
	}

	public void makeQueries() throws IOException {
		Stopper stop = new Stopper();
		HashSet<String> stopwords = stop.getStopwords();
		String tokenSentence = null;
		String[] tokens = null;
		StringBuffer queryBuffer = null;
		for (Query query : queries) {
			//System.out.println(query.getId());
			tokenSentence = query.getTokenSentence();
			//System.out.println(tokenSentence);
			tokens = tokenSentence.split(" ");
			queryBuffer = new StringBuffer();
			for (String token : tokens) {
				if (stopwords != null && stopwords.contains(token)) {
					continue;
				}
				queryBuffer.append(token);
				queryBuffer.append(" ");
			}
			query.setQuery(queryBuffer.toString().trim());	
		}
	}
}
