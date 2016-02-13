package apache.parser.baseline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class IDF {
	private int totaldocnum; 
	ArrayList<HashSet<String>> documents = new ArrayList<HashSet<String>>();
	ArrayList<String> wordlist;
	public IDF(int totaldocnum, ArrayList<String> wordlist) throws IOException{ 
		BufferedReader br = new BufferedReader(new FileReader("classes.txt"));
		String line;
		
		while((line = br.readLine()) != null){
			String desc = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ");
			String[] words = desc.split(" ");
			HashSet<String> uniquewordeachdoc = new HashSet<String>();
			for(String s : words){
				uniquewordeachdoc.add(s);
			}
			documents.add(uniquewordeachdoc);
		}
		
		br.close();
		
		br = new BufferedReader(new FileReader("req.txt"));
		while((line = br.readLine()) != null){
			String desc = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ");
			String[] words = desc.split(" ");
			HashSet<String> uniquewordeachdoc = new HashSet<String>();
			for(String s : words){
				uniquewordeachdoc.add(s);
			}
			documents.add(uniquewordeachdoc);
		}
		
		br.close();
		
		this.totaldocnum = totaldocnum;	
		this.wordlist = wordlist;
	}
	
	public LinkedHashMap<String, ArrayList<Double>> gethighIDF() throws IOException{ 
		LinkedHashMap<String, ArrayList<Double>> idf_high_all = new LinkedHashMap<String, ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new FileReader("req.txt"));
		String line;
		while((line = br.readLine()) != null){
			ArrayList<Double> idfvector = new ArrayList<Double>(Collections.nCopies(wordlist.size(), 0.0));
			String key = line.split(">>>")[0];
			String desc = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ");
			String[] words = desc.split("\\s+");
			HashSet<String> uniquewordeachdoc = new HashSet<String>();
			for(String s : words){
				uniquewordeachdoc.add(s);
			}
			
			for(String s : uniquewordeachdoc){
				int docTermCount = 0;
				for(HashSet<String> set : documents){
					if(set.contains(s)) docTermCount++; 
				}
				
				if(docTermCount != 0){
					double idf = Math.log((double)totaldocnum / (double)docTermCount);
					int termIndex = wordlist.indexOf(s);
					idfvector.set(termIndex, idf);
				}
			}
			
			idf_high_all.put(key, idfvector);
		}
		
		return idf_high_all;
	}
	
	public LinkedHashMap<String, ArrayList<Double>> getlowIDF() throws IOException{ 
		LinkedHashMap<String, ArrayList<Double>> idf_low_all = new LinkedHashMap<String, ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new FileReader("classes.txt"));
		String line;
		while((line = br.readLine()) != null){
			ArrayList<Double> idfvector = new ArrayList<Double>(Collections.nCopies(wordlist.size(), 0.0));
			String key = line.split(">>>")[0];
			String desc = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ");
			String[] words = desc.split("\\s+");
			HashSet<String> uniquewordeachdoc = new HashSet<String>();
			for(String s : words){
				uniquewordeachdoc.add(s);
			}
			
			for(String s : uniquewordeachdoc){
				int docTermCount = 0;
				for(HashSet<String> set : documents){
					if(set.contains(s)) docTermCount++; 
				}
				
				if(docTermCount != 0){
					double idf = Math.log((double)totaldocnum / (double)docTermCount);
					int termIndex = wordlist.indexOf(s);
					idfvector.set(termIndex, idf);
				}
			}
			
			idf_low_all.put(key, idfvector);
		}
		
		return idf_low_all;
	}
}


