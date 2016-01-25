package apache.parser.pick;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PickRequirement {
	static Stemmer stemmer = new Stemmer();
	public static void main(String[] args) throws IOException {
		File[] vbfiles = new File("vb").listFiles();
		File termlist = new File("termlist.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(termlist));
		String line;
		Map<String, ArrayList<String>> termlist_map = new HashMap<String, ArrayList<String>>();
		while((line = br.readLine()) != null){
			String[] split = line.split(":");
			String file = split[0];
			String terms = split[1];
			String[] term_split = terms.split(", ");
			ArrayList<String> topterm = new ArrayList<String>();
			for(String t : term_split){
				topterm.add(t);
			}
			termlist_map.put(file, topterm);
		}
		
		int i = 0;
		for(File f : vbfiles){
			System.out.println("Picking file " + ++i + ": " + f.getName());
			ArrayList<String> picked = new ArrayList<String>();
			ArrayList<String> topterms = termlist_map.get(f.getName());
			BufferedReader br1 = new BufferedReader(new FileReader(f));
			while((line = br1.readLine()) != null){
				line = stemVB(line);
				List<String> list = Arrays.asList(line.split("\\s+"));
				for(String term : topterms){
					if(list.contains(term)){
						picked.add(line);
						break;
					}
				}
			}
			br1.close();
			if(picked.size() != 0){
				FileWriter fw = new FileWriter("picked\\" + f.getName());
				for(String p : picked){
					fw.write(p + "\n");
				}
				fw.flush();
				fw.close();
			}
		}
	}
	
	private static String stem(String term){
		char[] chars = term.toCharArray();
		for (char c : chars)
			stemmer.add(c);
		stemmer.stem();
		String stemmed = stemmer.toString();
		return stemmed;
	}
	
	private static String stemVB(String sentence){
		String[] split = sentence.split("\\s+");
		String stemmed = "";
		for(String s : split){
			stemmed = stemmed + stem(s) + " ";
		}
		return stemmed;
	}

}
