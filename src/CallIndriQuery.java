import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CallIndriQuery {

	/**
	 * 
	 * @param r
	 * @param s
	 * @throws IOException
	 */
	private static void compileScripts(Runtime r, String s) throws IOException {
		Process proc;
		proc = r.exec(s);
		//IndriRunQuery data/queries/21127054.param > data/results/21127054.result
		//proc = r.getRuntime().exec(new String[]{"/usr/bin/make"});
		// print out STDOUT
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line = br.readLine();
		while (line != null) {
			System.out.println(line);
			line = br.readLine();
		}
		br.close();

		// print out STDERR
		br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		line = br.readLine();
		while (line != null) {
			//information from System.out is included into System.err.
			//System.out.println(line);
			System.err.println(line);
			line = br.readLine();
		}
		try {
			int exit = proc.waitFor();
			//information from System.out is included into System.err.
			//System.out.println("exit code="+exit);
			System.err.println("exit code="+exit);
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String pmid = args[0].substring(args[0].lastIndexOf("/")+1,args[0].indexOf("param")-1);
		Runtime runtime = Runtime.getRuntime();
		String makeCom = "IndriRunQuery "+args[0]+" > data/results/"+pmid+".result"; 
		System.out.println(makeCom);
		CallIndriQuery.compileScripts(runtime, makeCom);
	}

}
