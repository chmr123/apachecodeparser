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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apache.parser.tfidf.StanfordLemmatizer;


public class PickRequirement {
	static Stemmer stemmer = new Stemmer();
	static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
	public static void main(String[] args) throws IOException {
		
		String termlistfile = null;
		String vbfolder = null;
		String pickedfilefolder = null;
		
		 for(int i = 0;i < args.length;i++) {
		
		       if ("-t".equals(args[i])) {
		    	   termlistfile = args[i+1];
		    	  i++;
		      } else if ("-f".equals(args[i])) {
		    	  vbfolder = args[i+1];
		          i++;
		      } else if ("-p".equals(args[i])){
		    	  pickedfilefolder = args[i+1];
		    	  i++;
		      }
		    }
		 
		File[] vbfiles = new File(vbfolder).listFiles();
		File termlist = new File(termlistfile);
		
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
			Set<String> picked = new HashSet<String>();
			ArrayList<String> topterms = termlist_map.get(f.getName());
			BufferedReader br1 = new BufferedReader(new FileReader(f));
			while((line = br1.readLine()) != null){
				line = lemmatizeVB(line);
				List<String> list = new LinkedList<String>(Arrays.asList(line.split("\\s+")));
				String verb = list.get(0);
				if(!isAlpha(verb)) continue;
				Iterator<String> iter = list.iterator();
				while (iter.hasNext()) {
				   String s = iter.next(); // must be called before you can call i.remove()
				   if(!isAlpha(s)){
					   iter.remove();
				   }
				   // Do something   
				}
				
				String newline = "";
				for(String s : list){
					newline = newline + s + " ";
				}
				for(String term : topterms){
					if(list.contains(term)){
						picked.add(newline);
						break;
					}
				}
			}
			br1.close();
			if(picked.size() != 0){
				FileWriter fw = new FileWriter(pickedfilefolder + "\\" + f.getName());
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
	
	private static String lemmatizeVB(String sentence){
		String[] split = sentence.split("\\s+");
		String lemma = "";
		for(String s : split){
			lemma = lemma + lemmatizer.lemmatize(s) + " ";
		}
		return lemma;
	}

	public static boolean isAlpha(String name) {
	    return name.matches("[a-zA-Z]+");
	}
}
