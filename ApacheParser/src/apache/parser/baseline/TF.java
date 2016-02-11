package apache.parser.baseline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class TF {
	public LinkedHashMap<String, ArrayList<Double>> gethighTF() throws IOException{
		WordList wl = new WordList();
		ArrayList<String> wordlist = wl.getAllTerms();
		
		LinkedHashMap<String, ArrayList<Double>> tf_high_all = new LinkedHashMap<String, ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new FileReader("req_all_stem.txt"));
		String line;
		while((line = br.readLine()) != null){
			ArrayList<Double> tfvector = new ArrayList<Double>(Collections.nCopies(wordlist.size(), 0.0));
			String key = line.split(">>>")[0];
			String desc = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ");
			String[] words = desc.split("\\s+");
			HashSet<String> uniquewordeachdoc = new HashSet<String>();
			for(String s : words){
				uniquewordeachdoc.add(s);
			}
			
			for(String s : uniquewordeachdoc){
				double termfreq = 0;
				for(String w : words){
					if(s.equals(w)) termfreq++;
				}
				
				int termIndex = wordlist.indexOf(s);
				if(termIndex == -1)
					System.out.println(s);
				//System.out.println(s);
				tfvector.set(termIndex, termfreq);
			}
			
			tf_high_all.put(key, tfvector);
		}
		
		return tf_high_all;
	}
	
	public LinkedHashMap<String, ArrayList<Double>> getlowTF() throws IOException{
		WordList wl = new WordList();
		ArrayList<String> wordlist = wl.getAllTerms();
		//if(wordlist.contains("configur")) System.out.println("Yes");
		//else System.out.println("No");
		LinkedHashMap<String, ArrayList<Double>> tf_low_all = new LinkedHashMap<String, ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new FileReader("uc_all_stem.txt"));
		String line;
		while((line = br.readLine()) != null){
			ArrayList<Double> tfvector = new ArrayList<Double>(Collections.nCopies(wordlist.size(), 0.0));
			String key = line.split(">>>")[0];
			String desc = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ");
			String[] words = desc.split("\\s+");
			HashSet<String> uniquewordeachdoc = new HashSet<String>();
			for(String s : words){
				uniquewordeachdoc.add(s);
			}
			
			for(String s : uniquewordeachdoc){
				double termfreq = 0;
				for(String w : words){
					if(s.equals(w)) termfreq++;
				}
				
				int termIndex = wordlist.indexOf(s);
				if(termIndex == -1)
				System.out.println("oh " + s);
				tfvector.set(termIndex, termfreq);
			}
			
			tf_low_all.put(key, tfvector);
		}
		
		return tf_low_all;
	}
}
