package apache.parser.baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class WordList {
	public ArrayList<String> getAllTerms() throws IOException{

		HashSet<String> uniqueterms = new HashSet<String>();
		File highfile = new File("req_all_stem.txt");
		File lowfile = new File("uc_all_stem.txt");
		String line;
		BufferedReader br = new BufferedReader(new FileReader(highfile));
		while((line = br.readLine()) != null){
			//String highleveldesc = line.substring(line.lastIndexOf(">") + 1).replaceAll("[.,:;()\"']", " ");
			String[] terms = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ").split("\\s+");
			//String[] terms = line.split(">>>")[1].replaceAll("[.,:;()\"'!@#$%^&*_+-=<>?]", " ").split("\\s+");
			for(String s : terms){
				
				uniqueterms.add(s);
			}
		}
		
		br.close();
		br = new BufferedReader(new FileReader(lowfile));
		while((line = br.readLine()) != null){
			//String lowleveldesc = line.substring(line.lastIndexOf(">") + 1).replaceAll("[.,:;()\"']", " ");
			String[] terms = line.split(">>>")[1].replaceAll("[.,:;()\"']", " ").split("\\s+");
			//String[] terms = line.split(">>>")[1].replaceAll("[.,:;()\"'!@#$%^&*_+-=<>?]", " ").split("\\s+");
			for(String s : terms){
				/*if(s.equals("configur"))
					System.out.println("Yes configur");
				else
					System.out.println("No configur");*/
				uniqueterms.add(s);
			}
		}
		
		ArrayList<String> wordlist = new ArrayList<String>(uniqueterms);
		br.close();
		return wordlist;
	}
}
